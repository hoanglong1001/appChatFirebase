package com.example.techasians_appchat.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String idUser;
    private String key;
    private String name;
    private String avatar;
    private long timedate;
    private String status;
    private String picture;
    private String likes;

    public Post() {

    }

    public Post(String idUser, String name, String avatar, long timedate, String status, String picture) {
        this.idUser = idUser;
        this.name = name;
        this.avatar = avatar;
        this.timedate = timedate;
        this.status = status;
        this.picture = picture;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getTimedate() {
        return timedate;
    }

    public void setTimedate(long timedate) {
        this.timedate = timedate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<String> getNumberLike(String like) {
        List<String> numbers = new ArrayList<>();

        if(like != null && !like.isEmpty()) {
            String[] temp = like.split("_");
            for (int i = 0; i < temp.length; i++) {
                if (!temp[i].isEmpty()) {
                    numbers.add(temp[i]);
                }
            }
        }
        return numbers;
    }

    public String setLikesString(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();

        if(stringList != null && stringList.size() > 0) {
            for (int i = 0; i < stringList.size(); i++) {
               stringBuilder.append(stringList.get(i));
               stringBuilder.append("_");
            }
        }
        return stringBuilder.toString();
    }
}
