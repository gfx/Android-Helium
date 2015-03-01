package com.github.gfx.hatebulet.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.gfx.hatebulet.R;
import com.github.gfx.hatebulet.api.HatebuEntry;
import com.github.gfx.hatebulet.api.HatebuFeedClient;
import com.github.gfx.hatebulet.api.HttpClientHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

public class EntryFragment extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView listView;

    private ArrayAdapter<HatebuEntry> adapter;

    public EntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new EntriesAdapter(getActivity());

        HatebuFeedClient client = new HatebuFeedClient(HttpClientHolder.CLIENT);
        client.getHotentries().subscribe(new Action1<List<HatebuEntry>>() {
            @Override
            public void call(List<HatebuEntry> items) {
                adapter.addAll(items);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        listView = ButterKnife.findById(view, android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HatebuEntry entry = adapter.getItem(position);

        Uri uri = Uri.parse(entry.link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    class EntriesAdapter extends ArrayAdapter<HatebuEntry> {

        public EntriesAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.card_entry, parent, false);
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
