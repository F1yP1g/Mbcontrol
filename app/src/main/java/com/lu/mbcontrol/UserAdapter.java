package com.lu.mbcontrol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LU on 2018/5/10.
 */

public class UserAdapter extends ArrayAdapter<tejiaUser> {
        private int layoutId;
    public UserAdapter(Context context, int layoutId, List<tejiaUser> list){
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tejiaUser tU = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        TextView tv_phone = (TextView) view.findViewById(R.id.TV_phoneNumber_user);
        TextView tv_password = (TextView) view.findViewById(R.id.TV_password_user);
        TextView tv_osversion = (TextView) view.findViewById(R.id.TV_osversion_user);
        TextView tv_phonetype = (TextView) view.findViewById(R.id.TV_phonetype_user);
        tv_phone.setText(tU.getPhone());
        tv_password.setText(tU.getPwd());
        tv_osversion.setText(tU.getVersion());
        tv_phonetype.setText(tU.getPhonetype());
        return view;
    }
}
