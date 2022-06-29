package com.setruth.calendar.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import com.setruth.calendar.R

/**
 * @author  :Setruth
 * time     :2022/5/3 23:19
 * e-mail   :1607908758@qq.com
 * remark   :calendar
 */

class AboutDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.about_dialog)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        window?.setBackgroundDrawableResource(R.drawable.around_ra)
    }
}