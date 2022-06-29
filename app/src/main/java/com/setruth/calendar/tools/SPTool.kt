package com.setruth.calendar.tools

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * @author  :Setruth
 * time     :2022/4/25 14:11
 * e-mail   :1607908758@qq.com
 * remark   :calendar
 */

class SPTool(context: Context) {
    var context:Context=context
    companion object{
        /**
         * TODO 获取 SharedPreferences对象
         *
         * @param context 上下文
         * @param name 要访问哪个村存储的文件
         * @return 返回 SharedPreferences对象
         */
        @JvmStatic
        fun  getSharedPreferences(context: Context,name: String):SharedPreferences{
            return context.getSharedPreferences(name, MODE_PRIVATE)
        }
        @JvmStatic
        fun getEdit(context: Context,name: String):SharedPreferences.Editor{
            return context.getSharedPreferences(name, MODE_PRIVATE).edit()
        }
    }
}