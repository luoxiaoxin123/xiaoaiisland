package com.xiaoai.islandnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 接收来自超级小爱进程发回的课程数据状态快照广播。
 * 静态注册在 AndroidManifest 中，保证即使模块 APP 处于后台也能持久化最新状态。
 */
public class CourseStatusReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_COURSE_STATUS =
            "com.xiaoai.islandnotify.ACTION_UPDATE_COURSE_STATUS";
    public static final String KEY_COURSE_STATUS_SNAPSHOT = "snapshot_json";
    public static final String PREFS_RUNTIME_NAME = "island_runtime";
    public static final String KEY_SNAPSHOT_STORAGE = "course_status_snapshot";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (ACTION_UPDATE_COURSE_STATUS.equals(intent.getAction())) {
            String snapshot = intent.getStringExtra(KEY_COURSE_STATUS_SNAPSHOT);
            if (snapshot != null && !snapshot.isEmpty()) {
                SharedPreferences sp = context.getSharedPreferences(PREFS_RUNTIME_NAME, Context.MODE_PRIVATE);
                sp.edit().putString(KEY_SNAPSHOT_STORAGE, snapshot).apply();
                Log.d("IslandNotify", "CourseStatusReceiver: saved snapshot -> " + snapshot);
                ComposeRefreshBus.bump();
            }
        }
    }
}
