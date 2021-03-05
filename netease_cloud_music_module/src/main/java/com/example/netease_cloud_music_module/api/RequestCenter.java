package com.example.netease_cloud_music_module.api;

import com.example.lib_network.okhttp.CommonOkHttpClient;
import com.example.lib_network.okhttp.listener.DisposeDataHandle;
import com.example.lib_network.okhttp.listener.DisposeDataListener;
import com.example.lib_network.okhttp.request.CommonRequest;
import com.example.lib_network.okhttp.request.RequestParams;
import com.example.lib_network.okhttp.response.CommonJsonCallback;
import com.example.netease_cloud_music_module.model.discory.BaseRecommandModel;
import com.example.netease_cloud_music_module.model.discory.BaseRecommandMoreModel;
import com.example.netease_cloud_music_module.model.friend.BaseFriendModel;
import com.example.netease_cloud_music_module.model.user.User;

public class RequestCenter {
    public static class HttpConstants {
        //private static final String ROOT_URL = "http://imooc.com/api";
        private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
    }
    //根据参数发送所有post请求
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener,
                                   Class<?> clazz){
        CommonOkHttpClient.post(CommonRequest.createPostRequest(url,params), new DisposeDataHandle(listener,clazz));
    }
    //根据参数发送所有get请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    public static void login(DisposeDataListener listener){
        RequestParams params=new RequestParams();
        params.put("mb","18734924592");
        params.put("pwd","999999q");
        RequestCenter.postRequest(HttpConstants.LOGIN,params,listener, User.class);
    }

    public static void requestFriendData(DisposeDataListener listener){
        postRequest(HttpConstants.HOME_FRIEND,null,listener, BaseFriendModel.class);
    }

    public static void requestRecommandData(DisposeDataListener listener) {
        RequestCenter.getRequest(HttpConstants.HOME_RECOMMAND, null, listener,
                BaseRecommandModel.class);
    }

    public static void requestRecommandMore(DisposeDataListener listener) {
        RequestCenter.getRequest(HttpConstants.HOME_RECOMMAND_MORE, null, listener,
                BaseRecommandMoreModel.class);
    }
}
