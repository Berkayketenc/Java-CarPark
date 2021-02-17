package com.infobk.carpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Model> {

    ArrayList<Model>modelList;
    Context context;

    public Adapter(@NonNull Context context, ArrayList<Model>modelList) {
        super(context, R.layout.adapter_list,modelList);

        this.modelList=modelList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View customView = layoutInflater.inflate(R.layout.adapter_list,parent,false);
        TextView nameTextView =  customView.findViewById(R.id.nameTextView);
        nameTextView.setText(modelList.get(position).name);
        return customView;
    }
}
