package com.setruth.calendar.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.setruth.calendar.R
import com.setruth.calendar.data.Constant

class SetWeekDialog(context: Context,initMode:Int,initStartWeek:Int,initEndWeek:Int) : Dialog(context),View.OnClickListener {
    interface SaveListener {
        fun save(mode:Int,startWeek:Int,endWeek:Int)
    }
    /*mode标识变量设置当前双周单周还是全周,0全周，1单周，2双周*/
    var mode=0
     var saveBtn:TextView
     var startWeek: NumberPicker
     var endWeek: NumberPicker
     var weekModeButtonList= mutableListOf<TextView>()
    init {
        setContentView(R.layout.set_week)
        this.mode=initMode
        window?.setBackgroundDrawableResource(R.drawable.around_ra)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        saveBtn=findViewById(R.id.save)
        weekModeButtonList.add(findViewById(R.id.week_mode_all))
        weekModeButtonList.add(findViewById(R.id.week_mode_single))
        weekModeButtonList.add(findViewById(R.id.week_mode_double))
        startWeek=findViewById(R.id.set_week_dialog_start_week)
        endWeek=findViewById(R.id.set_week_dialog_end_week)
        /*初始化控件信息*/
        startWeek.maxValue=Constant.WEEK_COUNT.size
        startWeek.minValue=1
        endWeek.maxValue=Constant.WEEK_COUNT.size
        endWeek.minValue=1
        startWeek.value=initStartWeek
        endWeek.value=initEndWeek
        weekModeButtonList.forEach {
            it.setOnClickListener(this)
        }
        setModeButton(mode)
        /*设置周的滑动监听*/
        startWeek.setOnScrollListener(NumberPicker.OnScrollListener { numberPicker, i ->
            when(i){
                NumberPicker.OnScrollListener.SCROLL_STATE_IDLE ->{
                    if (startWeek.value>endWeek.value){
                        endWeek.value=startWeek.value
                    }
                }
            }
        })
        /*设置周的滑动监听*/
        endWeek.setOnScrollListener(NumberPicker.OnScrollListener { numberPicker, i ->
            when(i){
                NumberPicker.OnScrollListener.SCROLL_STATE_IDLE ->{
                    if (startWeek.value>endWeek.value){
                        endWeek.value=startWeek.value
                    }
                }
            }
        })
    }
    fun addListener(saveListener: SaveListener){
        saveBtn.setOnClickListener {
            if (startWeek.value>endWeek.value){
                Toast.makeText(context, "开始的周大于结束的周", Toast.LENGTH_SHORT).show()
            }else{
                saveListener.save(mode,startWeek.value,endWeek.value)
            }
        }
    }

    /**
     * TODO 改变周模式的按钮点击样式
     *
     * @param modeIndex 第几个按钮
     */
    fun setModeButton(modeIndex:Int){
        weekModeButtonList.forEachIndexed { index, textView ->
            if (index==modeIndex){
                textView.setBackgroundResource(R.drawable.around_ra_active)
                textView.setTextColor(Color.parseColor("#ffffff"))
            }else{
                textView.setBackgroundResource(R.drawable.around_ra)
                textView.setTextColor(Color.parseColor("#000000"))
            }
        }
    }

    /**
     * TODO 继承View.Onclick判断点击的是哪一个按钮
     *
     * @param p0
     */
    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.week_mode_single->{
                mode=1
                setModeButton(1)
            }
            R.id.week_mode_all->{
                mode=0
                setModeButton(0)
            }
            R.id.week_mode_double->{
                mode=2
                setModeButton(2)
            }
        }
    }

}