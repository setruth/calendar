package com.setruth.calendar.tools

import java.util.*

class TimeTool {
    lateinit var calendar:Calendar
    var year:Int = 0
    var mom:Int =0
    var day:Int=0
    init {
        calendar=Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        this.year=calendar.get(Calendar.YEAR)
        this.mom=calendar.get(Calendar.MONTH)+1
        this.day=calendar.get(Calendar.DATE)
    }

}