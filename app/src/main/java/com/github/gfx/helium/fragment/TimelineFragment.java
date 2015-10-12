package com.github.gfx.helium.fragment;

import com.bumptech.glide.Glide;
import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription;
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.CardTimelineEntryBinding;
import com.github.gfx.helium.databinding.FragmentEntryBinding;
import com.github.gfx.helium.model.HatebuEntry;
import com.github.gfx.helium.util.AppTracker;
import com.github.gfx.helium.util.LoadingAnimation;
import com.github.gfx.helium.util.ViewSwitcher;
import com.github.gfx.helium.widget.ArrayRecyclerAdapter;
import com.github.gfx.helium.widget.BindingHolder;
import com.github.gfx.helium.widget.LayoutManagers;
import com.github.gfx.helium.widget.OnItemClickListener;
import com.github.gfx.helium.widget.OnItemLongClickListener;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * The timeline that shows what you like.
 */
@ParametersAreNonnullByDefault
public class TimelineFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    static final String TAG = TimelineFragment.class.getSimpleName();

    static final String kUsername = "username";

    final HatebuEntry emptyEntry = new HatebuEntry();

    @Inject
    HatenaClient hatenaClient;

    @Inject
    AppTracker tracker;

    @Inject
    AndroidCompositeSubscription compositeSubscription;

    @Inject
    ViewSwitcher viewSwitcher;

    @Inject
    LoadingAnimation loadingAnimation;

    LayoutManagers layoutManagers;

    FragmentEntryBinding binding;

    EntriesAdapter adapter;

    String username;

    int currentEntries;

    public TimelineFragment() {
    }

    public static TimelineFragment newInstance(String username) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(kUsername, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeliumApplication.getAppComponent().inject(this);

        layoutManagers = new LayoutManagers(getActivity());

        adapter = new EntriesAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        for (int i = 0, max = layoutManagers.getSpanCount(); i < max; i++) {
            adapter.addItem(emptyEntry);
        }

        username = getArguments().getString(kUsername);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry, container, false);

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(layoutManagers.create());
        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {
                    loadMore();
                }
            }
        });

        binding.swipeRefresh.setColorSchemeResources(R.color.app_primary);
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload().subscribe(new Action1<List<HatebuEntry>>() {
                    @Override
                    public void call(List<HatebuEntry> items) {
                        adapter.reset(items);
                        binding.swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });

        reload().subscribe(new Action1<List<HatebuEntry>>() {
            @Override
            public void call(List<HatebuEntry> items) {
                adapter.reset(items);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        compositeSubscription.unsubscribe();

        super.onStop();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            tracker.sendScreenView(TAG);
        }
    }

    Observable<List<HatebuEntry>> reload() {
        currentEntries = 0;
        Observable<List<HatebuEntry>> observable = hatenaClient.getFavotites(username, currentEntries);
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new OperatorAddToCompositeSubscription<List<HatebuEntry>>(compositeSubscription))
                .onErrorReturn(new Func1<Throwable, List<HatebuEntry>>() {
                    @Override
                    public List<HatebuEntry> call(Throwable e) {
                        reportError(e);
                        return Collections.emptyList();
                    }
                });
    }


    void loadMore() {
        currentEntries += adapter.getItemCount();
        hatenaClient.getFavotites(username, currentEntries)
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new OperatorAddToCompositeSubscription<List<HatebuEntry>>(compositeSubscription))
                .subscribe(new Subscriber<List<HatebuEntry>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        reportError(e);
                    }

                    @Override
                    public void onNext(List<HatebuEntry> items) {
                        adapter.addAllWithNotification(items);
                    }
                });
    }

    void reportError(Throwable e) {
        Log.w(TAG, "Error while loading entries", e);
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Error while loading entries\n"
                            + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        HatebuEntry entry = adapter.getItem(position);
        openUri(Uri.parse(entry.link), "original");
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        HatebuEntry entry = adapter.getItem(position);
        openUri(hatenaClient.buildHatebuEntryUri(entry.link), "service");
        return true;
    }

    void openUri(Uri uri, String action) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        trackOpenUri(action);
    }

    void trackOpenUri(String action) {
        tracker.sendEvent(TAG, action);
    }

    private class EntriesAdapter extends ArrayRecyclerAdapter<HatebuEntry, BindingHolder<CardTimelineEntryBinding>> {

        public EntriesAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public BindingHolder<CardTimelineEntryBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.card_timeline_entry);
        }

        @Override
        public void onBindViewHolder(final BindingHolder<CardTimelineEntryBinding> holder, final int position) {
            CardTimelineEntryBinding binding = holder.binding;

            HatebuEntry entry = getItem(position);

            if (entry == emptyEntry) {
                loadingAnimation.start(binding.getRoot());
                return;
            }
            loadingAnimation.cancel(binding.getRoot());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchOnItemClick(v, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return dispatchOnItemLongClick(v, position);
                }
            });

            binding.bookmarkCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchOnItemLongClick(v, position);
                }
            });

            Glide.with(getContext())
                    .load(hatenaClient.buildHatebuIconUri(entry.creator))
                    .into(binding.author);

            binding.title.setText(entry.title);

            viewSwitcher.setTextOrGone(binding.tags, entry.getTags());
            viewSwitcher.setTextOrGone(binding.comment, entry.description);

            binding.date.setText(entry.getTimestamp());
            binding.bookmarkCount.setText(entry.bookmarkCount);
            binding.originalUrl.setText(entry.link);

            CharSequence summary = entry.getSummary();
            if (!TextUtils.isEmpty(summary)) {
                binding.textSummary.setText(entry.getSummary());
                binding.layoutSummary.setVisibility(View.VISIBLE);
                binding.textSummary.setVisibility(View.VISIBLE);
                binding.imageSummary.setVisibility(View.GONE);
            } else if (entry.looksLikeImageUrl()) {
                Glide.with(getContext())
                        .load(entry.link)
                        .into(binding.imageSummary);
                binding.layoutSummary.setVisibility(View.VISIBLE);
                binding.textSummary.setVisibility(View.GONE);
                binding.imageSummary.setVisibility(View.VISIBLE);
            } else {
                binding.layoutSummary.setVisibility(View.GONE);
            }
        }
    }
}
