package com.github.gfx.helium.fragment;

import com.google.android.gms.analytics.Tracker;

import com.bumptech.glide.Glide;
import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription;
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.analytics.TrackingUtils;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.CardTimelineEntryBinding;
import com.github.gfx.helium.databinding.FragmentEntryBinding;
import com.github.gfx.helium.model.HatebuEntry;
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

    @Inject
    HatenaClient hatenaClient;

    @Inject
    Tracker tracker;

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

    int currentPage;

    final HatebuEntry emptyEntry = new HatebuEntry();

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
            TrackingUtils.sendScreenView(tracker, TAG);
        }
    }

    Observable<List<HatebuEntry>> reload() {
        currentPage = 1;
        Observable<List<HatebuEntry>> observable = hatenaClient.getFavotites(username, currentPage);
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
        hatenaClient.getFavotites(username, ++currentPage)
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
        TrackingUtils.sendEvent(tracker, TAG, action);
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

            Glide.with(getContext())
                    .load(hatenaClient.buildHatebuIconUri(entry.creator))
                    .into(binding.author);


            if (!TextUtils.isEmpty(entry.title)) {
                binding.title.setText(entry.title);
                binding.title.setVisibility(View.VISIBLE);
            } else {
                binding.title.setVisibility(View.GONE);
            }

            if (!entry.subject.isEmpty()) {
                binding.tags.setText(TextUtils.join(" ", Observable.from(entry.subject).map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return "#" + s;
                    }
                }).toList().toBlocking().first()));
                binding.tags.setVisibility(View.VISIBLE);
            } else {
                binding.tags.setVisibility(View.GONE);
            }

            binding.date.setText(entry.getTimestamp());
            binding.bookmarkCount.setText(entry.bookmarkCount);
            binding.description.setText(entry.description);
            binding.originalUrl.setText(entry.link);

            binding.bookmarkCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchOnItemLongClick(v, position);
                }
            });
        }
    }
}
