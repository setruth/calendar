package com.setruth.calendar.tools

import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.setruth.calendar.data.Constant
class DataBaseHelper(
    context: Context?,
) : SQLiteOpenHelper(context, Constant.DATA_BASE_NAME, null, Constant.VERSION_CODE) {
    override fun onCreate(p0: SQLiteDatabase?) {
        Log.e(TAG, "onCreate: 创建数据库", )
        val sql="create table calender_content" +
                "(id integer PRIMARY KEY AUTOINCREMENT," +
                "class_name varchar(100)," +
                "teacher varchar(50)," +
                "remark varchar(200)," +
                "book varchar(50)," +
                "class_time varchar(150));"
        p0?.execSQL(sql)
    }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}