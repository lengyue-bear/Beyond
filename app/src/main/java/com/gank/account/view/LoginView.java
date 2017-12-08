package com.gank.account.view;


import com.gank.account.model.UserInfo;
import com.trident.beyond.core.MvvmBaseView;

public interface LoginView extends MvvmBaseView<UserInfo> {

    void loginSuccess(UserInfo userInfo);

    void loginFail(Exception error);

}