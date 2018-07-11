package cn.piesat.waterconservation.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import butterknife.BindView;
import cn.piesat.waterconservation.BuildConfig;
import cn.piesat.waterconservation.MainActivity;
import cn.piesat.waterconservation.R;
import cn.piesat.waterconservation.base.BaseActivity;
import cn.piesat.waterconservation.view.LoadDataView;

/**
 * Created by sen.luo on 2018/6/21.
 */

public class SplashActivity extends BaseActivity{

    @BindView(R.id.tvWelcome)
    TextView tvWelcome;

    @Override
    protected int layoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        tvWelcome.setText("水保 V"+ BuildConfig.VERSION_NAME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }



    @Override
    protected void initData() {

    }



    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }


}
