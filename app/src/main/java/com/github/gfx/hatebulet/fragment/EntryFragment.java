package com.github.gfx.hatebulet.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.gfx.hatebulet.R;
import com.github.gfx.hatebulet.api.HatebuEntry;
import com.github.gfx.hatebulet.api.HatebuFeedClient;
import com.github.gfx.hatebulet.api.HttpClientHolder;
import com.github.gfx.hatebulet.fragment.dummy.DummyContent;

import java.util.List;

import butterknife.ButterKnife;
import rx.functions.Action1;

public class EntryFragment extends Fragment implements AbsListView.OnItemClickListener {

    static final String PARAM_OF = "of";

    private OnFragmentInteractionListener listener;

    private AbsListView listView;

    private ArrayAdapter<HatebuEntry> adapter;

    public static EntryFragment newInstance() {
        EntryFragment fragment = new EntryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EntryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != listener) {
            listener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = listView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

}
