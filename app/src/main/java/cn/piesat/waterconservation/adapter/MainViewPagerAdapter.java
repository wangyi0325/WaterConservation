package cn.piesat.waterconservation.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by sen.luo on 2018/6/21.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> listragment;


    public MainViewPagerAdapter(FragmentManager fm, List<Fragment> listragment) {
        super(fm);
        this.listragment = listragment;
    }

    @Override
    public Fragment getItem(int position) {
        return listragment.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
