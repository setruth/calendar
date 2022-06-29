package com.setruth.calendar.dialog

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.TextView
import android.widget.Toast
import com.setruth.calendar.R
import com.setruth.calendar.data.Constant
import java.util.*

class SetTimeDialog(context: Context, private val initWeekIndex: Int,
                    private val initStartTime: Int, private val initEndTime: Int
) : Dialog(context) {

     interface SaveListener{
        fun save(week:Int,startTime: Int,endTime: Int)
    }

    private lateinit var saveBtn:TextView
    private lateinit var week:NumberPicker
    private lateinit var startTime:NumberPicker
    private lateinit var endTime:NumberPicker
    var calendar: Calendar= Calendar.getInstance()
    init {
        setContentView(R.layout.choose_time)
        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawableResource(R.drawable.around_ra)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        initPicker()
    }
    fun initPicker(){
        saveBtn=findViewById(R.id.save)
        //初始化周选择器
        week=findViewById(R.id.class_day)
        week.displayedValues=Constant.WEEK_DATA
        week.maxValue=Constant.WEEK_DATA.size-1
        week.minValue=0
        week.value=initWeekIndex
        //初始化课程开始选择器
        startTime=findViewById(R.id.class_start)
        startTime.displayedValues=Constant.CLASS_TIME
        startTime.maxValue=Constant.CLASS_TIME.size-1
        startTime.minValue=0
        startTime.value=initStartTime
        //初始化结束选择器
        endTime=findViewById(R.id.class_end)
        endTime.displayedValues=Constant.CLASS_TIME
        endTime.maxValue=Constant.CLASS_TIME.size-1
        endTime.minValue=0
        endTime.value=initEndTime
        //设置每行滑动监听
        startTime.setOnScrollListener { numberPicker, i ->
            when(i){
                SCROLL_STATE_IDLE->{
                    if (numberPicker.value>endTime.value){
                        endTime.value=numberPicker.value
                    }
                }
            }
        }
        endTime.setOnScrollListener { numberPicker, i ->
            when(i){
                SCROLL_STATE_IDLE->{
                    if (numberPicker.value<startTime.value){
                        startTime.value=numberPicker.value
                    }
                }
            }
        }
    }
    fun setSaveListener(saveListener: SaveListener){
        saveBtn.setOnClickListener {
           if (startTime.value>endTime.value){
               Toast.makeText(context, "开始时间大于结束时间", Toast.LENGTH_SHORT).show()
           }else{
               saveListener.save(week.value,startTime.value+1,endTime.value+1)
           }
        }
    }
}