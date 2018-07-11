package cn.piesat.waterconservation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.piesat.waterconservation.MainActivity;
import cn.piesat.waterconservation.R;
import cn.piesat.waterconservation.base.BaseFragment;
import cn.piesat.waterconservation.bean.JsonBean;
import cn.piesat.waterconservation.bean.MapDataBean;
import cn.piesat.waterconservation.constants.Config;
import cn.piesat.waterconservation.constants.Constants;
import cn.piesat.waterconservation.constants.SysConstant;
import cn.piesat.waterconservation.utils.FileWriteUtils;
import cn.piesat.waterconservation.utils.GetJsonDataUtil;
import cn.piesat.waterconservation.utils.LocationUtils;
import cn.piesat.waterconservation.utils.LogUtils;
import cn.piesat.waterconservation.utils.ToastUtil;
import cn.piesat.waterconservation.view.BottomListDialog;
import cn.piesat.waterconservation.view.progress.ProgressDialogTool;
import pie.core.Dataset;
import pie.core.DatasetVector;
import pie.core.Layer;
import pie.core.LayerSet;
import pie.core.Recordset;

/**
 * 图斑详情
 * Created by sen.luo on 2018/6/21.
 */

public class FigureDetailFragment extends Fragment{
    @BindView(R.id.btSave)
    Button btSave;
    @BindView(R.id.btCancel)
    Button btCancel;

    @BindView(R.id.tvRaoDongBianHua)
    TextView tvRaoDongBianHua;
    @BindView(R.id.tvJianShe)
    TextView tvJianShe;
    @BindView(R.id.tvRaoDongTuBan)
    TextView tvRaoDongTuBan;
    @BindView(R.id.tvRaoDongHeGui)
    TextView tvRaoDongHeGui;

    @BindView(R.id.tvReplyDate)
    TextView tvReplyDate; //批复时间

    @BindView(R.id.etProjectName)
    EditText etProjectName; //项目名称
    @BindView(R.id.tvCity)
    TextView tvCity;

    @BindView(R.id.imgAdd)
    ImageView imgAdd;

    @BindView(R.id.tvNamber)
    TextView tvNamber;

    private MapDataBean mapDataBean;
    private MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_figure_detail, container, false);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }


    public void setMainActivity(MainActivity activity){
        mainActivity=activity;
    }


    private void initView() {
        Bundle bundle=getArguments();
        mapDataBean= (MapDataBean) bundle.getSerializable("key");
        tvNamber.setText("图斑编号："+mapDataBean.QDNM);
        etProjectName.setText(mapDataBean.PRNM); //项目名称

    }

    @OnClick({R.id.layoutRaoDong,R.id.layoutJianShe,
            R.id.layoutRaoDongTuBan,R.id.layoutRaoDongHeGui,R.id.tvReplyDate,R.id.layoutCity,R.id.btSave,R.id.btCancel,R.id.imgAdd})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.imgAdd: //添加照片
                ToastUtil.showShort(getActivity(),"正在打开相机");
                openCamera();
                break;

            case R.id.btSave://保存
                if (mainActivity.getMapViewData()!=null) {
                    String sql = " PIEID  = " + mapDataBean.pieId;
                    LayerSet layerSet = mainActivity.getMapViewData().getLayerSet(SysConstant.PropertyName.SLTCJ);
                    Layer layer = layerSet.getLayer(0);
                    Dataset dataset = layer.getDataset();

                    dataset.open();
                    boolean a =dataset.isOpen();
                    DatasetVector vector = (DatasetVector) dataset;
                    Log.d("写入图版数量", "getObjectCount: " + vector.getObjectCount());
                    Recordset recordset = vector.query(sql);
                    int recordCount = recordset.getRecordCount();

                    boolean b = recordset.setFieldValueInt("PRNM", 2);

                    if (b) {
                        ToastUtil.showShort(getActivity(), "写入成功");
                    } else {
                        ToastUtil.showShort(getActivity(), "写入失败");
                    }

                }

                break;

            case R.id.btCancel:// 取消

                break;

            case R.id.layoutRaoDong: //扰动变化类型
                BottomListDialog raoDiaLog = new BottomListDialog(getActivity(),tvRaoDongBianHua.getText().toString(),"请选择扰动变化类型", Constants.RAO_DONG_BIAN_HUA);
                raoDiaLog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvRaoDongBianHua.setText(Constants.RAO_DONG_BIAN_HUA[position]);
                    }
                });
                raoDiaLog.show();

                break;

            case R.id.layoutJianShe: //建设状态
                BottomListDialog jianDiaLog = new BottomListDialog(getActivity(),tvJianShe.getText().toString(),"请选择建设状态", Constants.JIAN_SHE_ZHUANG_TAI);
                jianDiaLog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvJianShe .setText(Constants.JIAN_SHE_ZHUANG_TAI[position]);
                    }
                });
                jianDiaLog.show();
                break;

            case R.id.layoutRaoDongTuBan: //扰动图斑类型
                BottomListDialog TuDiaLog = new BottomListDialog(getActivity(),tvRaoDongTuBan.getText().toString(),"请选择扰动图斑类型", Constants.RAO_DONG_TU_BAN);
                TuDiaLog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvRaoDongTuBan .setText(Constants.RAO_DONG_TU_BAN[position]);
                    }
                });
                TuDiaLog.show();
                break;

            case R.id.layoutRaoDongHeGui: //扰动合规性
                BottomListDialog HeDiaLog = new BottomListDialog(getActivity(),tvRaoDongHeGui.getText().toString(),"请选择扰动合规性", Constants.RAO_DONG_HE_GUI);
                HeDiaLog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tvRaoDongHeGui.setText(Constants.RAO_DONG_HE_GUI[position]);
                    }
                });
                HeDiaLog.show();

                break;

            case R.id.tvReplyDate : //批复时间
                DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvReplyDate.setText("批复时间："+year+"年"+(month+1)+"月"+dayOfMonth+"日");
                    }
                }, Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH);

                DatePicker datePicker=datePickerDialog.getDatePicker();
                datePicker.setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;



            case R.id.layoutCity: //时间选择

                if (isLoaded){
                    showPickerView();
                }else {
                  ProgressDialogTool.showDialog(getActivity());
                    mHandler.sendEmptyMessage(MSG_LOAD_DATA);
                }


                break;
        }
    }

    /**
     * 写入照片坐标值
     * @param photoName
     */
    private void writeTXT(String photoName) {
        LocationUtils.initLocation(getActivity());
        String longitude= String.valueOf(LocationUtils.longitude);
        String latitude=String.valueOf(LocationUtils.latitude);
        String filePath = Config.getProjectPath()+"/image/";
        String fileName = "经纬度.txt";
        FileWriteUtils.writeTxtToFile(photoName+"坐标值："+latitude+","+longitude,filePath,fileName);

    }



    /**
     * 调用系统相机
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            LogUtils.show("拍照",
                    "sd卡不可用.");
            return;
        }

        if (data!=null){
            Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
        FileOutputStream b = null;
        File file = new File(Config.getProjectPath()+"/image/");
        file.mkdirs();// 创建文件夹
        String name = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String fileName =Config.getProjectPath()+"/image/"+name;

        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            writeTXT(fileName); //写入当前照片坐标

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            imgAdd.setImageBitmap(bitmap);// 将图片显示在ImageView里
        }catch(Exception e)
        {
            LogUtils.show("拍照error", e.getMessage());
        }

        }

    }

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded=false;

    private Handler mHandler  =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOAD_DATA:
                    if (thread==null){
                        thread=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded=true;
                    ProgressDialogTool.dismissDialog();
                    showPickerView();
                    break;

                case MSG_LOAD_FAILED:
                    ProgressDialogTool.dismissDialog();
                    ToastUtil.showShort(getActivity(),"城市数据加载有误，稍后再试");
                    break;


            }
        }
    };



    /**
     * 加载本地城市json数据
     */
    private void initJsonData() {
        String cityJson =new GetJsonDataUtil().getJson(getActivity(),"province.json");
        ArrayList<JsonBean> jsonBean = parseData(cityJson);
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    /**
     * 显示地区选择器
     */
    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
//                ToastUtil.showShort(MainActivity.this,tx);
                tvCity.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setSubmitText("确定")
                .setCancelText("取消")
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    /**
     *
     *解析城市json
     * @param result
     * @return
     */
    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


}
