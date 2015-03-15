package com.github.gfx.helium.fragment;

import com.github.gfx.helium.R;
import com.github.gfx.helium.analytics.TrackingUtils;
import com.github.gfx.helium.api.EpitomeFeedClient;
import com.github.gfx.helium.api.HttpClientHolder;
import com.github.gfx.helium.model.EpitomeEntry;

import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

@ParametersAreNonnullByDefault
public class EpitomeEntryFragment extends Fragment
        implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    static final String TAG = EpitomeEntryFragment.class.getSimpleName();

    public static EpitomeEntryFragment newInstance() {
        EpitomeEntryFragment fragment = new EpitomeEntryFragment();
        fragment.setArguments(new Bundle());
        return fragment;

    }

    @InjectView(android.R.id.list)
    AbsListView listView;

    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(android.R.id.empty)
            TextView empty;

    EntriesAdapter adapter;

    EpitomeFeedClient feedClient;

    public EpitomeEntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new EntriesAdapter(getActivity());
        feedClient = new EpitomeFeedClient(getActivity(), HttpClientHolder.CLIENT);
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

        TrackingUtils.sendScreenView(getActivity(), TAG);
    }

    Observable<?> reload() {
        return AppObservable.bindFragment(this, feedClient.getEntries())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<EpitomeEntry>>() {
                    @Override
                    public void call(List<EpitomeEntry> entries) {
                        adapter.clear();
                        adapter.addAll(entries);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EpitomeEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.epitomeUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        TrackingUtils.sendEvent(getActivity(), TAG, "service");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        EpitomeEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.upstreamUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

        TrackingUtils.sendEvent(getActivity(), TAG, "original");
        return true;
    }

    static class EntriesAdapter extends ArrayAdapter<EpitomeEntry> {

        public EntriesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public void addAll(Collection<? extends EpitomeEntry> collection) {
            Iterator<? extends EpitomeEntry> iterator = Observable.from(collection)
                    .filter(new Func1<EpitomeEntry, Boolean>() {
                        @Override
                        public Boolean call(EpitomeEntry epitomeEntry) {
                            return epitomeEntry.hasKnownScheme();
                        }
                    })
                    .toBlocking()
                    .getIterator();

            while (iterator.hasNext()) {
                add(iterator.next());
            }
        }

        @Override
        public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.card_epitome_entry, parent, false);
                convertView.setTag(new ViewHolder());
            }

            EpitomeEntry entry = getItem(position);

            if (entry.isGists()) {
                setupSchemaGists(convertView, entry);
                convertView.setVisibility(View.VISIBLE);
            } else {
                throw new IllegalStateException("Unknown scheme: " + entry.scheme);
            }

            return convertView;
        }

        void setupSchemaGists(View view, EpitomeEntry entry) {

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            ButterKnife.inject(viewHolder, view);

            viewHolder.title.setText(entry.title);
            viewHolder.views.setText("閲覧数: " + Integer.toString(entry.views));
            viewHolder.date.setText("投稿日: " + ISODateTimeFormat.date().print(entry.getTimestamp()));
            viewHolder.originalUrl.setText(entry.upstreamUrl);

            fillGists(viewHolder.gists, entry.gists);
        }

        void fillGists(LinearLayout layout, List<EpitomeEntry.Gist> gists) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            layout.removeAllViews();

            for (int i = 0; i < gists.size(); i++) {
                View view = inflater.inflate(R.layout.item_epitome_gist, layout, false);

                GistViewHolder vh = new GistViewHolder();
                ButterKnife.inject(vh, view);

                vh.point.setText(Integer.toString(i + 1));
                vh.text.setText(gists.get(i).content);

                layout.addView(view);
            }
        }

        static class ViewHolder {

            @InjectView(R.id.title)
            TextView title;

            @InjectView(R.id.original_url)
            TextView originalUrl;

            @InjectView(R.id.views)
            TextView views;

            @InjectView(R.id.published_date)
            TextView date;

            @InjectView(R.id.gists)
            LinearLayout gists;
        }
    }

    static class GistViewHolder {

        @InjectView(R.id.gist_point)
        TextView point;

        @InjectView(R.id.gist_text)
        TextView text;
    }
}
