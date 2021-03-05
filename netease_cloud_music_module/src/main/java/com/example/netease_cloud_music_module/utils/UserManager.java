package com.example.netease_cloud_music_module.utils;


import com.example.netease_cloud_music_module.model.user.User;

/**
 * @description 单例管理登陆用户信息
 */
public class UserManager {

  private static UserManager userManager = null;
  private User mUser = null;

  public static UserManager getInstance() {
    if (userManager == null) {
      synchronized (UserManager.class) {
        if (userManager == null) {
          userManager = new UserManager();
        }
      }
    }
    return userManager;
  }
  public boolean hasLogined() {

    return mUser == null ? false : true;
  }

  /**
   * save user
   */
  public void saveUser(User user){
    mUser=user;
    saveLocal(user);
  }
  private void saveLocal(User user){

  }

  /**
   * get user
   */
  public User getUser() {
    return mUser;
  }

  public User getLocal(){
    return null;
  }

  /**
   * remove the user info
   */
  public void removeUser() {
    mUser = null;
    removeLocal();
  }
  private void removeLocal(){

  }
}
