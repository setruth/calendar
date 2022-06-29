package com.setruth.calendar.tools

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.annotation.Nullable
import com.alibaba.fastjson.JSON
import com.setruth.calendar.data.ClassInfo
import com.setruth.calendar.data.Constant
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ProcessBuilder.Redirect.to

/**
 * @author  :Setruth
 * time     :2022/4/19 9:14
 * e-mail   :1607908758@qq.com
 * remark  :calendar
 */
class Dao(context: Context) {
    private var dataBaseHelper:DataBaseHelper = DataBaseHelper(context)

    /**
     * TODO 判断是否有重复的课程
     *
     * @param className 课程的名称
     * @return true:有重复的 false:没有重复的
     */
    @SuppressLint("Recycle")
    fun judeClassReport(className:String):Boolean{
        val db=dataBaseHelper.readableDatabase
        val queryAllSql="select * from ${Constant.CALENDAR_CONTENT_TABLE_NAME}"
        val cursor=db.rawQuery(queryAllSql,null)
        val classNameIndex:Int=cursor.getColumnIndex("class_name")
        while (cursor.moveToNext()){
            if (cursor.getString(classNameIndex)==className) {
                return true
            }
        }
        return false
    }
    /**
     * TODO 增加新的课表内容
     *
     * @param classInfo 课表信息
     */
    fun insert(classInfo: ClassInfo){
        val db=dataBaseHelper.writableDatabase
        val sql="insert into ${Constant.CALENDAR_CONTENT_TABLE_NAME}(id,class_name,teacher,remark,book,class_time) values(?,?,?,?,?,?)"
        val timeJSONString:String=JSON.toJSONString(classInfo.timeList)
        db.execSQL(sql, arrayOf(null,classInfo.name,classInfo.teacher,classInfo.remark,classInfo.book,timeJSONString))
        db.close()
    }

    /**
     * TODO 删除所选中的课
     *
     * @param classInfo 本课信息
     * @param index 删除哪一节
     */
    fun delCLassIndex(classInfo: ClassInfo,classIndex:Int){
        val db=dataBaseHelper.writableDatabase
        val list= mutableListOf<ClassInfo.Time>()
        classInfo.timeList.forEachIndexed { index, time ->
            if (index!=classIndex){
                list.add(time)
            }
        }
        val timeJSONString=JSON.toJSONString(list)
        val sql="update ${Constant.CALENDAR_CONTENT_TABLE_NAME} set class_time='$timeJSONString' where class_name='${classInfo.name}'"
        db.execSQL(sql)
        db.close()
    }

    /**
     * TODO 删除整节课
     *
     * @param classInfo
     */
    fun delClass(classInfo: ClassInfo){
        val db =dataBaseHelper.writableDatabase
        val sql="delete from ${Constant.CALENDAR_CONTENT_TABLE_NAME} where class_name='${classInfo.name}'"
        db.execSQL(sql)
        db.close()
    }

    /**
     * TODO 更新课程信息
     *
     * @param newClassInfo
     */
    fun updateClassInfo(newClassInfo: ClassInfo){
        val db=dataBaseHelper.writableDatabase
        val timeJSONString:String=JSON.toJSONString(newClassInfo.timeList)
        val sql="update ${Constant.CALENDAR_CONTENT_TABLE_NAME} set teacher='${newClassInfo.teacher}', remark='${newClassInfo.remark}', book='${newClassInfo.book}', class_time='$timeJSONString' where class_name='${newClassInfo.name}'"
        db.execSQL(sql)
    }

    /**
     * TODO 查询课程信息
     *
     * @param classInfoName 课程名字
     * @return 返回查询到的课程信息
     */
    @SuppressLint("Recycle")
    fun queryClassInfo(classInfoName: String): ClassInfo? {
        val allSql="select * from ${Constant.CALENDAR_CONTENT_TABLE_NAME}"
        val dataBase=dataBaseHelper.readableDatabase
        val cursor=dataBase.rawQuery(allSql,null)
        val classNameIndex:Int=cursor.getColumnIndex("class_name")
        val teacherIndex:Int=cursor.getColumnIndex("teacher")
        val remarkIndex:Int=cursor.getColumnIndex("remark")
        val bookIndex:Int=cursor.getColumnIndex("book")
        val timeIndex:Int=cursor.getColumnIndex("class_time")
        val classInfo:ClassInfo
        while (cursor.moveToNext()){
            if (cursor.getString(classNameIndex)==classInfoName){
                val timeList:MutableList<ClassInfo.Time> = JSON.parseArray(cursor.getString(timeIndex),ClassInfo.Time::class.java)
                classInfo= ClassInfo(cursor.getString(classNameIndex),
                    cursor.getString(teacherIndex),
                    cursor.getString(remarkIndex),
                    cursor.getString(bookIndex),
                )
                classInfo.timeList=timeList
                return classInfo
            }
        }
        return null
    }

    /**
     * TODO 全部的课程信息
     *
     * @return 返回全部课程信息
     */
    fun getAllClass():MutableList<ClassInfo>{
        val res:MutableList<ClassInfo> = mutableListOf()
        val queryAllSql="select * from ${Constant.CALENDAR_CONTENT_TABLE_NAME}"
        val dataBase=dataBaseHelper.readableDatabase
        val cursor=dataBase.rawQuery(queryAllSql,null)
        val classNameIndex:Int=cursor.getColumnIndex("class_name")
        val teacherIndex:Int=cursor.getColumnIndex("teacher")
        val remarkIndex:Int=cursor.getColumnIndex("remark")
        val bookIndex:Int=cursor.getColumnIndex("book")
        val timeIndex:Int=cursor.getColumnIndex("class_time")
        while (cursor.moveToNext()){
            val timeList:MutableList<ClassInfo.Time> = JSON.parseArray(cursor.getString(timeIndex),ClassInfo.Time::class.java)
            val classInfo= ClassInfo(cursor.getString(classNameIndex),
               cursor.getString(teacherIndex),
               cursor.getString(remarkIndex),
               cursor.getString(bookIndex),
           )
            classInfo.timeList=timeList
            res.add(classInfo)
        }
        cursor.close()
        dataBase.close()
        return res
    }
}