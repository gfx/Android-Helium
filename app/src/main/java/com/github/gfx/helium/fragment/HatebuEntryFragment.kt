package com.github.gfx.helium.fragment

import com.cookpad.android.rxt4a.operators.OperatorAddToCompositeSubscription
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription
import com.github.gfx.helium.HeliumApplication
import com.github.gfx.helium.R
import com.github.gfx.helium.api.HatenaClient
import com.github.gfx.helium.databinding.CardHatebuEntryBinding
import com.github.gfx.helium.databinding.FragmentEntryBinding
import com.github.gfx.helium.model.HatebuEntry
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
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import java.util.Collections

import javax.annotation.ParametersAreNonnullByDefault
import javax.inject.Inject

import rx.Observable
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers

@ParametersAreNonnullByDefault
class HatebuEntryFragment : Fragment(), OnItemClickListener<HatebuEntry>, OnItemLongClickListener<HatebuEntry> {

    internal val emptyEntry = HatebuEntry()

    @Inject
    internal var hatenaClient: HatenaClient

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

    internal var binding: FragmentEntryBinding

    internal var adapter: EntriesAdapter

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
            val category = category
            tracker.sendScreenView(if (category != null) TAG + "-" + category else TAG)
        }
    }

    internal fun reload(): Observable<List<HatebuEntry>> {
        val observable: Observable<List<HatebuEntry>>
        if (category != null) {
            observable = hatenaClient.getHotentries(category)
        } else {
            observable = hatenaClient.hotentries
        }
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).lift(OperatorAddToCompositeSubscription<List<HatebuEntry>>(compositeSubscription)).onErrorReturn { throwable ->
            Log.w(TAG, "Error while loading entries", throwable)
            if (activity != null) {
                Toast.makeText(activity, "Error while loading entries\n" + throwable.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show()
            }
            emptyList<HatebuEntry>()
        }
    }

    internal val category: String?
        get() = if (arguments != null) arguments.getString(kCategory) else null

    internal fun openUri(uri: Uri, action: String) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
        trackOpenUri(action)
    }

    internal fun trackOpenUri(action: String) {
        val category = category
        tracker.sendEvent(if (category != null) TAG + "-" + category else TAG, action)
    }

    override fun onItemClick(view: View, entry: HatebuEntry) {
        openUri(Uri.parse(entry.link), "original")
    }

    override fun onItemLongClick(view: View, entry: HatebuEntry): Boolean {
        openUri(hatenaClient.buildHatebuEntryUri(entry.link), "service")
        return true
    }

    private inner class EntriesAdapter(context: Context) : ArrayRecyclerAdapter<HatebuEntry, BindingHolder<CardHatebuEntryBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<CardHatebuEntryBinding> {
            return BindingHolder(context, parent, R.layout.card_hatebu_entry)
        }

        override fun onBindViewHolder(holder: BindingHolder<CardHatebuEntryBinding>, position: Int) {
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

            binding.title.text = entry.title
            binding.date.text = entry.timestamp
            binding.subject.text = TextUtils.join(" ", entry.subject)
            binding.bookmarkCount.text = entry.bookmarkCount
            binding.description.text = entry.description
            binding.originalUrl.text = entry.link

            binding.bookmarkCount.setOnClickListener { v -> dispatchOnItemLongClick(v, entry) }
        }
    }

    companion object {

        internal val TAG = HatebuEntryFragment::class.java!!.getSimpleName()

        internal val kCategory = "category"

        fun newInstance(): HatebuEntryFragment {
            val fragment = HatebuEntryFragment()
            fragment.arguments = Bundle()
            return fragment
        }

        fun newInstance(category: String): HatebuEntryFragment {
            val fragment = HatebuEntryFragment()
            val args = Bundle()
            args.putString(kCategory, category)
            fragment.arguments = args
            return fragment
        }
    }
}
