package com.setruth.calendar.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.TextureView
import android.view.ViewGroup
import android.widget.TextView
import com.setruth.calendar.R

/**
 * @author  :Setruth
 * time     :2022/4/24 21:37
 * e-mail   :1607908758@qq.com
 * remark   :calendar
 */

@SuppressLint("SetTextI18n")
class ClassInfoDialog(context: Context, className:String, teacher:String, book:String, remark:String, weekArea:Int,position:String) : Dialog(context) {
    val name:TextView
    val teacher:TextView
    val book:TextView
    val remark:TextView
    val weekArea:TextView
    val position:TextView
    init {
        setContentView(R.layout.class_info_dialog)
        window?.setBackgroundDrawableResource(R.drawable.around_ra)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.name=findViewById(R.id.class_dialog_name)
        this.book=findViewById(R.id.class_dialog_book)
        this.teacher=findViewById(R.id.class_dialog_teacher)
        this.remark=findViewById(R.id.class_dialog_remark)
        this.weekArea=findViewById(R.id.class_dialog_week_area)
        this.position=findViewById(R.id.class_dialog_position)
        this.name.text=className
        this.book.text=book
        this.teacher.text=teacher
        this.remark.text=remark
        this.position.text=position
        this.weekArea.text="到${weekArea}周"
    }
}