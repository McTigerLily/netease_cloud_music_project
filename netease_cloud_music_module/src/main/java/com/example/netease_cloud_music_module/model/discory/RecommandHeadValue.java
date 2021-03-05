package com.example.netease_cloud_music_module.model.discory;

import com.example.netease_cloud_music_module.model.BaseModel;
import java.util.ArrayList;

/**
 * @author: vision
 * @function:
 * @date: 19/6/2
 */
public class RecommandHeadValue extends BaseModel {

    public ArrayList<String> ads;
    public ArrayList<RecommandMiddleValue> middle;
    public ArrayList<RecommandFooterValue> footer;

}
