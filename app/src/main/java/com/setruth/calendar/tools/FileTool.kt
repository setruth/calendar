package com.setruth.calendar.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.setruth.calendar.data.Constant
import java.io.*

/**
 * @author  :Setruth
 * time     :2022/4/27 0:02
 * e-mail   :1607908758@qq.com
 * remark   :calendar
 */

class FileTool {
    companion object{
        val TAG="FileTool"

        /**
         * TODO 把uri转换为File对象
         *
         * @param uri
         * @return 返回转换的对象
         */
        @JvmStatic
        fun uriToFile(uri:Uri):File{
            val root= Environment.getExternalStorageDirectory()
            val index=uri.path!!.lastIndexOf(":")
            val fileUrl= uri.path!!.substring(index+1)
            return File("$root/$fileUrl")
        }

        /**
         * TODO 保存导入的文件
         *
         * @param context 上下文对象
         * @param file 文件对象
         */
        @JvmStatic
        fun saveImportFile(context: Context, file: File){
            Log.e(TAG, "saveImportFile: ${file.toString()} wenjian:$file", )
            val dataBaseFile=context.getDatabasePath(Constant.DATA_BASE_NAME)
            dataBaseFile.delete()
            val newDataBaseFile=context.getDatabasePath(Constant.DATA_BASE_NAME)
            // 拿到输入流
            val input =  FileInputStream(file);
            // 建立存储器
            val buf = ByteArray(input.available())
            input.read(buf);
            // 关闭输入流
            input.close();
            val fileOutputStream=FileOutputStream(newDataBaseFile)
            val bufferedOutputStream=BufferedOutputStream(fileOutputStream)
            bufferedOutputStream.write(buf)
            bufferedOutputStream.close()
            fileOutputStream.close()
        }

        /**
         * TODO 分享数据库文件
         *
         * @param context
         */
        @JvmStatic
        fun shareDatabaseFile(context: Context){
            val dataBaseFile=context.getDatabasePath(Constant.DATA_BASE_NAME)
            val share =  Intent(Intent.ACTION_SEND);
            val photoUri = FileProvider.getUriForFile(context,context.packageName + ".fileprovider",dataBaseFile);
            share.putExtra(Intent.EXTRA_STREAM, photoUri);
            share.setType("*/*");//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));

        }
    }
}