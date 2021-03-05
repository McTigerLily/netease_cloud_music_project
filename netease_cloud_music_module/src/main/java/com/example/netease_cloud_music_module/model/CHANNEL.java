package com.example.netease_cloud_music_module.model;

public enum  CHANNEL {
    MY("我的",0x01),
    DISCOVERY("发现",0x02),
    FRIEND("朋友",0x03);

    //所有类型标志
    public static final int MINE_ID=0x01;
    public static final int DISCOVERY_ID=0x02;
    public static final int FRIEND_ID=0x03;


    private final String key;
    private final int value;

    CHANNEL(String key,int value){
        this.key=key;
        this.value=value;
    }
    public int getValue(){
        return value;
    }
    public String getKey(){
        return key;
    }

}
