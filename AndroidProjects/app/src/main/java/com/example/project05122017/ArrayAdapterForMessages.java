package com.example.project05122017;

import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArrayAdapterForMessages extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<Message> arrayList;
    Context context;
    String date="01/01/1981";
    boolean a=true;
    String logme;
    ArrayAdapterForMessages(Context context, ArrayList<Message> arrayList){
        this.context=context;
        this.arrayList=arrayList;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if (view==null)
            view=inflater.inflate(R.layout.listrow,parent,false);
        Message m=getMessage(position);
        DisplayMetrics dm=context.getResources().getDisplayMetrics();
        TextPaint tp=new TextPaint();
        float widthText=tp.measureText(m.message);
        tp.setTextSize(14);
        (view.findViewById(R.id.date)).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        (view.findViewById(R.id.iv)).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        (view.findViewById(R.id.fiv)).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        (view.findViewById(R.id.fivlog)).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        (view.findViewById(R.id.ivlog)).setLayoutParams(new RelativeLayout.LayoutParams(0,0));

        if(!(date.equals(m.date))){
            date=m.date;
            (view.findViewById(R.id.date)).setLayoutParams(new RelativeLayout.LayoutParams((int)(100*dm.density),(int)(25*dm.density)));
            ((TextView)view.findViewById(R.id.date)).setHeight((int)(25*dm.density));
            if(date.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
                ((TextView)view.findViewById(R.id.date)).setText("Сегодня");
            ((TextView)view.findViewById(R.id.date)).setText(date);
        }
        /*if(m.login.equals(m.loginme)){
            if (m.status==1)
                view.setBackgroundColor(Color.LTGRAY);
            (view.findViewById(R.id.fiv)).setLayoutParams(new RelativeLayout.LayoutParams((int)(80*dm.density),(int)(25*dm.density)));
            ((TextView)view.findViewById(R.id.fiv)).setText(m.time);
            if(widthText>=dm.widthPixels-90*dm.density){
                (view.findViewById(R.id.iv)).setLayoutParams(new RelativeLayout.LayoutParams((int)(Math.ceil(dm.widthPixels-50*dm.density)),(int)(Math.ceil(6*dm.density+widthText/(dm.widthPixels-90*dm.density)))));
            }
            else
                (view.findViewById(R.id.iv)).setLayoutParams(new RelativeLayout.LayoutParams((int)(Math.ceil(40*dm.density+widthText)),(int)(Math.ceil(6*dm.density+widthText/(dm.widthPixels-90*dm.density)))));
            ((TextView)view.findViewById(R.id.iv)).setText(m.message);}
        else{
            if (m.status==1&&a)
                view.setFocusable(true);
            ((TextView)view.findViewById(R.id.fivlog)).setText(m.time);
            (view.findViewById(R.id.fivlog)).setLayoutParams(new RelativeLayout.LayoutParams((int)(80*dm.density),(int)(25*dm.density)));
            tp.setTextSize(14);
            if(widthText>=dm.widthPixels-90*dm.density){
                (view.findViewById(R.id.ivlog)).setLayoutParams(new RelativeLayout.LayoutParams((int)(Math.ceil(dm.widthPixels-50*dm.density)),(int)(Math.ceil(6*dm.density+widthText/(dm.widthPixels-90*dm.density)))));
            }
            else
                (view.findViewById(R.id.ivlog)).setLayoutParams(new RelativeLayout.LayoutParams((int)(Math.ceil(40*dm.density+widthText)),(int)(Math.ceil(6*dm.density+widthText/(dm.widthPixels-90*dm.density)))));
            ((TextView)view.findViewById(R.id.ivlog)).setText(m.message);
        }*/
        return view;
    }
    private Message getMessage(int position){return ((Message)getItem(position));}
}
