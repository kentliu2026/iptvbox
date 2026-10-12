package com.example.iptvbox;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * 机顶盒 IPTV 组播播放器 —— 主界面（频道列表）
 *
 * 设计要点：
 *  - 用 ListView 承载频道，原生支持遥控器方向键导航 + 确认键选中，
 *    无需额外处理 D-pad 焦点逻辑。
 *  - 本 APP 只负责"频道管理 + 遥控器交互"，真正的组播解码播放
 *    交给本机已安装的 VLC（org.videolan.vlc），由 VLC 引擎处理
 *    udp://@ / rtp://@ 组播流，兼容性与硬件解码最稳妥。
 */
public class MainActivity extends AppCompatActivity {

    private static final String VLC_PACKAGE = "org.videolan.vlc";
    private List<Channel> channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        channels = ChannelStore.load(this);

        ListView list = findViewById(R.id.channel_list);
        ChannelAdapter adapter = new ChannelAdapter(this, channels);
        list.setAdapter(adapter);

        // 遥控器确认键 / 鼠标点击都会触发 onItemClick
        list.setOnItemClickListener((parent, view, position, id) -> {
            Channel c = channels.get(position);
            play(c);
        });
    }

    /** 调起 VLC 播放指定组播地址 */
    private void play(Channel c) {
        if (!isVlcInstalled()) {
            Toast.makeText(this, R.string.vlc_missing, Toast.LENGTH_LONG).show();
            return;
        }
        Uri uri = Uri.parse(c.url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(VLC_PACKAGE);
        intent.setData(uri);                 // VLC 按 scheme(udp/rtp/rtsp/http) 识别
        intent.putExtra("from_start", true); // VLC 扩展参数：从头播放
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.vlc_missing, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isVlcInstalled() {
        try {
            getPackageManager().getPackageInfo(VLC_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
