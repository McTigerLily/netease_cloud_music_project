package com.example.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.example.lib_network.okhttp.exception.OkHttpException;
import com.example.lib_network.okhttp.listener.DisposeDataHandle;
import com.example.lib_network.okhttp.listener.DisposeDataListener;
import com.example.lib_network.okhttp.utils.ResponseEntityToModule;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 处理json类型的响应
 */
public class CommonJsonCallback implements Callback {
    protected final String EMPTY_MSG = "";

    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int OTHER_ERROR = -3; // the unknow error

    private Class<?> mClass;
    private DisposeDataListener mListener;
    private Handler mDeliveryHandler;

    public CommonJsonCallback(DisposeDataHandle handle){
        mListener=handle.mListener;
        mClass=handle.mClass;
        mDeliveryHandler=new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call,final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR,e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result=response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(String result) {
        if(result==null||result.trim().equals("")){
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
            return;
        }
        try{
            if(mClass==null){
                mListener.onSuccess(result);
            }else{
                //需要解析对象
                Object obj= ResponseEntityToModule.parseJsonToModule(result,mClass);
                if(obj!=null){
                    mListener.onSuccess(obj);
                }else{
                    mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                }
            }
        }catch (Exception e){
            mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
        }
    }
}
