package com.github.gfx.helium.fragment

import com.bumptech.glide.Glide
import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription
import com.github.gfx.android.orma.TransactionTask
import com.github.gfx.helium.HeliumApplication
import com.github.gfx.helium.R
import com.github.gfx.helium.api.HatenaClient
import com.github.gfx.helium.databinding.CardTimelineEntryBinding
import com.github.gfx.helium.databinding.FragmentEntryBinding
import com.github.gfx.helium.model.HatebuEntry
import com.github.gfx.helium.model.HatebuEntry_Relation
import com.github.gfx.helium.model.OrmaDatabase
import com.github.gfx.helium.util.AppTracker
import com.github.gfx.helium.util.LoadingAnimation
import com.github.gfx.helium.util.ViewSwitcher
import com.github.gfx.helium.widget.ArrayRecyclerAdapter
import com.github.gfx.helium.widget.BindingHolder
import com.github.gfx.helium.widget.LayoutManagers
import com.github.gfx.helium.widget.LoadingIndicatorViewHolder
import com.github.gfx.helium.widget.OnItemClickListener
import com.github.gfx.helium.widget.OnItemLongClickListener

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import java.util.ArrayList
import java.util.Collections

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject

import rx.Observable
import rx.Subscriber
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers

/**
 * The timeline that shows what you like.
 */
@ParametersAreNonnullByDefault
class TimelineFragment : Fragment(), OnItemClickListener<HatebuEntry>, OnItemLongClickListener<HatebuEntry> {

    internal val emptyEntry = HatebuEntry()

    @Inject
    private lateinit var hatenaClient: HatenaClient

    @Inject
    private lateinit var tracker: AppTracker

    @Inject
    private lateinit var compositeSubscription: AndroidCompositeSubscription

    @Inject
    private lateinit var viewSwitcher: ViewSwitcher

    @Inject
    private lateinit var loadingAnimation: LoadingAnimation

    @Inject
    private lateinit var layoutManagers: LayoutManagers

    @Inject
    private lateinit var orma: OrmaDatabase

    private lateinit var binding: FragmentEntryBinding

    private lateinit var adapter: EntriesAdapter

    private lateinit var username: String

    private var currentEntries: Int = 0

    private var cachedEntries = emptyList<HatebuEntry>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        HeliumApplication.getComponent(this).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = EntriesAdapter(activity)
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        username = arguments.getString(kUsername)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEntryBinding.inflate(inflater, container, false)

        loadCachedEntries()

        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManagers.create()

        binding.swipeRefresh.setColorSchemeResources(R.color.app_primary)
        binding.swipeRefresh.setOnRefreshListener {
            reload().subscribe { items ->
                mergeItemsAndCache(items)
                binding.swipeRefresh.isRefreshing = false
            }
        }

        reload().subscribe { items ->
            mergeItemsAndCache(items)

            binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMore()
                    }
                }
            })
        }

        return binding.root
    }

    override fun onStop() {
        compositeSubscription.unsubscribe()

        truncateCache()

        super.onStop()
    }

    private fun truncateCache() {
        if (!cachedEntries.isEmpty()) {
            relation().truncateAsObservable(CACHED_ENTRY_SIZE).subscribeOn(Schedulers.io()).subscribe()
        }
    }

    internal fun mergeItemsAndCache(newItems: List<HatebuEntry>) {
        var i: Int
        i = 0
        FIND@ while (i < newItems.size) {
            val newItem = newItems[i]
            for (cache in adapter) {
                if (newItem.link == cache.link && newItem.creator == cache.creator) {
                    break@FIND
                }
            }
            i++
        }

        if (i == 0) {
            return  // nothing to do
        }

        val items = newItems.subList(0, i)

        orma.transactionAsync(object : TransactionTask() {
            @Throws(Exception::class)
            override fun execute() {
                val reversedItems = ArrayList(items)
                Collections.reverse(reversedItems)
                relation().inserter().executeAll(reversedItems)
            }
        })

        if (cachedEntries.isEmpty()) {
            adapter.reset(items)
        } else {
            adapter.addAll(0, items)
            adapter.notifyItemRangeInserted(0, items.size)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            tracker.sendScreenView(TAG)
        }
    }

    internal fun relation(): HatebuEntry_Relation {
        return orma.relationOfHatebuEntry().orderByCacheIdDesc()
    }

    internal fun loadCachedEntries() {
        cachedEntries = relation().selector().toList()

        if (cachedEntries.isEmpty()) {
            var i = 0
            val max = layoutManagers.spanCount
            while (i < max) {
                adapter.addItem(emptyEntry)
                i++
            }
        } else {
            adapter.addAll(cachedEntries)
        }
    }

    internal fun reload(): Observable<List<HatebuEntry>> {
        currentEntries = 0
        val observable = hatenaClient.getFavotites(username, currentEntries)
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).lift(OperatorAddToCompositeSubscription<List<HatebuEntry>>(compositeSubscription)).onErrorReturn { e ->
            reportError(e)
            emptyList<HatebuEntry>()
        }
    }

    internal fun loadMore() {
        currentEntries += adapter.itemCount
        hatenaClient.getFavotites(username, currentEntries).observeOn(AndroidSchedulers.mainThread()).lift(OperatorAddToCompositeSubscription<List<HatebuEntry>>(compositeSubscription)).subscribe(object : Subscriber<List<HatebuEntry>>() {
            override fun onCompleted() {

            }

            override fun onError(e: Throwable) {
                reportError(e)
            }

            override fun onNext(items: List<HatebuEntry>) {
                adapter.addAllWithNotification(items)
            }
        })
    }

    internal fun reportError(e: Throwable) {
        Log.w(TAG, "Error while loading entries", e)
        if (activity != null) {
            Toast.makeText(activity, "Error while loading entries\n" + e.message,
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onItemClick(view: View, item: HatebuEntry) {
        openUri(Uri.parse(item.link), "original")
    }

    override fun onItemLongClick(view: View, item: HatebuEntry): Boolean {
        openUri(hatenaClient.buildHatebuEntryUri(item.link), "service")
        return true
    }

    internal fun openUri(uri: Uri, action: String) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
        trackOpenUri(action)
    }

    internal fun trackOpenUri(action: String) {
        tracker.sendEvent(TAG, action)
    }

    inner class EntriesAdapter(context: Context) : ArrayRecyclerAdapter<HatebuEntry, BindingHolder<CardTimelineEntryBinding>>(context) {

        private val TYPE_LOADING = 0

        private val TYPE_NORMAL = 1


        override fun getItemViewType(position: Int): Int {
            val entry = getItem(position)
            return if (entry === emptyEntry) TYPE_LOADING else TYPE_NORMAL
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<CardTimelineEntryBinding> {
            when (viewType) {
                TYPE_LOADING -> return LoadingIndicatorViewHolder(context, parent, R.layout.card_timeline_entry)
                TYPE_NORMAL -> return BindingHolder(context, parent, R.layout.card_timeline_entry)
            }
            throw AssertionError("not reached")
        }

        override fun onBindViewHolder(holder: BindingHolder<CardTimelineEntryBinding>, position: Int) {
            val binding = holder.binding

            val entry = getItem(position)

            if (entry === emptyEntry) {
                return
            }

            holder.itemView.setOnClickListener { v -> dispatchOnItemClick(v, entry) }
            holder.itemView.setOnLongClickListener { v -> dispatchOnItemLongClick(v, entry) }

            binding.bookmarkCount.setOnClickListener { v -> dispatchOnItemLongClick(v, entry) }

            Glide.with(context).load(hatenaClient.buildHatebuIconUri(entry.creator)).into(binding.author)

            binding.title.text = entry.title

            viewSwitcher.setTextOrGone(binding.tags, entry.tags)
            viewSwitcher.setTextOrGone(binding.comment, entry.description)

            binding.date.text = entry.timestamp
            binding.bookmarkCount.text = entry.bookmarkCount
            binding.originalUrl.text = entry.link

            val summary = entry.summary
            if (!TextUtils.isEmpty(summary)) {
                binding.textSummary.text = entry.summary
                binding.layoutSummary.visibility = View.VISIBLE
                binding.textSummary.visibility = View.VISIBLE
                binding.imageSummary.visibility = View.GONE
            } else if (entry.looksLikeImageUrl()) {
                Glide.with(context).load(entry.link).into(binding.imageSummary)
                binding.layoutSummary.visibility = View.VISIBLE
                binding.textSummary.visibility = View.GONE
                binding.imageSummary.visibility = View.VISIBLE
            } else {
                binding.layoutSummary.visibility = View.GONE
            }
        }
    }

    companion object {

        internal val TAG = TimelineFragment::class.java!!.getSimpleName()

        internal val kUsername = "username"

        internal val CACHED_ENTRY_SIZE = 100

        fun newInstance(username: String): TimelineFragment {
            val fragment = TimelineFragment()
            val args = Bundle()
            args.putString(kUsername, username)
            fragment.arguments = args
            return fragment
        }
    }
}
