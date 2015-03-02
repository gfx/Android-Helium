package com.github.gfx.hatebulet.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
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

import com.github.gfx.hatebulet.R;
import com.github.gfx.hatebulet.api.HatebuFeedClient;
import com.github.gfx.hatebulet.api.HttpClientHolder;
import com.github.gfx.hatebulet.model.HatebuEntry;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.functions.Action1;

public class HatebuEntryFragment extends Fragment implements AbsListView.OnItemClickListener {
    static final String TAG = HatebuEntryFragment.class.getSimpleName();

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
    public void onResume() {
        super.onResume();

        swipeRefreshLayout.setRefreshing(true);
        reload().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HatebuEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    Observable<?> reload() {
        Log.d(TAG, "reload for " + this);
        return feedClient.getHotentries()
                .doOnNext(new Action1<List<HatebuEntry>>() {
                    @Override
                    public void call(List<HatebuEntry> items) {
                        adapter.clear();
                        adapter.addAll(items);
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.wtf(TAG, "Error while loading entries: " + throwable);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Error while loading entries", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private class EntriesAdapter extends ArrayAdapter<HatebuEntry> {

        public EntriesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.card_hatebu_entry, parent, false);
                convertView.setTag(new ViewHolder());
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            ButterKnife.inject(viewHolder, convertView);

            HatebuEntry entry = getItem(position);

            viewHolder.title.setText(entry.title);
            viewHolder.date.setText(entry.date);
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
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
