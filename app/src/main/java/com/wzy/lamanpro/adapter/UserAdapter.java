package com.wzy.lamanpro.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.utils.WZYBaseAdapter;

import java.util.List;

public class UserAdapter extends WZYBaseAdapter<Users> {
    public UserAdapter(List<Users> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, Users users, int indexPostion) {
        TextView name = (TextView) holder.getView(R.id.name);
        TextView account = (TextView) holder.getView(R.id.account);
        TextView email = (TextView) holder.getView(R.id.email);
        name.setText(users.getName());
        account.setText(users.getAccount());
        email.setText(users.getEmail());

    }
}
