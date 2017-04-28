package com.example.story.MyAdapter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.story.activity.CollectionFragment;
import com.example.story.activity.FoodFragment;
import com.example.story.activity.HealthyFragment;
import com.example.story.activity.MainFragment;
import com.example.story.activity.TechnologyFragment;

/**
 *创建一个viewpager的内容适配器类，继承FragmentPagerAdapter
 *@author story
 *@time 2017/4/13 16:22
 */
public class MyFragmentPageAdapter extends FragmentPagerAdapter {
    private Handler handler;
    private static final String PAGE_RECOMMEND_TITLE="推荐";
    private static final String PAGE_FOOD_TITLE="饮食";
    private static final String PAGE_HEALTHY_TITLE="健康";
    private static final String PAGE_TECHNOLOGY_TITLE="科学";
    private static  final String PAGE_COLLECTION_TITLE="印迹"; 
    public MyFragmentPageAdapter(FragmentManager fm, Handler handler) {
        super(fm);
        this.handler=handler;
    }
    /**
     * 设置viewpager里面的内容，此处是设置一个fragment，来填充内容
     * @param position 表示第几项内容
     * @return 返回创建的fragment
     */
    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            return MainFragment.newInstance(handler);
        }
        if(position==1)
        {
            return FoodFragment.newFoodFragmentInstance(handler);
        }
        if(position==2)
        {
            return HealthyFragment.newHealthyFragmentInstance(handler);
        }
        if(position==3)
        {
            return TechnologyFragment.newTechnologyFragmentInstance(handler);
        }
        else
        {
            return CollectionFragment.newCollectionFragmentInstance(handler);
        }
    }

    /**
     * 设置内容的数目
     * @return
     */
    @Override
    public int getCount() {
        return 5;
    }

    /**
     * 设置tab栏对应的标题
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position)
        {
            case 0:
                return PAGE_RECOMMEND_TITLE;
            case 1:
                return PAGE_FOOD_TITLE;
            case 2:
                return PAGE_HEALTHY_TITLE;
            case 3:
                return PAGE_TECHNOLOGY_TITLE;
            case 4:
                return PAGE_COLLECTION_TITLE;
            default:
                break;
        }
        return "";
    }
}
