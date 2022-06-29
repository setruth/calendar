package com.setruth.calendar.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.setruth.calendar.R
import com.setruth.calendar.adapter.MainViewPagerAdapter
import com.setruth.calendar.data.ClassInfo
import com.setruth.calendar.data.Constant
import com.setruth.calendar.dialog.AboutDialog
import com.setruth.calendar.dialog.ClassInfoDialog
import com.setruth.calendar.dialog.PrivateDialog
import com.setruth.calendar.dialog.SetNowWeekIndexDialog
import com.setruth.calendar.tools.Dao
import com.setruth.calendar.tools.FileTool
import com.setruth.calendar.tools.SPTool
import com.setruth.calendar.tools.TimeTool
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private lateinit var menuBtn: ImageView
    private lateinit var time: TextView
    private var timeTool = TimeTool()
    private lateinit var viewPager: ViewPager
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private var viewPagerList: MutableList<View> = mutableListOf()
    private lateinit var addClassBt: ImageView
    private val that = this
    private var weekPosition: Int = 0 //当前页面在第几周
    private var changeWeekPosition: Int = 0 //改变的周
    private var positionView: Int = 0 //当前是第几个页面

    //TODO 更新页面内容广播接收者
    inner class UpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            initNowPageData(positionView, changeWeekPosition, viewPagerList)
        }
    }

    //TODO 软件创建
    @SuppressLint("SetTextI18n", "RtlHardcoded", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //获取存储对象
        val sharedPreferences = SPTool.getSharedPreferences(this, Constant.SHARED_PREFERENCES_NAME)

        //注册更新广播
        val filter = IntentFilter()
        filter.addAction(Constant.UPDATE_BROADCAST)
        val brUpdate = UpdateBroadcastReceiver()
        registerReceiver(brUpdate, filter)
        //设置actionbar
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.hide()
        }
        /***************获取组件*****************/
        drawer = findViewById(R.id.drawer)
        menuBtn = findViewById(R.id.menu_btn)
        time = findViewById(R.id.main_time)
        viewPager = findViewById(R.id.viewpager)
        addClassBt = findViewById(R.id.add_class)
        /***************初始化组件*****************/
        time.text = "" + timeTool.year + "/" + timeTool.mom + "/" + timeTool.day
        val bottomSheetBehavior =
            BottomSheetBehavior.from(findViewById<NestedScrollView>(R.id.bottom_content))
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.isHideable = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        menuBtn.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        addClassBt.setOnClickListener {
            val intent = Intent(this, AddClassActivity::class.java)
            startActivity(intent)
        }
        viewPagerList.add(LayoutInflater.from(this).inflate(R.layout.content_container, null, true))
        viewPagerList.add(LayoutInflater.from(this).inflate(R.layout.content_container, null, true))
        mainViewPagerAdapter = MainViewPagerAdapter(this, viewPagerList)
        viewPager.adapter = mainViewPagerAdapter
        //判断是否有设置当前是第几个星期
        if (!sharedPreferences.getBoolean("setWeekSign", false)) {
            //如果没有设置当前是第几个周则弹出设置窗口设置
            val setNowWeekIndexDialog = SetNowWeekIndexDialog(this)
            //设置点击空白部分弹窗不会消失
            //TODO 设置当前周是第几周弹窗
            setNowWeekIndexDialog.setCanceledOnTouchOutside(false)
            setNowWeekIndexDialog.show()
            setNowWeekIndexDialog.addListen(object : SetNowWeekIndexDialog.Save {
                override fun save(content: String) {
                    if (content != "") {
                        val editor = SPTool.getEdit(that, "setting")
                        //保存设置了周的标记
                        editor.putBoolean("setWeekSign", true)
                        //保存是从第几周开始
                        editor.putInt("startWeek", content.toInt())
                        //保存设置那天的时间戳
                        editor.putLong("startDayTime", System.currentTimeMillis())
                        editor.commit()
                        weekPosition = getNowWeekIndex()
                        changeWeekPosition = weekPosition
                        initNowPageData(positionView, weekPosition, viewPagerList)
                        setNowWeekIndexDialog.dismiss()
                    }
                }
            })
        } else {
            weekPosition = getNowWeekIndex()
            changeWeekPosition = weekPosition
            initNowPageData(positionView, changeWeekPosition, viewPagerList)
        }
        //判断是否同意过用户须知，否则弹出隐私弹窗
        if (!sharedPreferences.getBoolean("allowPrivate",false)){
            val privateDialog=PrivateDialog(this,object :PrivateDialog.BottomListen{
                override fun click(mode: Int) {
                    if (mode==1) {
                        val edit=SPTool.getEdit(that,Constant.SHARED_PREFERENCES_NAME)
                        edit.putBoolean("allowPrivate",true)
                        edit.commit()
                        Toast.makeText(that, "感谢您的配合", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(that, "请同意才能正常使用", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            })
            privateDialog.setCancelable(false)
            privateDialog.show()
        }
        //监听页面滑动
        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                positionView = position
                changeWeekPosition = if (position == 0) {
                    weekPosition
                } else {
                    weekPosition + 1
                }
                initNowPageData(positionView, changeWeekPosition, viewPagerList)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        initMenu()
    }

    //TODO 重写子intent返回结果方法
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data?.data != null) {
            val uri = data.data
            val nameIndex = uri!!.path!!.lastIndexOf(":")
            val name = uri.path?.substring(nameIndex)
            //获取选择的文件后缀
            val suffix = uri.path?.substring(uri.path!!.length - 2, uri.path!!.length)
            if (suffix != "db") {
                Toast.makeText(this, "您选择的不是课程表的文件", Toast.LENGTH_SHORT).show()
            } else {
                //确认弹窗
                val builder = AlertDialog.Builder(this)
                builder.setTitle("确认导入吗")
                builder.setMessage("文件名是$name")
                builder.setNeutralButton("取消", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss() //关闭dialog
                    Toast.makeText(this, "取消成功", Toast.LENGTH_SHORT).show()
                })
                builder.setPositiveButton(
                    "确认"
                ) { dialog, _ ->
                    val file = FileTool.uriToFile(uri)
                    FileTool.saveImportFile(this, file)
                    initNowPageData(positionView, changeWeekPosition, viewPagerList)
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    /**
     * TODO 初始化当前页面课程表数据
     *
     * @param position 当前是第几个页面索引
     * @param positionWeek 当前是第几周
     * @param pageList 所有周所在的容器
     */
    @SuppressLint("SetTextI18n")
    private fun initNowPageData(positionView: Int, positionWeek: Int, pageList: MutableList<View>) {
        //保存本周课程
        var calendarClassList = mutableListOf<ClassInfo>()
        //默认本周是没课的
        var pageHaveClass: Boolean = false
        //获取周索引所对应的这一周页面
        val nowWeekView: View = pageList[positionView]
        //第一周标识TextView
        val record = nowWeekView.findViewById<TextView>(R.id.record)
        //判断是单周还是双周
        val dao: Dao = Dao(this)
        //如果渲染的是今天的周设置今天的的顶部索引
        val weekMode = if (positionWeek == 1) {
            1
        } else {
            if (positionWeek % 2 == 0) {
                2
            } else {
                1
            }
        }
        if (positionView == 0) {
            setNowDayNavigation(nowWeekView as LinearLayout)
        }
        record.text = "${positionWeek}周" //设置第几周提示
        //获取今天周的课程表内容
        calendarClassList = dao.getAllClass()
        if (calendarClassList.size != 0) {
            pageHaveClass = true //如果获取的本周课程不为空则本周有课 设置本周课标识为true
            val noPage = nowWeekView.findViewById<LinearLayout>(R.id.no_have_class_page)
            noPage.visibility = View.GONE //获取本周无课页面隐藏此页面
        }
        //如果当前周有课程内容则进行课程渲染
        if (pageHaveClass == true) {
            //获取当前竖直页面的最外容器
            val calendarContentContainer: LinearLayout =
                nowWeekView.findViewById(R.id.calendar_content_container)
            //获取所有的垂直页面布局
            val contentItemContainerList: Sequence<View> = calendarContentContainer.children
            contentItemContainerList.forEachIndexed { index, item ->
                if (index == 0) {
                    val leftLinearLayout = item as LinearLayout
                    //当布局索引是0的时候 判断是否已经初始化 如果有内容就不再渲染
                    if (leftLinearLayout.childCount != Constant.CLASS_TIME.size) {
                        initLeftIndex(leftLinearLayout)
                    }
                } else { // 初始化index天的课程表信息课程信息列表
                    initCLassTime(
                        positionWeek,
                        weekMode,
                        index,
                        item as LinearLayout,
                        calendarClassList
                    )
                }
            }
        }
    }

    /**
     * TODO 初始化课程列表内容
     *
     * @param dayIndex 当前星期几
     * @param view 添加到当前天的容器的
     * @param calendarClassList 当前周的课程表信息
     */
    private fun initCLassTime(
        weekIndex: Int,
        nowWeekMode: Int,
        dayIndex: Int,
        view: LinearLayout,
        calendarClassList: MutableList<ClassInfo>,
    ) {
        val viewChildCount = view.childCount
        if (viewChildCount != 0) {
            view.removeAllViews()
        }
        val gridLayout = GridLayout(this) //动态添加今天的课程网格
        gridLayout.columnCount = 1 //一列
        gridLayout.rowCount = Constant.CLASS_TIME.size //有Constant.CLASS_TIME.size行
        gridLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        //先添加占位的空块
        for (i in 0..Constant.CLASS_TIME.size - 1) {
            gridLayout.addView(getEmptyBlockView())
        }
        //找出本周中今天的课程
        calendarClassList.forEach { classInfo ->
            classInfo.timeList.forEachIndexed { index, it ->
                if (it.dayIndex + 1 == dayIndex && (it.startWeek <= weekIndex && it.endWeek >= weekIndex) && (it.weekMode == nowWeekMode || it.weekMode == 0)) {
                    val height = (it.endTime - it.startTime) + 1
                    gridLayout.addView(
                        getClassBlockView(it.position, it.endWeek, classInfo, index),
                        classPosition(it.startTime - 1, 0, height)
                    )
                }
            }
        }
        view.addView(gridLayout)
    }

    /**
     * TODO 获取空的课程信息块
     *
     * @return 返回占位的空布局
     */
    private fun getEmptyBlockView(): View {
        val block = View(this)
        block.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            Constant.CALENDAR_ITEM_HEIGHT
        )
        return block
    }

    /**
     * TODO 初始化左边时间索引
     *
     * @param view 传入时间索引所在的容器
     */
    @SuppressLint("SetTextI18n")
    private fun initLeftIndex(view: LinearLayout) {
        Constant.CLASS_TIME.forEach {
            val textView = TextView(this)
            textView.text = it
            textView.gravity = Gravity.CENTER
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Constant.CALENDAR_ITEM_HEIGHT
            )
            view.addView(textView)
        }
    }

    /**
     * TODO 设置今天的顶部突出索引
     *
     * @param nowPage 今天的页面
     */
    private fun setNowDayNavigation(nowPage: LinearLayout) {
        val navigationContainer = nowPage.findViewById<LinearLayout>(R.id.week_navigation_container)
        val date: Date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        val i: Int = calendar.get(Calendar.DAY_OF_WEEK)
        val children = navigationContainer.children
        val k = if (i == 1) {
            7
        } else {
            i - 1
        }
        children.forEachIndexed { index, view ->
            if (index == k) {
                val text = view as TextView
                text.setBackgroundResource(R.drawable.around_ra_active)
                text.setTextColor(Color.parseColor("#ffffff"))
            }
        }
    }

    /**
     * TODO 初始化左边的菜单
     *
     */
    fun initMenu() {
        val allClass = findViewById<TextView>(R.id.menu_all_class)
        val setWeekIndex = findViewById<TextView>(R.id.menu_set_week)
        val exportClassTable = findViewById<TextView>(R.id.menu_export_class_table)
        val importClassTable = findViewById<TextView>(R.id.menu_import_class_table)
        val private = findViewById<TextView>(R.id.menu_private)
        val about = findViewById<TextView>(R.id.menu_about)
        //所有课程表按钮的监听
        allClass.setOnClickListener {
            startActivity(Intent(this, AllClassActivity::class.java))
        }
        private.setOnClickListener {
            val dialog=PrivateDialog(this,object:PrivateDialog.BottomListen{
                override fun click(mode: Int) {

                }

            })
            dialog.show()
        }
        //关于按钮的监听
        about.setOnClickListener {
            val dialog=AboutDialog(this)
            dialog.show()
        }
        //设置选择设置本周索引监听
        setWeekIndex.setOnClickListener {
            //如果没有设置当前是第几个周则弹出设置窗口设置
            val setNowWeekIndexDialog = SetNowWeekIndexDialog(this)
            setNowWeekIndexDialog.show()
            setNowWeekIndexDialog.addListen(object : SetNowWeekIndexDialog.Save {
                override fun save(content: String) {
                    if (content != "") {
                        val editor = SPTool.getEdit(that, "setting")
                        //保存设置了周的标记
                        editor.putBoolean("setWeekSign", true)
                        //保存是从第几周开始
                        editor.putInt("startWeek", content.toInt())
                        //保存设置那天的时间戳
                        editor.putLong("startDayTime", System.currentTimeMillis())
                        editor.commit()
                        weekPosition = getNowWeekIndex()
                        changeWeekPosition = weekPosition
                        initNowPageData(positionView, weekPosition, viewPagerList)
                        setNowWeekIndexDialog.dismiss()
                    }
                }
            })
        }
        //导入监听
        importClassTable.setOnClickListener {
            //判断是否是安卓11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 有没有权限全部文件权限
                if (!Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "`请给予所有文件操作权限,用于导入课程表内容", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, 1024)
                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("导入的注意事项")
                    dialog.setMessage("请把要导入的课程表文件放在根目录下(也就是emulated/0/下)，以免无法导入或者导入出错，因为您导入后会把原来的课程表文件删除，所以不慎操作可能会导致原来的信息丢失")
                    dialog.setPositiveButton("确认") { _, _ ->
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "application/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                        startActivityForResult(intent, 1)
                    }
                    dialog.show()
                }
            }else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("导入的注意事项")
                dialog.setMessage("请把要导入的课程表文件放在根目录下(也就是emulated/0/下)，以免无法导入或者导入出错，因为您导入后会把原来的课程表文件删除，所以不慎操作可能会导致原来的信息丢失")
                dialog.setPositiveButton("确认") { _, _ ->
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "application/*"
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    startActivityForResult(intent, 1)
                }
                dialog.show()
            }
        }
        exportClassTable.setOnClickListener {
            FileTool.shareDatabaseFile(this)
        }
    }

    /**
     * TODO 获取课程块
     *
     * @param position 上课地点
     * @param info 这个课程的信息
     * @return 返回生成的块
     */
    fun getClassBlockView(
        position: String,
        endWeekIndex: Int,
        info: ClassInfo,
        classIndex: Int,
    ): TextView {
        val block: TextView = TextView(this)
        block.text = info.name
        block.gravity = Gravity.CENTER_HORIZONTAL
        block.setBackgroundResource(R.drawable.class_item_style)
        block.setOnClickListener {
            val dialog = ClassInfoDialog(
                this,
                info.name,
                info.teacher,
                info.book,
                info.remark,
                endWeekIndex,
                position
            )
            dialog.show()
        }
        block.setOnLongClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("确认删除课程吗")
            dialog.setMessage("只会删除这部分课程")
            dialog.setPositiveButton("确认") { dialogInterface, i ->
                val dao = Dao(this)
                dao.delCLassIndex(info, classIndex)
                initNowPageData(positionView, weekPosition, viewPagerList)
            }
            dialog.show()
            true
        }
        return block
    }

    /**
     * TODO 设置当天的课程的位置
     *
     * @param row 在第几行
     * @param column 第几列
     * @param rowSize 占了多少行
     * @return 返回位置
     */
    fun classPosition(row: Int, column: Int, rowSize: Int): GridLayout.LayoutParams {
        val rowSpec: GridLayout.Spec = GridLayout.spec(row, rowSize)
        val columnSpec: GridLayout.Spec = GridLayout.spec(column, 1)
        val la = GridLayout.LayoutParams(rowSpec, columnSpec)
        la.width = GridLayout.LayoutParams.MATCH_PARENT
        la.setGravity(Gravity.FILL)
        la.setMargins(3, 3, 3, 3)
        return la
    }

    /**
     * TODO 获取本周是从设置的周开始的第几周
     *
     *  return 返回本周是第几周
     */
    fun getNowWeekIndex(): Int {
        val todayTime: Long = System.currentTimeMillis()
        val sp = SPTool.getSharedPreferences(this, Constant.SHARED_PREFERENCES_NAME)
        val startWeekIndex = sp.getInt("startWeek", 0)
        val startTime = sp.getLong("startDayTime", 0)
        val r = todayTime - startTime
        return if (r / 24 / 60 / 60 / 1000 < 7) {
            startWeekIndex
        } else {
            val weekCount = (r / 7)
            startWeekIndex + weekCount.toInt()
        }
    }
}