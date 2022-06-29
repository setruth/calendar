    package com.setruth.calendar.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.setruth.calendar.R
import com.setruth.calendar.data.ClassInfo
import com.setruth.calendar.data.Constant
import com.setruth.calendar.dialog.SetTimeDialog
import com.setruth.calendar.dialog.SetWeekDialog
import com.setruth.calendar.tools.Dao
import java.util.*
import kotlin.collections.HashMap

    class EditClassActivity : AppCompatActivity() {
        //本节课的课程信息
        private  var info: ClassInfo? = null
        //添加新的时间块按钮
        private lateinit var addTime: FloatingActionButton
        //课程信息容器
        private lateinit var classInfoContainer: LinearLayout
        //时间页面列表
        private var timeViewList: MutableList<View> = mutableListOf()
        private lateinit var name: EditText
        private lateinit var teacher: EditText
        private lateinit var remark: EditText
        private lateinit var book: EditText
        private lateinit var classInfo: ClassInfo
        //创建默认的周数据
        private val that = this
        private lateinit var dao: Dao
        //时间块
        private var timeDataList: MutableList<ClassInfo.Time> = mutableListOf()
        //要编辑的课程表名称
        lateinit var  className:String
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_class)
            //获取传入的课程表名称
            val intent=intent
            className= intent.getStringExtra("name").toString()
            //获取数据库
            dao = Dao(this)
            //获取本节课的内容
            info=dao.queryClassInfo(className)
            if (info==null) {
                Toast.makeText(this, "查不到$className 课程", Toast.LENGTH_SHORT).show()
            }else{
                //获取actionbar
                val actionBar = supportActionBar
                if (actionBar != null) {
                    /*设置actionbar标题*/
                    actionBar.title = "添加新的课程"
                }
                /*获取组件*/
                name = findViewById(R.id.class_name)
                teacher = findViewById(R.id.teacher_name)
                book = findViewById(R.id.book_name)
                remark = findViewById(R.id.class_remark)
                classInfoContainer = findViewById(R.id.set_class_container)
                /*设置actionbar返回*/
                actionBar?.setDisplayHomeAsUpEnabled(true)
                addTime = findViewById<FloatingActionButton>(R.id.add_time)
                addTime.setOnClickListener {
                    timeViewList.add(addTimeContainer(classInfoContainer, R.layout.select_class_time,timeDataList.size,null))
                }
                initCLassInfo(info!!,timeViewList)
            }

        }

        /**
         * TODO 初始化当前页面的课程信息
         *
         * @param timeViewList 时间块列表
         * @param classInfo 课程信息
         */
        fun initCLassInfo(classInfo: ClassInfo, timeViewList: MutableList<View>) {
            name.setText(classInfo.name)
            name.isEnabled=false //不可编辑
            teacher.setText(classInfo.teacher)
            book.setText(classInfo.book)
            remark.setText(classInfo.remark)
            classInfo.timeList.forEach {
                timeViewList.add(addTimeContainer(classInfoContainer, R.layout.select_class_time,timeDataList.size,it))
            }
        }
        /**
         * TODO 解析选择的时间
         *
         * @param content
         * @return 返回Int数组第一个是第几天 第二个是开始时间 第三个是结束时间
         */
        fun analysisTimeContent(content: String): Array<Int> {
            val weekIndexIndex: Int = content.indexOf("期")
            val weekIndexContent: String = content.substring(weekIndexIndex + 1, weekIndexIndex + 2)
            var week: Int = 0
            Constant.WEEK_DATA.forEachIndexed { index, item ->
                week = if (item == weekIndexContent) {
                    index
                } else {
                    week
                }
            }
            val startTimeIndex: Int = content.indexOf("第")
            val startTIme: Int = content.substring(startTimeIndex + 1, startTimeIndex + 2).toInt()-1
            val endTimeIndex: Int = content.lastIndexOf("第")
            val endTIme: Int = content.substring(endTimeIndex + 1, endTimeIndex + 2).toInt()-1
            return arrayOf(week, startTIme, endTIme)
        }

        /**
         * TODO 添加课程时间页面块
         *
         * @param container 添加到的页面
         * @param resId 时间页面的资源id
         * @param viewId 这是第几个时间页面
         * @return 返回时间页面
         */
        @SuppressLint("CutPasteId", "SetTextI18n")
        fun addTimeContainer(container: LinearLayout, resId: Int, viewId:Int,classTime:ClassInfo.Time?): View {
            //如果 提供了root 且 attachToRoot为true，返回值是root， 否则，是xml 中的 root。
            val view = LayoutInflater.from(this).inflate(resId, null, false)
            container.addView(view)
            //获取组件对象
            val setTimeBtn = view.findViewById<TextView>(R.id.choose_time)
            val position=view.findViewById<EditText>(R.id.position)
            val setWeek=view.findViewById<TextView>(R.id.choose_week)
            val delete=view.findViewById<TextView>(R.id.delete_time)
            //创建当前时间的数据
            val time=if (classTime==null){
                 ClassInfo.Time()
            }else{
                position.setText(classTime.position)
                classTime
            }
            //设置默认的显示按钮信息
            setTimeBtn.text = "星期${Constant.WEEK_DATA[time.dayIndex]}第${time.startTime}节课->第${time.endTime}节课"
            setWeek.text="从第${time.startWeek}到第${time.endWeek}(全周)"
            //设置按钮的监听s
            setTimeBtn.setOnClickListener {
                val weekRes = analysisTimeContent(setTimeBtn.text.toString())
                val setTimeDialog = SetTimeDialog(that, weekRes[0], weekRes[1], weekRes[2])
                setTimeDialog.setSaveListener(object : SetTimeDialog.SaveListener {
                    @SuppressLint("SetTextI18n")
                    override fun save(week: Int, startTime: Int, endTime: Int) {
                        time.dayIndex = week
                        time.startTime = startTime
                        time.endTime = endTime
                        setTimeBtn.text =
                            "星期${Constant.WEEK_DATA[time.dayIndex]}第${time.startTime}节课->第${time.endTime}节课"
                        setTimeDialog.dismiss()
                    }
                })
                setTimeDialog.show()
            }
            setWeek.setOnClickListener {
                /*设置选择周数的弹窗*/
                val setWeekDialog = SetWeekDialog(that, time.weekMode, time.startWeek,time.endWeek)
                setWeekDialog.addListener(object: SetWeekDialog.SaveListener{
                    @SuppressLint("SetTextI18n")
                    override fun save(mode: Int, startWeek: Int, endWeek: Int) {
                        time.startWeek=startWeek
                        time.endWeek=endWeek
                        time.weekMode=mode
                        val modeContent=if (mode==0){
                            "全周"
                        }else if (mode==1){
                            "单周"
                        }else{
                            "双周"
                        }
                        setWeekDialog.dismiss()
                        setWeek.text="第$startWeek 周到第$endWeek ($modeContent)"
                    }
                })
                setWeekDialog.show()
            }
            //删除这个时间段
            delete.setOnClickListener {
                container.removeView(view)
                timeDataList.removeAt(viewId)
                timeViewList.removeAt(viewId)
            }
            //把当前设置时间页面数据添加到全局时间数据列表中
            timeDataList.add(time)
            return view
        }

        /**
         * TODO 更新课程信息
         *
         */
        fun updateClassInfo() {
            val name = name.text.toString()
            val teacherContent: String
            val remarkContent: String
            val bookContent: String
            //判断是否有课程名称
            if (name == "") {
                Toast.makeText(this, "课程名不能为空", Toast.LENGTH_SHORT).show()
            } else {
                //获取老师信息
                teacherContent = if (teacher.text.toString() == "") {
                    ""
                } else {
                    teacher.text.toString()
                }
                //获取备注信息
                remarkContent = if (remark.text.toString() == "") {
                    ""
                } else {
                    remark.text.toString()
                }
                //书本信息
                bookContent = if (book.text.toString() == "") {
                    ""
                } else {
                    book.text.toString()
                }
                var timeTiger=true //时间段标记,是否有空的信息
                timeViewList.forEachIndexed { index, view ->
                    val positionEditText: EditText = view.findViewById(R.id.position)
                    val position: String = positionEditText.text.toString()
                    if (position != "") {
                        timeDataList[index].position = position
                    } else {
                        timeTiger = false
                    }
                }
                if (timeViewList.size == 0) {
                    Toast.makeText(that, "请设置上课地点", Toast.LENGTH_SHORT).show()
                } else if (timeTiger) {
                    classInfo = ClassInfo(name, teacherContent, remarkContent, bookContent)
                    classInfo.timeList=timeDataList
                    Log.e("TAG", "updateClassInfo: 更新", )
                    val dao= Dao(that)
                    dao.updateClassInfo(classInfo)
                    Toast.makeText(that, "更新成功", Toast.LENGTH_SHORT).show()
                    Intent().also {
                        it.action = Constant.UPDATE_BROADCAST
                        sendBroadcast(it)
                    }
                    finish()
                } else {
                    Toast.makeText(that, "地点和上课时间都要设置", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * TODO 重写actionbar的点击监听
         *
         * @param item
         * @return
         */
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    finish()
                }
                R.id.save -> {
                    updateClassInfo()
                }
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            MenuInflater(this).inflate(R.menu.main, menu)
            return true
        }
}