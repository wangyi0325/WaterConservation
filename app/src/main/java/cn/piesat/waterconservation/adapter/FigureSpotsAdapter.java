package cn.piesat.waterconservation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.piesat.waterconservation.R;
import cn.piesat.waterconservation.bean.MapDataBean;

/**
 * 图斑列表适配器
 * Created by sen.luo on 2018/6/21.
 */

public class FigureSpotsAdapter extends BaseAdapter{

    private List<MapDataBean> mapDataBeanList;
    private Context context;

    private GetCallBack getCallBack;
    private int mSelectedPos  = 0;

    public FigureSpotsAdapter(Context context,List<MapDataBean> figureList) {
        this.mapDataBeanList = figureList;
        this.context = context;
    }

    public void setCallBack(GetCallBack callBack){
        this.getCallBack=callBack;
    }

    @Override
    public int getCount() {
        return mapDataBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapDataBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FigureViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_figure_spots,null);
            holder=new FigureViewHolder();
            holder.tvContent=convertView.findViewById(R.id.tvContent);
            holder.layoutRoot=convertView.findViewById(R.id.layoutRoot);

            convertView.setTag(holder);
        }else {
            holder= (FigureViewHolder) convertView.getTag();

        }

        if (mapDataBeanList.get(position).RST!=null){ //是否复核
            holder.layoutRoot.setSelected(mapDataBeanList.get(position).RST.equals("1"));
        }

        if (position==0){
            holder.layoutRoot.performClick();
        }

        holder.tvContent.setText(mapDataBeanList.get(position).QDNM);

        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCallBack.getCallBackMsg(position);
            }
        });




        return convertView;
    }



    class FigureViewHolder {
        TextView tvContent;RelativeLayout layoutRoot;

    }


   public interface GetCallBack{
        void getCallBackMsg(int  position);
    }

}
