package com.omeletlab.argora.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omeletlab.argora.R;
import com.omeletlab.argora.activity.CropStateYearAnalysisActivity;
import com.omeletlab.argora.activity.MainActivity;
import com.omeletlab.argora.model.Crop;
import com.omeletlab.argora.util.GlobalConstant;

import java.util.List;

/**
 * Created by akashs on 10/21/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CropViewHolder> {
    List<Crop> cropList;
    Activity mActivity;

    public RVAdapter(List<Crop> cropList, Activity activity)
    {
        this.cropList = cropList;
        this.mActivity = activity;
    }

    @Override
    public CropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_card, parent, false);
        CropViewHolder pvh = new CropViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CropViewHolder holder, final int position) {
        holder.cropName.setText(cropList.get(position).getCropName()+"("+cropList.get(position).getStateName()+")");
        holder.cropValue.setText(String.valueOf(cropList.get(position).getValue()));

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mActivity, CropStateYearAnalysisActivity.class);
                in.putExtra(GlobalConstant.TAG_commodity_desc, cropList.get(position).getCropName());
                in.putExtra(GlobalConstant.TAG_state_name, cropList.get(position).getStateName());
                mActivity.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cropList != null) {
            return cropList.size();
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class CropViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView cropName;
        TextView cropValue;

        CropViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            cropName = (TextView) itemView.findViewById(R.id.person_name);
            cropValue = (TextView) itemView.findViewById(R.id.person_age);
        }
    }


}
