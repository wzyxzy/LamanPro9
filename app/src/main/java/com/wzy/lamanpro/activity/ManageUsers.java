package com.wzy.lamanpro.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.UserAdapter;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.dao.UserDaoUtils;

import java.util.List;

import static com.wzy.lamanpro.common.LaManApplication.isManager;

public class ManageUsers extends AppCompatActivity implements View.OnClickListener {

    //    private Toolbar toolbar;
    private ListView userList;
    private FloatingActionButton fab;
    private List<Users> users;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        initView();
        initData();
    }

    private void initData() {
        users = new UserDaoUtils(this).queryAllUsers();
        userAdapter = new UserAdapter(users, this, R.layout.item_users);
        userList.setAdapter(userAdapter);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageUsers.this, UserDetails.class);
                intent.putExtra("account", users.get(position).getAccount());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        users = new UserDaoUtils(this).queryAllUsers();
        userAdapter.updateRes(users);
    }

    private void initView() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userList = (ListView) findViewById(R.id.userList);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "点击此处可以添加用户，是否要添加？", Snackbar.LENGTH_LONG)
                        .setAction("添加", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isManager) {
                                    Intent intent = new Intent(ManageUsers.this, UserDetails.class);
                                    intent.putExtra("account", "");
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ManageUsers.this, "您不是管理员，不可以添加用户！", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).show();
                break;
        }
    }
}
