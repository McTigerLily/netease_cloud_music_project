package com.example.lib_network.okhttp.listener;

/**
 * 业务逻辑层真正处理的地方，包括java层异常和业务层异常
 */
public interface DisposeDataListener {
    /**
     * 请求成功回调事件处理
     * @param object
     */
    void onSuccess(Object object);

    /**
     * 请求失败h回调事件处理
     * @param object
     */
    void onFailure(Object object);
}
