package cn.piesat.waterconservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.piesat.waterconservation.adapter.FigureSpotsAdapter;
import cn.piesat.waterconservation.bean.MapDataBean;
import cn.piesat.waterconservation.constants.Config;
import cn.piesat.waterconservation.constants.Constants;
import cn.piesat.waterconservation.fragment.FigureDetailFragment;
import cn.piesat.waterconservation.fragment.MapViewFragment;
import cn.piesat.waterconservation.utils.LogUtils;
import cn.piesat.waterconservation.utils.MapUtil;
import cn.piesat.waterconservation.utils.ToastUtil;
import pie.core.MapView;


public class MainActivity extends AppCompatActivity implements FigureSpotsAdapter.GetCallBack{

    @BindView(R.id.rvFigureSpots)
    ListView rvFigureSpots;


    private List<MapDataBean> mapDataBeanList;
    private FigureSpotsAdapter figureSpotsAdapter;

    private MapViewFragment mapViewFragment;
    private FigureDetailFragment detailFragment;

    private FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtil.getInstace().init(this,  Config.getMapResoucePath());
        setContentView( R.layout.activity_main);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        EventBus.getDefault().register(this);
        mapDataBeanList=new ArrayList<>();
        figureSpotsAdapter=new FigureSpotsAdapter(this,mapDataBeanList);
        rvFigureSpots.setAdapter(figureSpotsAdapter);
        figureSpotsAdapter.setCallBack(this);

        if (mapViewFragment==null){
            mapViewFragment=new MapViewFragment();
        }
        mapViewFragment.setActivity(this);
        switchFragment(mapViewFragment);

    }

    public MapView getMapViewData(){
        if (mapViewFragment!=null){
            return mapViewFragment.getMapView();
        }

        return null;
    }



    /**
     * Adapter 回调
     * @param
     */

    private MapDataBean dataBean;
    @Override
    public void getCallBackMsg(int  position) {

        if (currentFragment!=null &&currentFragment instanceof FigureDetailFragment){
            switchFragment(mapViewFragment);
        }
        dataBean =mapDataBeanList.get(position);
        EventBus.getDefault().post(dataBean, Constants.EventBus.MAP_VIEW_DATA_SUMMARY);
    }


    /**
     * MapFragment 点击编辑重新替换Fragment
     */
    @Subscriber(mode = ThreadMode.MAIN,tag = Constants.EventBus.MAP_FRAGMENT_CAll_BACK)
    public void fragmentCallBack(String msg){

        LogUtils.show("---",msg);

        detailFragment=new FigureDetailFragment();
        detailFragment.setMainActivity(MainActivity.this);
        Bundle bundle =new Bundle();
        bundle.putSerializable("key",dataBean);
        detailFragment.setArguments(bundle);
        switchFragment(detailFragment);
    }


    /**
     * 回传List
     * @param mapDataBeans
     */
    @Subscriber(mode = ThreadMode.MAIN,tag =Constants.EventBus.MAP_DATA_LIST_CAll_BACK)
    public void getMapDataLit(List<MapDataBean> mapDataBeans){
        LogUtils.show("---------","收到回传List");
        mapDataBeanList.clear();
        mapDataBeanList.addAll(mapDataBeans);
        figureSpotsAdapter.notifyDataSetChanged();
    }



    private Fragment currentFragment;
    public void switchFragment(Fragment to) {
            initFragmentTransaction();
        if (currentFragment != to) {
            if (currentFragment == null) {
                fragmentTransaction.add(R.id.frameLayout, to).commitAllowingStateLoss();
                currentFragment = to;
                fragmentTransaction = null;
                return;
            }

            if (!to.isAdded()) {
                fragmentTransaction.hide(currentFragment).add(R.id.frameLayout, to).commitAllowingStateLoss();
            } else {
                fragmentTransaction.hide(currentFragment).show(to).commitAllowingStateLoss();
            }
        }
        currentFragment = to;
        fragmentTransaction = null;

    }
    private void initFragmentTransaction() {
        if (fragmentTransaction == null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    @Override
    public void onBackPressed() {

        if (mBackPressed+TIME_INTERVAL >System.currentTimeMillis()){
            finish();
            return;
        }else {
            ToastUtil.showShort(this,"再按一次返回键退出程序");
        }

        mBackPressed=System.currentTimeMillis();

    }

}
