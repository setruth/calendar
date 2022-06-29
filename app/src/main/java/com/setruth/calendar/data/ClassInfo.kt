package com.setruth.calendar.data

import android.widget.EditText
import java.util.*
import kotlin.collections.HashMap

class ClassInfo(name: String, teacher: String, remark: String, book: String) {
    /**
     * TODO 周的数据结构
     *
     * @property area
     * @property mode
     */

    /**
     * TODO 时间数据结构
     *
     */
     class Time {
         var position: String=""
         var dayIndex: Int = 0
         var startTime: Int = 1
         var endTime: Int = 1
        var startWeek: Int = 1
        var endWeek: Int = Constant.WEEK_COUNT.size
        var weekMode: Int = 0
     }

    var name: String = name
    var teacher: String = teacher
    var remark: String = remark
    var book:String=book
    var timeList = mutableListOf<Time>()
    fun addTime(time: Time) {
        timeList.add(time)
    }
}