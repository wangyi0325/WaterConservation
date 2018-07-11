package cn.piesat.waterconservation.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.piesat.waterconservation.MainActivity;
import cn.piesat.waterconservation.Presenter.MainViewContract;
import cn.piesat.waterconservation.Presenter.MainViewPresenter;
import cn.piesat.waterconservation.R;
import cn.piesat.waterconservation.base.BaseFragment;
import cn.piesat.waterconservation.bean.MapDataBean;
import cn.piesat.waterconservation.constants.Constants;
import cn.piesat.waterconservation.constants.SysConstant;
import cn.piesat.waterconservation.entity.TaskEntity;
import cn.piesat.waterconservation.utils.DialogUtils;
import cn.piesat.waterconservation.utils.FileUtil;
import cn.piesat.waterconservation.utils.LogUtils;
import cn.piesat.waterconservation.utils.MapUtil;
import cn.piesat.waterconservation.utils.SpHelper;
import cn.piesat.waterconservation.utils.ToastUtil;
import cn.piesat.waterconservation.view.LoadDataView;
import cn.piesat.waterconservation.view.progress.ProgressDialogTool;
import pie.core.Dataset;
import pie.core.DatasetVector;
import pie.core.FieldInfo;
import pie.core.Layer;
import pie.core.LayerSet;
import pie.core.MapEditView;
import pie.core.MapScaleChangedListener;
import pie.core.MapView;
import pie.core.Point2D;
import pie.core.QueryDef;
import pie.core.Recordset;
import pie.core.Rect2D;
import pie.core.Selection;
import pie.map.MapViews;
import pie.map.gesture.MapGestureController;

/**
 * 地图详情
 * Created by sen.luo on 2018/6/21.
 */

public class MapViewFragment extends Fragment implements MainViewContract.View{

    @BindView(R.id.imgNavigation)
    ImageView imgNavigation;

    @BindView(R.id.layoutMapView)
    RelativeLayout layoutMapView;  //地图布局

    @BindView(R.id.imgEdit)
    ImageView imgEdit;

    @BindView(R.id.tvTuBanName)
    TextView tvTuBanName; //图斑名

    @BindView(R.id.tvEditing)
    TextView tvEditing;

    @BindView(R.id.mapView)
    MapViews mapViews;

    @BindView(R.id.tv_pie_scale_value)
    TextView tv_pie_scale_value;

    @BindView(R.id.layoutMapSummary)
    LinearLayout layoutMapSummary; //地图简介信息

    @BindView(R.id.tvProjectName)
    TextView tvProjectName;


    private List<TaskEntity> listEntitiesIng = new ArrayList<>();
    private List<TaskEntity> listEntitiesNo = new ArrayList<>();
    private List<TaskEntity> listEntitiesEnd = new ArrayList<>();


    private MainViewPresenter m_presenter;
    private pie.core.MapView mSwitchMapView;
    private MapEditView m_mapEditView;


    private SharedPreferences sper;
    private MainActivity mainActivity;
    private DatasetVector dataset;
    private Layer layer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    public void setActivity(MainActivity activity) {
        mainActivity = activity;
    }



    private void initView() {
        EventBus.getDefault().register(this);
        sper = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        m_presenter = new MainViewPresenter(this);
        mSwitchMapView = mapViews.getMapView();
        m_mapEditView = mapViews.getMapEditView();
        m_mapEditView.setEditHandleSize(500);
        m_mapEditView.setSelectionTolerance(100);
        mSwitchMapView.setMapBkgColor(R.color.color_72AAD2);

        mSwitchMapView.setIsScale(true);
        mSwitchMapView.setMapGestureController(new MapGestureController());
        mSwitchMapView.setMapScaleChangedListener(new ScaleValueChangedListener());

        ProgressDialogTool.showDialog(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {

                String prjName = taskID();
                if(!prjName.equals("Null")){
                    SpHelper.setStringValue("ID",prjName);
                    openMap(prjName);
                }else {
                    if (listEntitiesIng.size() > 0) {
                        String taskNumber = listEntitiesIng.get(0).getTaskNumber();
                        SpHelper.setStringValue("ID",taskNumber);
                        openMap(taskNumber);
                    } else if (listEntitiesNo.size() > 0) {
                        String taskNumber = listEntitiesNo.get(0).getTaskNumber();
                        SpHelper.setStringValue("ID",taskNumber);
                        openMap(taskNumber);
                    } else if (listEntitiesEnd.size() > 0) {
                        String taskNumber = listEntitiesEnd.get(0).getTaskNumber();
                        SpHelper.setStringValue("ID",taskNumber);
                        openMap(taskNumber);
                    } else {
//                        openMap("T060220180529003");
                        openMap("T020620180629003");
                    }
                }

                mHandler.sendEmptyMessage(MSG_LOAD_MAP);

            }
        }).start();

    }




    @Subscriber(mode = ThreadMode.MAIN,tag = Constants.EventBus.MAP_VIEW_DATA_SUMMARY)
    public void onDataCallBack(MapDataBean  mapDataBean){

        if (layoutMapSummary.getVisibility()!=View.VISIBLE){
            layoutMapSummary.setVisibility(View.VISIBLE);
        }

        tvTuBanName.setText(mapDataBean.QDNM);
        tvProjectName.setText("项目名称："+mapDataBean.PRNM);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }

//
//        // 先关闭地图，再销毁地图，再关闭工作区间
//        if (mSwitchMapView != null) {
//            mSwitchMapView.closeMap();
//            mSwitchMapView.destroyMapWindow();
//            mSwitchMapView = null;
//        }
//        MapUtil.getInstace().getWorkspace().close();
    }

    @OnClick({R.id.ib_pie_zoom_in,R.id.ib_pie_zoom_out,R.id.imgEdit,R.id.imgNavigation,R.id.ivfullScreen})
    public void onViewClick(View view) {
        switch (view.getId()) {

            case R.id.ivfullScreen: //全屏
                 LogUtils.show("全屏","----");
                layoutMapSummary.setVisibility(layoutMapSummary.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);

                break;

            case R.id.ib_pie_zoom_in: //地图放大
                mSwitchMapView.zoomIn();
                LogUtils.show("按钮放大", mSwitchMapView.getScale() + "");

                break;

            case R.id.ib_pie_zoom_out://地图缩小
                mSwitchMapView.zoomOut();
                LogUtils.show("按钮缩小", mSwitchMapView.getScale() + "");
                break;


            case R.id.imgEdit: //编辑
                EventBus.getDefault().post("切换详情Fragment",Constants.EventBus.MAP_FRAGMENT_CAll_BACK);
                break;


            case R.id.imgNavigation: //导航

                if (FileUtil.isPackageInstalled(getActivity(),"com.baidu.BaiduMap")){
                    ToastUtil.showShort(getActivity(),"请稍后，正在打开百度地图");
                    StringBuffer stringBuffer = new StringBuffer("baidumap://map/geocoder?location=")
                            .append("34.2685154349").append(",").append("108.9339934089").append("&type=TIME");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
                    intent.setPackage("com.baidu.BaiduMap");
                    startActivity(intent);
                }else {
                    ToastUtil.showShort(getActivity(),"请安装百度地图");
                }

                break;

        }
    }



    private static final int MSG_LOAD_MAP = 0x0004;
    private Handler mHandler  =new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_LOAD_MAP:
                        ProgressDialogTool.dismissDialog();
                        setMap();
                        getVectorData();
                    break;
                }
            }
        };

    /**
     * 获取矢量数据
     */
    List<MapDataBean>mapDataList;
    private void getVectorData() {
        mapDataList=new ArrayList<>();
        //获取所有的矢量数据
        LayerSet layerSet = mSwitchMapView.getLayerSet(SysConstant.PropertyName.SLTCJ);
        //获取矢量数据的条目数
        int layerCount = layerSet.getLayerCount();
        //根据条目索引获取Layer；
        for (int y = 0; y < layerCount; y++) {
            Layer layer = layerSet.getLayer(y);
            LogUtils.show("tbbh", "msPoint: "+layer.getName());

            Dataset dataset = layer.getDataset();
            dataset.open();
            DatasetVector vector = (DatasetVector) dataset;
            Log.d("图版数量", "getObjectCount: "+ vector.getObjectCount());

            for (int i = 1;i<vector.getObjectCount();i++){
                MapDataBean dataBean =new MapDataBean();
                String sql = " PIEID  = " + i;
                Recordset recordset = vector.query(sql);
                dataBean.pieId=i+"";
                dataBean.PRNM=recordset.getFieldValueString("PRNM");
                LogUtils.show("项目名称---",dataBean.PRNM);
                dataBean.QDNM=recordset.getFieldValueString("QDNM");
                dataBean.DPOZ=recordset.getFieldValueString("DPOZ");
                dataBean.PRTYPE=recordset.getFieldValueString("PRTYPE");
                dataBean.SEAA=recordset.getFieldValueString("SEAA");
                dataBean.SADT=recordset.getFieldValueString("SADT");
                dataBean.QTYPE=recordset.getFieldValueString("QTYPE");
                dataBean.QDCS=recordset.getFieldValueString("QDCS");
                mapDataList.add(dataBean);

            }
        }

        EventBus.getDefault().post(mapDataList,Constants.EventBus.MAP_DATA_LIST_CAll_BACK);


    }


    /**根据四方位确定加载范围*/
    private void setMap() {
        LayerSet layerSet = mSwitchMapView.getLayerSet(SysConstant.PropertyName.DTTC);
        if(layerSet != null){
            int layerCount = layerSet.getLayerCount();
            for(int i = 0;i<layerCount;i++){
                Layer layer = layerSet.getLayer(i);
                Rect2D bounds = layer.getDataset().getBounds();
                double bottom = bounds.getBottom();
                double top = bounds.getTop();
                double left = bounds.getLeft();
                double right = bounds.getRight();
                mSwitchMapView.setViewBounds(new Rect2D(left,top,right,bottom));
                mSwitchMapView.setScale(mSwitchMapView.getMinScale()*90);//设置默认的加载比例尺
                mSwitchMapView.setMapCenter(bounds.getCenter());
                mSwitchMapView.setMinScale(mSwitchMapView.getMinScale()*90);//设置最小缩放比例
            }
        }else{
            ToastUtil.showShort(getActivity(),"请添加瓦片数据");
        }
    }




    public String taskID() {
        String taskID = sper.getString("TASK_ID", "Null");
        return taskID;
    }


    private void openMap(String taskNumber) {
        if(MapUtil.getInstace().getWorkspace() == null){
            m_presenter.start(taskNumber);
        }else{
            mSwitchMapView.closeMap();
            m_presenter.start(taskNumber);
        }
    }

    /**
     * 地图缩放回调
     *
     * @author pie
     */
    private class ScaleValueChangedListener implements MapScaleChangedListener {

        @Override
        public void onScaleChanged(double scale) {
            Log.e("手势缩小",""+scale);
            setScaleTextView();
        }

    }
    /**
     * 设置显示缩放比例 TextView
     */
    private void setScaleTextView() {
        int value = getCurrentScaleValue() / 100;// 厘米 换算成米
        if (value < 1000) {
            tv_pie_scale_value.setText(value + "m");
        } else {
            double valueF = value / 1000.00;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String dfValue = decimalFormat.format(valueF);
            tv_pie_scale_value.setText(dfValue + "km");
        }
    }

    /**
     * 获取当前比例尺 对应 1：value
     *
     * @return 1:value,value值（单位 cm）
     */
    private int getCurrentScaleValue() {
        double scale = mSwitchMapView.getScale();
        int scaleValue = (int) (1 / scale);
        return scaleValue;
    }



    @Override
    public void switchFragment(String flag) {
    }

    @Override
    public MapView getMapView() {
        return mSwitchMapView;
    }

    @Override
    public MapEditView getMapEditView() {
        return m_mapEditView;
    }

    @Override
    public void removeGeometryByTag(String name) {
        mSwitchMapView.getTrackingLayer().removeGeometryByName(name);
    }

    @Override
    public Layer getEditLayer() {
        return null;
    }

    @Override
    public int saveGpsRecordToDataset(List<Point2D> m_gpsPointList) {
        return 0;
    }

    @Override
    public void setPresenter(MainViewContract.Presenter presenter) {

    }

    @Override
    public void showToast(String msg) {

    }



}
