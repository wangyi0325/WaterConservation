package cn.piesat.waterconservation.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import cn.piesat.waterconservation.MainActivity;
import cn.piesat.waterconservation.activity.SplashActivity;
import cn.piesat.waterconservation.constants.Config;
import cn.piesat.waterconservation.utils.MapUtil;
import cn.piesat.waterconservation.view.LoadDataView;

/**
 * Created by sen.luo on 2018/6/21.
 */

public abstract class BaseActivity extends AppCompatActivity{
    protected abstract int layoutId();
    protected abstract ViewGroup loadDataViewLayout();
    protected abstract void getLoadView(LoadDataView loadView);
    protected abstract void initView();
    protected abstract void initData();

    private LoadDataView mLoadView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setStatusBar();
        if (this instanceof SplashActivity){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

//        if (this instanceof MainActivity){ //加载布局前初始化地图数据
//            MapUtil.getInstace().init(this,  Config.getMapResoucePath());
//        }
        setContentView(layoutId());
        ButterKnife.bind(this);
        initView();
        initViewGroup();
        initData();

    }


    /**
     * 嵌入LoadDataView
     */
    private void initViewGroup() {
        ViewGroup view = loadDataViewLayout();
        if (null != view) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
                this.mLoadView = new LoadDataView(this, view);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                //                params.addRule(RelativeLayout.BELOW, R.id.include_topbar);
                //                params.setMargins(0, 0, 0, 0);
                viewGroup.addView(this.mLoadView, params);
                getLoadView(this.mLoadView);
            }
        }
    }

    /**
     * 设置状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
