package cn.piesat.waterconservation;

import android.app.Application;

import cn.piesat.waterconservation.utils.SpHelper;

/**
 * Created by sen.luo on 2018/6/21.
 */

public class WCApplication extends Application{
    private static WCApplication instance;

    public  static WCApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        SpHelper.init(this);
    }
}
