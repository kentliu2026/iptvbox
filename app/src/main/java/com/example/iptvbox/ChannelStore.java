package com.example.iptvbox;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 频道数据源
 *  - 优先从 res/raw/channels.txt 读取（方便编辑，无需改代码）
 *  - 格式：频道名|组播地址，# 开头为注释
 *  - 若文件为空则给占位示例，提示用户替换
 */
public class ChannelStore {

    public static List<Channel> load(Context context) {
        List<Channel> list = new ArrayList<>();
        try (InputStream is = context.getResources().openRawResource(R.raw.channels)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    list.add(new Channel(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            // 读取失败则走下方默认占位
        }
        if (list.isEmpty()) {
            list.add(new Channel("示例-央视(占位)", "udp://@239.1.1.1:1234"));
            list.add(new Channel("示例-卫视(占位)", "udp://@239.1.1.2:1234"));
        }
        return list;
    }
}
