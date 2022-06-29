package com.setruth.calendar.data

class Constant {
    companion object{
        val WEEK_DATA= arrayOf("一","二","三","四","五","六","日")
        val WEEK_COUNT= arrayOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21)
        val CLASS_TIME=arrayOf("第1节","第2节","第3节","第4节","第5节","第6节","第7节","第8节","第9节","第10节","第11节","第12节","第13节","第14节")
         const val VERSION_CODE=1
        const val DATA_BASE_NAME="calender.db"
        const val CALENDAR_CONTENT_TABLE_NAME="calender_content"
        const val CALENDAR_ITEM_HEIGHT=150
        const val SHARED_PREFERENCES_NAME="setting"
        const val UPDATE_BROADCAST="UPDATE_BROADCAST"
    }
}