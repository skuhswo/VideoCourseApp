package de.hsworms.videokurse.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsworms.videokurse.R
import de.hsworms.videokurse.data.CourseListItem
import de.hsworms.videokurse.viewmodel.CourseNavigationViewModel


private const val TAG = "CourseListFragment"

class CourseListFragment : Fragment() {

    private lateinit var courseListHeader: Button

    private lateinit var courseListRecyclerView: RecyclerView
    private var adapter: CourseAdapter? = null

    private lateinit var moreCoursesButton: Button

    // model created by hosting activity
    private val viewmodel: CourseNavigationViewModel by activityViewModels()

    // required interface for communication with hosting activities
    interface Callbacks {
        fun onCourseSelected(courseId: String)
        fun onMoreCoursesButtonSelected()
    }

    private var callbacks: Callbacks? = null

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
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_course_list, container, false)

        courseListRecyclerView = view.findViewById(R.id.course_list_recycler_view) as RecyclerView
        courseListRecyclerView.layoutManager = LinearLayoutManager(context)
        courseListRecyclerView.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        courseListHeader = view.findViewById(R.id.course_list_header)

        moreCoursesButton = view.findViewById(R.id.more_courses_button)
        moreCoursesButton.setOnClickListener(MoreCoursesButtonClickListener())

        updateUI()

        return view
    }

    private fun updateUI() {
        val courses = viewmodel.getMyCourses()
        adapter = CourseAdapter(courses)
        courseListRecyclerView.adapter = adapter

        if (courses.size == 1) {
            courseListHeader.text = getString(R.string.text_my_course)
        } else {
            courseListHeader.text = getString(R.string.text_my_courses)
        }
    }

    private inner class CourseHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var courseListItem: CourseListItem

        private val courseLabel: TextView = itemView.findViewById(R.id.course_label)
        private val courseDescriptionText: TextView = itemView.findViewById(R.id.course_description)

        private val courseButton: Button = itemView.findViewById(R.id.course_button)
        init {
            courseButton.setOnClickListener(this)
        }

        fun bind(courseListItem: CourseListItem) {
            this.courseListItem = courseListItem
            courseLabel.text = this.courseListItem.title

            courseDescriptionText.text = this.courseListItem.description

            val colorDrawable = ColorDrawable(Color.parseColor(courseListItem.complexity.v.color))
            itemView.background = colorDrawable
        }

        // Click handler for one course line item
        override fun onClick(v: View) {
            callbacks?.onCourseSelected(courseListItem.productId)
        }

    }

    private inner class CourseAdapter(var logovidListItems: List<CourseListItem>) :
        RecyclerView.Adapter<CourseHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
            val itemView = layoutInflater.inflate(R.layout.course_list_item, parent, false)

            val layoutParams: ViewGroup.LayoutParams = itemView.getLayoutParams()
            itemView.setLayoutParams(layoutParams)

            return CourseHolder(itemView)
        }

        override fun getItemCount() = logovidListItems.size

        override fun onBindViewHolder(holder: CourseHolder, position: Int) {
            val logovid = logovidListItems[position]
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams

            holder.itemView.layoutParams = params
            holder.bind(logovid)
        }
    }

    private inner class MoreCoursesButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            callbacks?.onMoreCoursesButtonSelected()
        }
    }

}
