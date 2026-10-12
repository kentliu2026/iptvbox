package com.example.iptvbox;

/** 单个频道：名称 + 组播地址 */
public class Channel {
    public final String name;
    public final String url;

    public Channel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return name;
    }
}
