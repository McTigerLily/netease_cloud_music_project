package com.example.netease_cloud_music_module.model.friend;

import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.netease_cloud_music_module.model.BaseModel;

import java.util.ArrayList;

public class FriendBodyValue extends BaseModel {
    public int type;
    public String avatr;
    public String name;
    public String fans;
    public String text;
    public ArrayList<String> pics;
    public String videoUrl;
    public String zan;
    public String msg;
    public AudioBean audioBean;
}
