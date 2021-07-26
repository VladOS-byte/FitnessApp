package com.example.project05122017;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrayAdapterForCommunities extends BaseAdapter {
    ArrayList<Communit> communitArrayList;
    LayoutInflater inflater;
    Context context;
    boolean a=true;
    public ArrayAdapterForCommunities(Context context, ArrayList<Communit> arrayList) {
        communitArrayList=arrayList;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return communitArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return communitArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(view==null)
            view=inflater.inflate(R.layout.mylist_item0,parent,false);
        Communit c=getCommunit(position);
        if (c.status==1){
            view.setBackgroundColor(Color.LTGRAY);
            if(a){
                view.setFocusable(true);
                a=false;}
        }
        ((TextView)view.findViewById(R.id.tvlog)).setText(c.log);
        ((TextView)view.findViewById(R.id.tvmes)).setText(c.colMes);
        return view;
    }
    Communit getCommunit(int position){
        return ((Communit)getItem(position));
    }
}
