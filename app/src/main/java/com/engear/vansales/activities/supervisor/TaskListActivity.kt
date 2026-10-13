package com.engear.vansales.activities.supervisor

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.TaskDateItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityTaskListBinding
import com.engear.vansales.databinding.DialogTaskItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.TaskData
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TaskListActivity : BaseActivity(), OnItemClickListener {

    var taskList: List<TaskData.Task> = ArrayList()
    var dateList: ArrayList<String> = ArrayList()
    var selectedDate = ""
    lateinit var binding: ActivityTaskListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        R.layout.activity_task_list
        binding = ActivityTaskListBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Task List")
        showRightMenu()
        baseBinding.toolbarLeftTitle!!.setText("List")
        baseBinding.toolbarLeftTitle!!.setOnClickListener {

            if (binding.calender.visibility == View.VISIBLE) {
                binding.calender.visibility = View.GONE
                binding.btnSubmit.visibility = View.GONE
                baseBinding.toolbarLeftTitle!!.setText("Add")
                taskList = ArrayList()
                taskList = db!!.getAllTask()
                dateList = ArrayList()
                for (item in taskList) {
                    dateList!!.add(item.date)
                }
                sortDateList()

                setUpRecycler(dateList)

            } else {
                binding.calender.visibility = View.VISIBLE
                binding.btnSubmit.visibility = View.VISIBLE
                baseBinding.toolbarLeftTitle!!.setText("List")
                taskList = ArrayList()
                taskList = db!!.getAllTaskbyDate(selectedDate)
                setUpRecycler(dateList)
            }
        }
        setNavigationView()
        selectedDate = com.engear.vansales.utils.Utils.getCurrentDate()
        binding.btnSubmit.setOnClickListener {
            showDialog()
        }

        binding.calender
            .setOnDateChangeListener { view, year, month, dayOfMonth ->
                val Date = (dayOfMonth.toString() + "-"
                        + (month + 1) + "-" + year)

                //                                binding.dateView.setText(Date);
                val sdf =
                    SimpleDateFormat("yyyy-MM-dd")
                val calendar = Calendar.getInstance()
                calendar[year, month] = dayOfMonth
                selectedDate = sdf.format(calendar.time)
                var localist: ArrayList<String> = ArrayList()

                for (itm in dateList) {
                    if (itm.equals(selectedDate)) {
                        localist.add(itm)
                    }
                }
                setUpRecycler(localist)
            }

        selectedDate = Utils.getCurrentDate()

//        taskList = ArrayList()
//
        taskList = db!!.getAllTask()

        dateList = ArrayList()
        for (item in taskList) {
            dateList!!.add(item.date)
        }
        sortDateList()

        Log.e("curdate",selectedDate)
        var localist: ArrayList<String> = ArrayList()

        for (itm in dateList) {
            if (itm.equals(selectedDate)) {
                localist.add(itm)
            }
        }
        setUpRecycler(localist)


//        setUpRecycler(dateList)

    }

    private fun sortDateList() {

        val hs = HashSet<String>()
        hs.addAll(dateList) // demoArrayList= name of arrayList from which u want to remove duplicates
        dateList.clear()
        dateList.addAll(hs)
    }


    private fun showDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        val binding: DialogTaskItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(applicationContext), R.layout.dialog_task_item, null, false
        )
        dialog.setContentView(binding.root)
        val displayMetrics = DisplayMetrics()
        val layoutParams = WindowManager.LayoutParams()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        /////////////
        var array_: ArrayList<String> = ArrayList()
        array_.add("task1")
        array_.add("task2")
        array_.add("task3")
        val adapterCategory = ArrayAdapter(
            this,
            R.layout.spinner_item_layout, array_
        )
        binding.spnTask.adapter = adapterCategory
        binding.spnTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {


                var str = array_[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.btnConfirm.setOnClickListener {
            var data = TaskData.Task(
                binding.edtTitle.text.toString(),
                binding.edtDescription.text.toString(),
                binding.dateTxt.text.toString(),
                binding.timeText.text.toString(),
                if (binding.allDayCheck.isChecked) {
                    "1"
                } else {
                    "0"
                }
            )

            db!!.insertTask(data)
            setUpRecycler(dateList)
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.dateTxt.setText(selectedDate)
        binding.timeText.setText(com.engear.vansales.utils.Utils.getCurrentTime())

        binding.allDayCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.timeText.visibility = View.INVISIBLE
            } else {
                binding.timeText.visibility = View.VISIBLE
            }
        }


        layoutParams.copyFrom(dialog.window!!.attributes)
        val dialogWindowWidth = (height * 0.5f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.gravity = Gravity.CENTER
        dialog.window!!.attributes = layoutParams

        dialog.show()
    }

    private fun setUpRecycler(dateList: ArrayList<String>) {

        Log.e("taskk", Gson().toJson(dateList))
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = TaskDateItemAdapter(this, dateList, db)
    }

    override fun onItemsClick(position: Int, tag: String) {

    }


}