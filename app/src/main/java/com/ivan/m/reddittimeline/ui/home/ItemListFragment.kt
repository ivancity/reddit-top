package com.ivan.m.reddittimeline.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.ivan.m.reddittimeline.R
import com.ivan.m.reddittimeline.ui.placeholder.PlaceholderContent;
import com.ivan.m.reddittimeline.databinding.FragmentItemListBinding
import com.ivan.m.reddittimeline.databinding.ItemListContentBinding
import com.ivan.m.reddittimeline.dependency.Injection
import com.ivan.m.reddittimeline.repo.USER_PREFERENCES
import com.ivan.m.reddittimeline.ui.detail.ItemDetailFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES
)

class ItemListFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null

    private lateinit var viewModel: HomeListViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var fetchJob: Job? = null
//    private var adapter: PostsAdapter? = null// call initAdapter instead
    private lateinit var adapter: PostsAdapter// call initAdapter instead

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this.requireContext()))
            .get(HomeListViewModel::class.java)

        //viewModel.start()

        viewModel.homeUi.observe(viewLifecycleOwner) { homeUi ->
            Toast.makeText(context, "Test", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemList

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        /** Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        val onClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as PlaceholderContent.PlaceholderItem
            val bundle = Bundle()
            bundle.putString(
                ItemDetailFragment.ARG_ITEM_ID,
                item.id
            )
            if (itemDetailFragmentContainer != null) {
                itemDetailFragmentContainer.findNavController()
                    .navigate(R.id.fragment_item_detail, bundle)
            } else {
                itemView.findNavController().navigate(R.id.show_item_detail, bundle)
            }
        }

        /**
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        val onContextClickListener = View.OnContextClickListener { v ->
            val item = v.tag as PlaceholderContent.PlaceholderItem
            Toast.makeText(
                v.context,
                "Context click of item " + item.id,
                Toast.LENGTH_LONG
            ).show()
            true
        }

//        setupRecyclerView(
//            recyclerView = recyclerView,
//            onClickListener = onClickListener,
//            onContextClickListener = onContextClickListener
//        )

        initAdapter(
            onClickListener = onClickListener,
            onContextClickListener = onContextClickListener
        )
        fetchPosts()
        binding.retryButton?.setOnClickListener { adapter.retry() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchPosts() {
        // Make sure we cancel the previous job before creating a new one
        fetchJob?.cancel()
        fetchJob = lifecycleScope.launch {
            viewModel.getPosts().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun initAdapter(
        onClickListener: View.OnClickListener,
        onContextClickListener: View.OnContextClickListener
    ) {
        this.adapter = PostsAdapter(
            onClickListener = onClickListener,
            onContextClickListener = onContextClickListener
        )

        val header = PostLoadStateAdapter { adapter.retry() }

        binding.itemList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = PostLoadStateAdapter { adapter.retry()}
        )

        adapter.addLoadStateListener { loadState ->
            if (_binding == null) {
                return@addLoadStateListener
            }

            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Show a retry header if there was an error refreshing, and items were previously
            // cached OR default to the default prepend state
            header.loadState = loadState.mediator
                ?.refresh
                ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                ?: loadState.prepend

            _binding!!.itemList.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
                    || loadState.mediator?.refresh is LoadState.NotLoading
            _binding!!.progressBar!!.isVisible = loadState.mediator?.refresh is LoadState.Loading
            _binding!!.retryButton!!.isVisible = loadState.mediator?.refresh is LoadState.Error
                    && adapter.itemCount == 0

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

            errorState?.let {
                Toast.makeText(
                    context,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { _binding?.itemList?.scrollToPosition(0) }
        }

    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        onClickListener: View.OnClickListener,
        onContextClickListener: View.OnContextClickListener
    ) {

        recyclerView.adapter = SimpleItemRecyclerViewAdapter(
            PlaceholderContent.ITEMS,
            onClickListener,
            onContextClickListener
        )
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList?.visibility = View.VISIBLE
            binding.itemList.visibility = View.GONE
        } else {
            binding.emptyList?.visibility = View.GONE
            binding.itemList.visibility = View.VISIBLE
        }
    }

    @ExperimentalPagingApi
    private fun fetchItems() {
        fetchJob?.cancel()
        fetchJob = lifecycleScope.launch {
            viewModel.getPosts().collectLatest {

            }
        }
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: List<PlaceholderContent.PlaceholderItem>,
        private val onClickListener: View.OnClickListener,
        private val onContextClickListener: View.OnContextClickListener
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
                setOnContextClickListener(onContextClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(binding: ItemListContentBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val idView: TextView = binding.idText
            val contentView: TextView = binding.content
        }
    }
}
