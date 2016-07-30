package com.github.gfx.helium.fragment;

import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.api.HatenaClient;
import com.github.gfx.helium.databinding.CardHatebuEntryBinding;
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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import rx.Single;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@ParametersAreNonnullByDefault
public class HatebuEntryFragment extends Fragment
        implements OnItemClickListener<HatebuEntry>, OnItemLongClickListener<HatebuEntry> {

    static final String TAG = HatebuEntryFragment.class.getSimpleName();

    static final String kCategory = "category";

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

    @Inject
    LayoutManagers layoutManagers;

    FragmentEntryBinding binding;

    EntriesAdapter adapter;

    public HatebuEntryFragment() {
    }

    public static HatebuEntryFragment newInstance() {
        HatebuEntryFragment fragment = new HatebuEntryFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public static HatebuEntryFragment newInstance(String category) {
        HatebuEntryFragment fragment = new HatebuEntryFragment();
        Bundle args = new Bundle();
        args.putString(kCategory, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        HeliumApplication.getComponent(this).inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new EntriesAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        for (int i = 0, max = layoutManagers.getSpanCount(); i < max; i++) {
            adapter.addItem(emptyEntry);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentEntryBinding.inflate(inflater, container, false);

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(layoutManagers.create());

        binding.swipeRefresh.setColorSchemeResources(R.color.app_primary);
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Subscription subscription = reload()
                        .doOnUnsubscribe(new Action0() {
                            @Override
                            public void call() {
                                binding.swipeRefresh.setRefreshing(false);
                            }
                        })
                        .subscribe(new Action1<List<HatebuEntry>>() {
                            @Override
                            public void call(List<HatebuEntry> items) {
                                adapter.reset(items);
                            }
                        });
                compositeSubscription.add(subscription);
            }
        });

        Subscription subscription = reload().subscribe(new Action1<List<HatebuEntry>>() {
            @Override
            public void call(List<HatebuEntry> items) {
                adapter.reset(items);
            }
        });
        compositeSubscription.add(subscription);

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

        if (isVisibleToUser && isAdded()) {
            String category = getCategory();
            tracker.sendScreenView(category != null ? TAG + "-" + category : TAG);
        }
    }

    Single<List<HatebuEntry>> reload() {
        Single<List<HatebuEntry>> observable;
        if (getCategory() != null) {
            observable = hatenaClient.getHotentries(getCategory());
        } else {
            observable = hatenaClient.getHotentries();
        }
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, List<HatebuEntry>>() {
                    @Override
                    public List<HatebuEntry> call(Throwable throwable) {
                        Log.w(TAG, "Error while loading entries", throwable);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Error while loading entries\n"
                                            + throwable.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        return Collections.emptyList();
                    }
                });
    }

    @Nullable
    String getCategory() {
        return getArguments() != null ? getArguments().getString(kCategory) : null;
    }

    void openUri(Uri uri, String action) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        trackOpenUri(action);
    }

    void trackOpenUri(String action) {
        String category = getCategory();
        tracker.sendEvent(category != null ? TAG + "-" + category : TAG, action);
    }

    @Override
    public void onItemClick(View view, HatebuEntry entry) {
        openUri(Uri.parse(entry.link), "original");
    }

    @Override
    public boolean onItemLongClick(View view, HatebuEntry entry) {
        openUri(hatenaClient.buildHatebuEntryUri(entry.link), "service");
        return true;
    }

    private class EntriesAdapter extends ArrayRecyclerAdapter<HatebuEntry, BindingHolder<CardHatebuEntryBinding>> {

        public EntriesAdapter(Context context) {
            super(context);
        }

        @Override
        public BindingHolder<CardHatebuEntryBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.card_hatebu_entry);
        }

        @Override
        public void onBindViewHolder(final BindingHolder<CardHatebuEntryBinding> holder, final int position) {
            CardHatebuEntryBinding binding = holder.binding;

            final HatebuEntry entry = getItem(position);
            if (entry == emptyEntry) {
                loadingAnimation.start(binding.getRoot());
                return;
            }
            loadingAnimation.cancel(binding.getRoot());

            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchOnItemClick(v, entry);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return dispatchOnItemLongClick(v, entry);
                }
            });

            binding.title.setText(entry.title);
            binding.date.setText(entry.getTimestamp());
            binding.subject.setText(TextUtils.join(" ", entry.subject));
            binding.bookmarkCount.setText(entry.bookmarkCount);
            binding.description.setText(entry.description);
            binding.originalUrl.setText(entry.link);

            binding.bookmarkCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchOnItemLongClick(v, entry);
                }
            });
        }
    }
}
