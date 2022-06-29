package com.setruth.calendar.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import com.setruth.calendar.R

/**
 * @author  :Setruth
 * time     :2022/4/25 13:33
 * e-mail   :1607908758@qq.com
 * remark   :calendar
 */

class SetNowWeekIndexDialog(context: Context) : Dialog(context) {
    private val content:EditText
    private val saveBtn:TextView
    private val cancelBtn:TextView
    interface Save{
        fun save(content:String)
    }
    init {
        setContentView(R.layout.set_now_week_index)
        window?.setBackgroundDrawableResource(R.drawable.around_ra)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        content=findViewById(R.id.set_now_week_dialog_content)
        saveBtn=findViewById(R.id.set_now_week_dialog_save)
        cancelBtn=findViewById(R.id.set_now_week_dialog_cancel)
        cancelBtn.setOnClickListener {
            Toast.makeText(context, "在菜单中可以设置，以免无法定位今天的课程", Toast.LENGTH_LONG).show()
            dismiss()
        }
    }
    fun addListen(save:Save){
        saveBtn.setOnClickListener {
            save.save(content.text.toString())
        }
    }
}