package com.setruth.calendar.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.setruth.calendar.R
import com.setruth.calendar.data.ClassInfo
import com.setruth.calendar.data.Constant
import com.setruth.calendar.tools.Dao

class AllClassActivity : AppCompatActivity() {
    private lateinit var allClassContainer:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_class)
         val actionBar=supportActionBar!!
        actionBar.show()
        actionBar.title = "所有课程"
        actionBar.setDisplayHomeAsUpEnabled(true)
        //初始化控件
        initComponent()
        //渲染课程项
        initCLassItem(allClassContainer)
    }
    /**
     * TODO 初始化组件
     *
     */
    fun initComponent(){
        allClassContainer=findViewById(R.id.all_class_container)

    }
    /**
     * TODO 初始化课程卡片
     *
     * @param container 传入渲染在哪个容器中
     */
    fun initCLassItem(container:LinearLayout){
        val dao=Dao(this)
        val classList=dao.getAllClass()
        if (classList.size==0){
            LayoutInflater.from(this).inflate(R.layout.empty_page,container,false)
        }else{
            classList.forEach {
                getClassItem(it,container)
            }
        }
    }

    /**
     * TODO 获取课程信息卡片
     *
     * @param classInfo 传入当前课表；信息
     * @return 返回卡片
     */
    @SuppressLint("SetTextI18n")
    fun getClassItem(classInfo: ClassInfo, container: LinearLayout){
        val view:LinearLayout=LayoutInflater.from(this).inflate(R.layout.all_class_item,null,false) as LinearLayout
        val name=view.findViewById<TextView>(R.id.class_name)
        val count=view.findViewById<TextView>(R.id.class_count)
        val intoWeek=view.findViewById<TextView>(R.id.class_info_to)
        val delete=view.findViewById<TextView>(R.id.delete_class)
        val edit=view.findViewById<TextView>(R.id.edit_class)
        //最后一周
        var endWeek=0
        classInfo.timeList.forEach {
            if (it.endTime>endWeek){
                endWeek=it.endWeek
            }
        }
        count.text="一周有${classInfo.timeList.size}节课(不分单双周)"
        name.text=classInfo.name
        intoWeek.text="到第${endWeek}周"
        //设置删除点击事件
        delete.setOnClickListener {
            val dialog=AlertDialog.Builder(this)
            dialog.setTitle("删除课程信息")
            dialog.setMessage("你确定要删除本课程的所有信息吗")
            dialog.setPositiveButton("确认", DialogInterface.OnClickListener { dialogInterface, i ->
                val dao=Dao(this)
                dao.delClass(classInfo)
                container.removeView(view)
                Intent().also {
                    it.action=Constant.UPDATE_BROADCAST
                    sendBroadcast(it)
                }
            })
            dialog.show()
            true
        }
        edit.setOnClickListener {
            val intent=Intent(this,EditClassActivity::class.java)
            intent.putExtra("name",classInfo.name)
            startActivity(intent)
        }

        container.addView(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                Intent().also {
                    it.action=Constant.UPDATE_BROADCAST
                    sendBroadcast(it)
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}