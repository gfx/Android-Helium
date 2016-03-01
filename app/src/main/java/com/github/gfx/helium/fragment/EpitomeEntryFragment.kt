package com.github.gfx.helium.fragment

import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription
import com.github.gfx.helium.HeliumApplication
import com.github.gfx.helium.R
import com.github.gfx.helium.api.EpitomeClient
import com.github.gfx.helium.databinding.CardEpitomeEntryBinding
import com.github.gfx.helium.databinding.FragmentEntryBinding
import com.github.gfx.helium.databinding.ItemEpitomeGistBinding
import com.github.gfx.helium.model.EpitomeEntry
import com.github.gfx.helium.util.AppTracker
import com.github.gfx.helium.util.LoadingAnimation
import com.github.gfx.helium.util.ViewSwitcher
import com.github.gfx.helium.widget.ArrayRecyclerAdapter
import com.github.gfx.helium.widget.BindingHolder
import com.github.gfx.helium.widget.LayoutManagers
import com.github.gfx.helium.widget.OnItemClickListener
import com.github.gfx.helium.widget.OnItemLongClickListener

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import java.util.ArrayList
import java.util.Collections

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject

import rx.Observable
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers

@ParametersAreNonnullByDefault
class EpitomeEntryFragment : Fragment(), OnItemClickListener<EpitomeEntry>, OnItemLongClickListener<EpitomeEntry> {

    @Inject
    internal var epitomeClient: EpitomeClient

    @Inject
    internal var tracker: AppTracker

    @Inject
    internal var compositeSubscription: AndroidCompositeSubscription

    @Inject
    internal var viewSwitcher: ViewSwitcher

    @Inject
    internal var loadingAnimation: LoadingAnimation

    @Inject
    internal var layoutManagers: LayoutManagers

    @Inject
    internal var inflater: LayoutInflater

    internal var binding: FragmentEntryBinding

    internal var adapter: EntriesAdapter

    internal var emptyEntry = EpitomeEntry()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        HeliumApplication.getComponent(this).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = EntriesAdapter(activity)
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)
        var i = 0
        val max = layoutManagers.spanCount
        while (i < max) {
            adapter.addItem(emptyEntry)
            i++
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentEntryBinding.inflate(inflater, container, false)

        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManagers.create()

        binding.swipeRefresh.setColorSchemeResources(R.color.app_primary)
        binding.swipeRefresh.setOnRefreshListener {
            reload().subscribe { items ->
                adapter.reset(items)
                binding.swipeRefresh.isRefreshing = false
            }
        }

        reload().subscribe { items -> adapter.reset(items) }

        return binding.root
    }

    override fun onStop() {
        compositeSubscription.unsubscribe()

        super.onStop()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            tracker.sendScreenView(TAG)
        }
    }

    internal fun reload(): Observable<List<EpitomeEntry>> {
        return epitomeClient.entries.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).lift(OperatorAddToCompositeSubscription<List<EpitomeEntry>>(compositeSubscription)).map { items ->
            val entriesWithKnownScheme = ArrayList<EpitomeEntry>(items.size)
            for (entry in items) {
                if (entry.hasKnownScheme()) {
                    entriesWithKnownScheme.add(entry)
                }
            }
            entriesWithKnownScheme
        }.onErrorReturn(Func1<kotlin.Throwable, kotlin.collections.List<com.github.gfx.helium.model.EpitomeEntry>> { throwable ->
            Log.w(TAG, "Error while loading entries", throwable)
            if (activity != null) {
                Toast.makeText(activity, "Error while loading entries",
                        Toast.LENGTH_LONG).show()
            }
            emptyList<EpitomeEntry>()
        })
    }

    override fun onItemClick(item: View, entry: EpitomeEntry) {
        val uri = Uri.parse(entry.epitomeUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

        tracker.sendEvent(TAG, "service")
    }

    override fun onItemLongClick(view: View, entry: EpitomeEntry): Boolean {
        val uri = Uri.parse(entry.upstreamUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

        tracker.sendEvent(TAG, "original")
        return true
    }

    private inner class EntriesAdapter internal constructor(context: Context) : ArrayRecyclerAdapter<EpitomeEntry, BindingHolder<CardEpitomeEntryBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<CardEpitomeEntryBinding> {
            return BindingHolder(context, parent, R.layout.card_epitome_entry)
        }

        override fun onBindViewHolder(holder: BindingHolder<CardEpitomeEntryBinding>, position: Int) {
            val binding = holder.binding

            val entry = getItem(position)

            if (entry === emptyEntry) {
                loadingAnimation.start(binding.root)
                return
            }
            loadingAnimation.cancel(binding.root)

            holder.itemView.isClickable = true
            holder.itemView.setOnClickListener { v -> dispatchOnItemClick(v, entry) }
            holder.itemView.setOnLongClickListener { v -> dispatchOnItemLongClick(v, entry) }

            if (entry.isGists) {
                populateGists(holder, entry)
                holder.itemView.visibility = View.VISIBLE
            } else {
                throw IllegalStateException("Unknown scheme: " + entry.scheme)
            }
        }

        internal fun populateGists(holder: BindingHolder<CardEpitomeEntryBinding>, entry: EpitomeEntry) {
            val binding = holder.binding

            binding.title.text = entry.title
            binding.views.text = "閲覧数: " + Integer.toString(entry.views)
            binding.publishedDate.text = "投稿日: " + entry.timestamp.toLocalDate()
            binding.originalUrl.text = entry.upstreamUrl

            fillGists(binding.gists, entry.gists)
        }

        internal fun fillGists(layout: LinearLayout, gists: List<EpitomeEntry.Gist>) {
            layout.removeAllViews()

            for (i in gists.indices) {
                val binding = ItemEpitomeGistBinding.inflate(inflater, layout, false)

                binding.gistPoint.setText(Integer.toString(i + 1))
                binding.gistText.setText(gists[i].content)

                layout.addView(binding.getRoot())
            }
        }
    }

    companion object {

        internal val TAG = EpitomeEntryFragment::class.java!!.getSimpleName()

        fun newInstance(): EpitomeEntryFragment {
            val fragment = EpitomeEntryFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }
}
