package com.setruth.calendar.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.TextureView
import android.view.ViewGroup
import android.widget.TextView
import com.setruth.calendar.R

/**
 * @author  :Setruth
 * time     :2022/5/3 22:38
 * e-mail   :1607908758@qq.com
 * remark   :calendar
 */

class PrivateDialog(context: Context,bottomListen: BottomListen) : Dialog(context) {
    interface BottomListen{
        fun click(mode:Int)
    }
    init {
        setContentView(R.layout.private_dialog)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawableResource(R.drawable.around_ra)
        val allowBtn=findViewById<TextView>(R.id.allow)
        val refuseBtn=findViewById<TextView>(R.id.refuse)
        allowBtn.setOnClickListener {
            bottomListen.click(1)
            dismiss()
        }
        refuseBtn.setOnClickListener {
            bottomListen.click(0)
        }
    }

}