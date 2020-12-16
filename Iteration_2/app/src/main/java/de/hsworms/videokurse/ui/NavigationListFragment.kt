package de.hsworms.videokurse.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsworms.videokurse.R
import de.hsworms.videokurse.data.NavigationListItem
import de.hsworms.videokurse.viewmodel.CourseNavigationViewModel

private const val ARG_NAV_ID = "nav_id"

class NavigationListFragment : Fragment() {

    // required interface for hosting activity
    interface Callbacks {
        fun onNavigationSelected(navigationItemId: String)
        fun setToolbarTitle(title: String?)
    }

    private var callbacks: Callbacks? = null

    private lateinit var navigationRecyclerView: RecyclerView
    private var adapter: NavigationAdapter? = null

    // UUID of the navigation item for which the sub menu is being displayed
    private lateinit var navID: String

    // model created by hosting activity
    private val viewmodel: CourseNavigationViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null)
            navID = arguments?.getSerializable(ARG_NAV_ID) as String
        else navID = viewmodel.currentCourse

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_navigation_list, container, false)

        navigationRecyclerView = view.findViewById(R.id.navigation_list_recycler_view) as RecyclerView
        navigationRecyclerView.layoutManager = LinearLayoutManager(context)

        callbacks?.setToolbarTitle(viewmodel.getToolbarTitle(navID) ?: "")

        updateUI()

        return view
    }

    private fun updateUI() {
        val navigationItems = viewmodel.getNavigationItemsForID(navID)
        adapter = NavigationAdapter(navigationItems)
        navigationRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(navID: String?): NavigationListFragment {
            val args = Bundle().apply {
                putSerializable(ARG_NAV_ID, navID.toString())
            }
            return NavigationListFragment().apply {
                arguments = args
            }
        }
    }

    private inner class NavigationHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var navigationListItem: NavigationListItem

        private val navigationButton: Button = itemView.findViewById(R.id.navigation_button)

        init {
            navigationButton.setOnClickListener(this)
        }

        fun bind(navigationListItem: NavigationListItem) {
            this.navigationListItem = navigationListItem
            navigationButton.text = this.navigationListItem.title
        }

        // Click handler for one Logovid line item
        override fun onClick(v: View) {
            callbacks?.onNavigationSelected(navigationListItem.id)
        }
    }

    private inner class NavigationAdapter(var navigationListItems: List<NavigationListItem>) : RecyclerView.Adapter<NavigationHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationHolder {
            val view = layoutInflater.inflate(R.layout.navigation_list_item, parent, false)
            return NavigationHolder(view)
        }

        override fun getItemCount() = navigationListItems.size

        override fun onBindViewHolder(holder: NavigationHolder, position: Int) {
            val navItem = navigationListItems[position]
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams

            if (navItem.newSection || position == 0){
                params.topMargin = 100
                holder.itemView.layoutParams = params
            } else {
                params.topMargin = 0
                holder.itemView.layoutParams = params
            }

            if (position == navigationListItems.lastIndex){
                params.bottomMargin = 100
                holder.itemView.layoutParams = params
            } else {
                params.bottomMargin = 0
                holder.itemView.layoutParams = params
            }

            holder.itemView.layoutParams = params
            holder.bind(navItem)
        }
    }

}

