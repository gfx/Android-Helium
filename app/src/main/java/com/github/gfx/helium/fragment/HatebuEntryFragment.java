package com.github.gfx.helium.fragment;

import com.google.android.gms.analytics.Tracker;

import com.github.gfx.helium.HeliumApplication;
import com.github.gfx.helium.R;
import com.github.gfx.helium.analytics.TrackingUtils;
import com.github.gfx.helium.api.HatebuFeedClient;
import com.github.gfx.helium.databinding.CardHatebuEntryBinding;
import com.github.gfx.helium.model.HatebuEntry;

import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
public class HatebuEntryFragment extends Fragment
        implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    static final String TAG = HatebuEntryFragment.class.getSimpleName();

    static final String kHatebuEntryPrefix = "http://b.hatena.ne.jp/entry/";

    static final String kCategory = "category";

    @Bind(R.id.list)
    AbsListView listView;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    HatebuFeedClient feedClient;

    @Inject
    Tracker tracker;

    ArrayAdapter<HatebuEntry> adapter;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeliumApplication.getAppComponent().inject(this);

        adapter = new EntriesAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        ButterKnife.bind(this, view);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.app_primary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload().subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });

        reload().subscribe();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            String category = getCategory();
            TrackingUtils.sendScreenView(tracker, category != null ? TAG + "-" + category : TAG);
        }
    }

    Observable<?> reload() {
        Observable<List<HatebuEntry>> observable;
        if (getCategory() != null) {
            observable = feedClient.getHotentries(getCategory());
        } else {
            observable = feedClient.getHotentries();
        }
        return AppObservable.bindFragment(this, observable)
                .doOnNext(new Action1<List<HatebuEntry>>() {
                    @Override
                    public void call(List<HatebuEntry> items) {
                        adapter.clear();
                        adapter.addAll(items);
                    }
                }).onErrorReturn(new Func1<Throwable, List<HatebuEntry>>() {
                    @Override
                    public List<HatebuEntry> call(Throwable throwable) {
                        Log.w(TAG, "Error while loading entries", throwable);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Error while loading entries",
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

    void openUri(String uri, String action) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
        trackOpenUri(action);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HatebuEntry entry = adapter.getItem(position);
        openUri(entry.link, "original");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        HatebuEntry entry = adapter.getItem(position);
        openUri(kHatebuEntryPrefix + entry.link, "service");
        return true;
    }

    void trackOpenUri(String action) {
        String category = getCategory();
        TrackingUtils
                .sendEvent(tracker, category != null ? TAG + "-" + category : TAG, action);
    }

    private class EntriesAdapter extends ArrayAdapter<HatebuEntry> {

        public EntriesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                CardHatebuEntryBinding binding = DataBindingUtil.inflate(inflater, R.layout.card_hatebu_entry, parent, false);
                convertView = binding.getRoot();
            }

            CardHatebuEntryBinding binding = DataBindingUtil.getBinding(convertView);

            final HatebuEntry entry = getItem(position);

            binding.title.setText(entry.title);
            binding.date.setText(ISODateTimeFormat.date().print(entry.getTimestamp()));
            binding.subject.setText(TextUtils.join(" ", entry.subject));
            binding.bookmarkCount.setText(entry.bookmarkCount);
            binding.description.setText(entry.description);
            binding.originalUrl.setText(entry.link);

            binding.bookmarkCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUri(kHatebuEntryPrefix + entry.link, "service");
                }
            });

            return convertView;
        }
    }
}
