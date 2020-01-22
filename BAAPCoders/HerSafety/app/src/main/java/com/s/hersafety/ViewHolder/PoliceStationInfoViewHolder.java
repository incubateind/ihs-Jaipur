package com.s.hersafety.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s.hersafety.Interface.ItemClickListener;
import com.s.hersafety.R;

public class PoliceStationInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtArea, txtDistance, txtIncharge, txtPhone;
    public Button btnSOS, btnCall, btnDirection;
    private ItemClickListener itemClickListener;

    public PoliceStationInfoViewHolder(@NonNull View itemView) {
        super(itemView);
        txtArea = itemView.findViewById(R.id.txtArea);
        txtDistance = itemView.findViewById(R.id.txtDistance);
        txtIncharge = itemView.findViewById(R.id.txt_in_charge);
        txtPhone = itemView.findViewById(R.id.txtPhone);
        btnCall = itemView.findViewById(R.id.btnCall);
        btnDirection = itemView.findViewById(R.id.btnDirection);
        btnSOS = itemView.findViewById(R.id.btnSOS);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
