package com.github.gfx.helium.fragment;

import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription;
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers;
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;
import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.util.AppTracker;
import com.github.gfx.helium.api.EpitomeClient;
import com.github.gfx.helium.databinding.CardEpitomeEntryBinding;
import com.github.gfx.helium.databinding.FragmentEntryBinding;
import com.github.gfx.helium.databinding.ItemEpitomeGistBinding;
import com.github.gfx.helium.model.EpitomeEntry;
import com.github.gfx.helium.util.LoadingAnimation;
import com.github.gfx.helium.util.ViewSwitcher;
import com.github.gfx.helium.widget.ArrayRecyclerAdapter;
import com.github.gfx.helium.widget.BindingHolder;
import com.github.gfx.helium.widget.LayoutManagers;
import com.github.gfx.helium.widget.OnItemClickListener;
import com.github.gfx.helium.widget.OnItemLongClickListener;

import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
public class EpitomeEntryFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    static final String TAG = EpitomeEntryFragment.class.getSimpleName();

    @Inject
    EpitomeClient epitomeClient;

    @Inject
    AppTracker tracker;

    @Inject
    AndroidCompositeSubscription compositeSubscription;

    @Inject
    ViewSwitcher viewSwitcher;

    @Inject
    LoadingAnimation loadingAnimation;

    FragmentEntryBinding binding;

    EntriesAdapter adapter;

    LayoutManagers layoutManagers;

    EpitomeEntry emptyEntry = new EpitomeEntry();

    public EpitomeEntryFragment() {
    }

    public static EpitomeEntryFragment newInstance() {
        EpitomeEntryFragment fragment = new EpitomeEntryFragment();
        fragment.setArguments(new Bundle());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry, container, false);

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(layoutManagers.create());

        binding.swipeRefresh.setColorSchemeResources(R.color.app_primary);
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload().subscribe(new Action1<List<EpitomeEntry>>() {
                    @Override
                    public void call(List<EpitomeEntry> items) {
                        adapter.reset(items);
                        binding.swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        reload().subscribe(new Action1<List<EpitomeEntry>>() {
            @Override
            public void call(List<EpitomeEntry> items) {
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

    Observable<List<EpitomeEntry>> reload() {
        return epitomeClient.getEntries()
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new OperatorAddToCompositeSubscription<List<EpitomeEntry>>(compositeSubscription))
                .map(new Func1<List<EpitomeEntry>, List<EpitomeEntry>>() {
                    @Override
                    public List<EpitomeEntry> call(List<EpitomeEntry> items) {
                        List<EpitomeEntry> entriesWithKnownScheme = new ArrayList<>(items.size());
                        for (EpitomeEntry entry : items) {
                            if (entry.hasKnownScheme()) {
                                entriesWithKnownScheme.add(entry);
                            }
                        }
                        return entriesWithKnownScheme;
                    }
                })
                .onErrorReturn(new Func1<Throwable, List<EpitomeEntry>>() {
                    @Override
                    public List<EpitomeEntry> call(Throwable throwable) {
                        Log.w(TAG, "Error while loading entries", throwable);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Error while loading entries",
                                    Toast.LENGTH_LONG).show();
                        }
                        return Collections.emptyList();
                    }
                });
    }

    @Override
    public void onItemClick(View item, int position) {
        EpitomeEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.epitomeUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        tracker.sendEvent(TAG, "service");
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        EpitomeEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.upstreamUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        tracker.sendEvent(TAG, "original");
        return true;
    }

    private class EntriesAdapter extends ArrayRecyclerAdapter<EpitomeEntry, BindingHolder<CardEpitomeEntryBinding>> {

        EntriesAdapter(@NonNull Context context) {
            super(context);
        }

        @Override
        public BindingHolder<CardEpitomeEntryBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BindingHolder<>(getContext(), parent, R.layout.card_epitome_entry);
        }

        @Override
        public void onBindViewHolder(final BindingHolder<CardEpitomeEntryBinding> holder, final int position) {
            CardEpitomeEntryBinding binding = holder.binding;

            EpitomeEntry entry = getItem(position);

            if (entry == emptyEntry) {
                loadingAnimation.start(binding.getRoot());
                return;
            }
            loadingAnimation.cancel(binding.getRoot());

            holder.itemView.setClickable(true);
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

            if (entry.isGists()) {
                populateGists(holder, entry);
                holder.itemView.setVisibility(View.VISIBLE);
            } else {
                throw new IllegalStateException("Unknown scheme: " + entry.scheme);
            }
        }

        void populateGists(BindingHolder<CardEpitomeEntryBinding> holder, EpitomeEntry entry) {
            CardEpitomeEntryBinding binding = holder.binding;

            binding.title.setText(entry.title);
            binding.views.setText("閲覧数: " + Integer.toString(entry.views));
            binding.publishedDate.setText("投稿日: " + ISODateTimeFormat.date().print(entry.getTimestamp()));
            binding.originalUrl.setText(entry.upstreamUrl);

            fillGists(binding.gists, entry.gists);
        }

        void fillGists(LinearLayout layout, List<EpitomeEntry.Gist> gists) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            layout.removeAllViews();

            for (int i = 0; i < gists.size(); i++) {
                ItemEpitomeGistBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_epitome_gist, layout, false);

                binding.gistPoint.setText(Integer.toString(i + 1));
                binding.gistText.setText(gists.get(i).content);

                layout.addView(binding.getRoot());
            }
        }
    }
}
