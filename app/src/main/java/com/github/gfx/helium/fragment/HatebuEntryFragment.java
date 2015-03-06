package com.github.gfx.helium.fragment;

import com.github.gfx.helium.R;
import com.github.gfx.helium.analytics.TrackingUtils;
import com.github.gfx.helium.api.HatebuFeedClient;
import com.github.gfx.helium.api.HttpClientHolder;
import com.github.gfx.helium.model.HatebuEntry;

import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
public class HatebuEntryFragment extends Fragment
        implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    static final String TAG = HatebuEntryFragment.class.getSimpleName();

    static final String kHatebuEntryPrefix = "http://b.hatena.ne.jp/entry/";

    static final String kCategory = "category";

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

    @InjectView(android.R.id.list)
    AbsListView listView;

    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    HatebuFeedClient feedClient;

    ArrayAdapter<HatebuEntry> adapter;

    public HatebuEntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new EntriesAdapter(getActivity());

        feedClient = new HatebuFeedClient(HttpClientHolder.CLIENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        ButterKnife.inject(this, view);

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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        reload().subscribe();

        String category = getCategory();
        TrackingUtils.sendScreenView(getActivity(), category != null ? TAG + "-" + category : TAG);
    }

    Observable<?> reload() {
        Log.d(TAG, "reload for " + this);

        Observable<List<HatebuEntry>> observable;
        if (getCategory() != null) {
            observable = feedClient.getHotentries(getCategory());
        } else {
            observable = feedClient.getHotentries();
        }
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<HatebuEntry>>() {
                    @Override
                    public void call(List<HatebuEntry> items) {
                        adapter.clear();
                        adapter.addAll(items);
                    }
                }).onErrorReturn(new Func1<Throwable, List<HatebuEntry>>() {
                    @Override
                    public List<HatebuEntry> call(Throwable throwable) {
                        Log.wtf(TAG, "Error while loading entries: " + throwable);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HatebuEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        trackOpenUrl("original");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        HatebuEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(kHatebuEntryPrefix + entry.link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        trackOpenUrl("service");
        return true;
    }

    void trackOpenUrl(String action) {
        String category = getCategory();
        TrackingUtils
                .sendEvent(getActivity(), category != null ? TAG + "-" + category : TAG, action);
    }

    private class EntriesAdapter extends ArrayAdapter<HatebuEntry> {

        public EntriesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.card_hatebu_entry, parent, false);
                convertView.setTag(new ViewHolder());
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            ButterKnife.inject(viewHolder, convertView);

            HatebuEntry entry = getItem(position);

            viewHolder.title.setText(entry.title);
            viewHolder.date.setText(ISODateTimeFormat.date().print(entry.getTimestamp()));
            viewHolder.subject.setText(entry.subject);
            viewHolder.bookmarkCount.setText(entry.bookmarkCount);
            viewHolder.description.setText(entry.description);

            setTextMask(viewHolder.description);

            return convertView;
        }
    }

    static void setTextMask(final TextView view) {
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            Shader textShader = new LinearGradient(0, 0, 0, view.getHeight(),
                                    new int[]{Color.BLACK, Color.TRANSPARENT},
                                    new float[]{0, 1}, Shader.TileMode.CLAMP);
                            view.getPaint().setShader(textShader);
                        }
                    });
        }
    }

    class ViewHolder {

        @InjectView(R.id.title)
        TextView title;

        @InjectView(R.id.description)
        TextView description;

        @InjectView(R.id.bookmark_count)
        TextView bookmarkCount;

        @InjectView(R.id.subject)
        TextView subject;

        @InjectView(R.id.date)
        TextView date;
    }
}
