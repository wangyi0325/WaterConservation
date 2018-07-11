package cn.piesat.waterconservation.constants;

import java.util.List;

/**
 * Created by sen.luo on 2018/6/22.
 */

public class Constants {

    //扰动合规
    public static final String[] RAO_DONG_HE_GUI={"合规","未批先建","超出防治责任范围","建设地点变"};

    //扰动图斑
    public static final String[] RAO_DONG_TU_BAN={"弃渣场","其他扰动"};

    //建设状态
    public static final String[] JIAN_SHE_ZHUANG_TAI={"施工","停工","完工"};

    //扰动变化类型
    public static final String[] RAO_DONG_BIAN_HUA={"新增","停工","扰动范围扩大","扰动范围缩小","扰动范围不变"};



    public class EventBus{

        public static final String MAP_VIEW_DATA_SUMMARY="map_view_data_summary"; //显示地图简介
        public static final String MAP_FRAGMENT_CAll_BACK="map_fragment_call_back"; //地图Fragment回传值
        public static final String MAP_DATA_LIST_CAll_BACK="map_data_list_call_back"; //MapDataBean回调


    }
}
