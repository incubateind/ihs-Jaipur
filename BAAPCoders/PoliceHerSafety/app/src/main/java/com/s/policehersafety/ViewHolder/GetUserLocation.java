package com.s.policehersafety.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s.policehersafety.R;

public class GetUserLocation extends RecyclerView.ViewHolder {

    public TextView txtName, txtAddress, txtPhone;
    public Button btnGetLocationUpdate;

    public GetUserLocation(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName);
        txtAddress = itemView.findViewById(R.id.txtAddress);
        txtPhone = itemView.findViewById(R.id.txtPhone);

        btnGetLocationUpdate = itemView.findViewById(R.id.btnGetLocationUpdate);
    }

}
