package com.example.ydd.dcb.login;

public interface LoginView {


    void setMsg(String msg,int type);

    void postMsg(String msg,int type);

    void success(String id);
}
