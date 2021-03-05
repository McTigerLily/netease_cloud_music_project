package com.example.lib_audio.mediaplayer.db;


import android.database.sqlite.SQLiteDatabase;

import com.example.lib_audio.app.AudioHelper;
import com.example.lib_audio.mediaplayer.model.AudioBean;
import com.example.lib_audio.mediaplayer.model.Favourite;

/**
 * 为什么用greenDao数据库呢？不用SQLite呢
 * greenDao很小，
 * api很贴近原生SQLite
 * 有强大的面向对象关系
 */
public class GreenDaoHelper {
    private static final String DB_NAME="music_db";
    //创建所有的表、升级数据库的工具,数据库帮助类
    private static DaoMaster.DevOpenHelper mHelper;
    //数据库，复用了SQLite
    private static SQLiteDatabase mDb;
    //管理数据库的增删改查
    private static DaoMaster mDaoMaster;
    //管理（实体dao）表的增删改查
    private static DaoSession mDaoSession;

    //初始化
    public static void initDatabase(){
        mHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(), DB_NAME,null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 添加一个收藏
     * @param bean
     */
    public static void addFavorite(AudioBean bean){
        FavouriteDao dao=mDaoSession.getFavouriteDao();
        Favourite favourite=new Favourite();
        favourite.setAudioId(bean.id);
        favourite.setAudioBean(bean);
        dao.insertOrReplace(favourite);
    }

    /**
     **移除一个收藏
     * @param bean
     */
    public static void removeFavorite(AudioBean bean){
        FavouriteDao dao=mDaoSession.getFavouriteDao();
        Favourite favourite=selectFavorite(bean);
        dao.delete(favourite);
    }

    /**
     **查找一个收藏
     * @param bean
     */
    public static Favourite selectFavorite(AudioBean bean){
        FavouriteDao dao=mDaoSession.getFavouriteDao();
        Favourite favourite=dao.queryBuilder().where(FavouriteDao.Properties.AudioId.eq(bean.id)).unique();
        return favourite;
    }




}

