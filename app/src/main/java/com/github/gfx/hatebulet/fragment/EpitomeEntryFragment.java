package com.github.gfx.hatebulet.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gfx.hatebulet.R;
import com.github.gfx.hatebulet.api.EpitomeFeedClient;
import com.github.gfx.hatebulet.api.HttpClientHolder;
import com.github.gfx.hatebulet.model.EpitomeEntry;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.functions.Action1;

public class EpitomeEntryFragment extends Fragment implements AbsListView.OnItemClickListener {
    static final String TAG = EpitomeEntry.class.getSimpleName();

    @InjectView(android.R.id.list)
    AbsListView listView;

    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    EntriesAdapter adapter;

    EpitomeFeedClient feedClient;

    public EpitomeEntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new EntriesAdapter(getActivity());
        feedClient = new EpitomeFeedClient(HttpClientHolder.CLIENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        ButterKnife.inject(this, view);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

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
    }

    Observable<?> reload() {
        return feedClient.getEntries()
                .doOnNext(new Action1<List<EpitomeEntry>>() {
                    @Override
                    public void call(List<EpitomeEntry> entries) {
                        adapter.clear();
                        adapter.addAll(entries);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.wtf(TAG, "Error while loading entries: " + throwable);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Error while loading entries", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EpitomeEntry entry = adapter.getItem(position);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.epitomeUrl));
        startActivity(intent);
    }

    static class EntriesAdapter extends ArrayAdapter<EpitomeEntry> {

        public EntriesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_epitome_entry, parent, false);
                convertView.setTag(new ViewHolder());
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            ButterKnife.inject(viewHolder, convertView);

            EpitomeEntry entry = getItem(position);
            viewHolder.title.setText(entry.title);
            viewHolder.date.setText(entry.publishedAt);
            viewHolder.views.setText(Integer.toString(entry.views));

            fillGists(viewHolder.gists, entry.gists);

            return convertView;
        }

        void fillGists(LinearLayout layout, List<EpitomeEntry.Gist> gists) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            int height = 0;

            for (int i = 0; i < gists.size(); i++) {
                View view = inflater.inflate(R.layout.item_gist, layout, false);

                GistViewHolder vh = new GistViewHolder();
                ButterKnife.inject(vh, view);

                vh.point.setText(Integer.toString(i + 1));
                vh.text.setText(gists.get(i).content);

                view.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                height += view.getMeasuredHeight();

                layout.addView(view);
            }

            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = height;
            layout.setLayoutParams(params);
        }

        void adjustHeight(ListView listView) {
            int h = 0;

            for (int i = 0; i < listView.getCount(); i++) {
                View view = listView.getAdapter().getView(i, null, listView);
                view.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                h += view.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = h + (listView.getDividerHeight() * (listView.getAdapter().getCount() - 1));
            listView.setLayoutParams(params);
        }

        static class ViewHolder {
            @InjectView(R.id.title)
            TextView title;

            @InjectView(R.id.views)
            TextView views;

            @InjectView(R.id.published_date)
            TextView date;

            @InjectView(R.id.gists)
            LinearLayout gists;
        }
    }

    static class GistsAdapter extends ArrayAdapter<EpitomeEntry.Gist> {

        public GistsAdapter(Context context, List<EpitomeEntry.Gist> gists) {
            super(context, 0, gists);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gist, parent, false);
                convertView.setTag(new GistViewHolder());
            }

            GistViewHolder viewHolder = (GistViewHolder) convertView.getTag();
            ButterKnife.inject(viewHolder, convertView);

            EpitomeEntry.Gist gist = getItem(position);
            viewHolder.point.setText(Integer.toString(position + 1));
            viewHolder.text.setText(gist.content);

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }

    static class GistViewHolder {
        @InjectView(R.id.gist_point)
        TextView point;

        @InjectView(R.id.gist_text)
        TextView text;
    }
}
