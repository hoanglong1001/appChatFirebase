package com.example.techasians_appchat.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String avatar;
    private String state;
    private String diachi;
    private String ngaysinh;
    private String cmt;
    private int index;

    public User() {

    }

    public User(String id, String name, String avatar, String state) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.state = state;
    }

    public User(String id, String name, String avatar, String state, String diachi, String ngaysinh, String cmt) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.state = state;
        this.diachi = diachi;
        this.ngaysinh = ngaysinh;
        this.cmt = cmt;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
