package com.example.netease_cloud_music_module.view.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.EventLog;
import android.view.View;

import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_network.okhttp.listener.DisposeDataListener;
import com.example.netease_cloud_music_module.R;
import com.example.netease_cloud_music_module.api.RequestCenter;
import com.example.netease_cloud_music_module.model.login.LoginEvent;
import com.example.netease_cloud_music_module.model.user.User;
import com.example.netease_cloud_music_module.utils.UserManager;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends BaseActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
    public void findView(){
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object object) {
                        User user=(User)object;
                        UserManager.getInstance().saveUser(user);
                        EventBus.getDefault().post(new LoginEvent());
                        finish();
                    }

                    @Override
                    public void onFailure(Object object) {

                    }
                });
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
    }
}