package com.s.policehersafety.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s.policehersafety.R;

public class UserVH extends RecyclerView.ViewHolder {

    public TextView txtName;

    public UserVH(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtUserName);

    }

}