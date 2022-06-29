package com.setruth.calendar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class MainViewPagerAdapter (context:Context,data:MutableList<View>): PagerAdapter() {
     var context: Context
     var data:MutableList<View>
    init {
        this.context=context
        this.data=data
    }
    override fun getCount(): Int {
       return data.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(data.get(position))
        return data.get(position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(data.get(position))
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }
}