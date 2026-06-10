package com.xiaoai.islandnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 接收 hook 进程（超级小爱）推送的“课程数据状态”，落本地盘供 UI 展示。
 *
 * <p>镜像与 CourseData 均存于超级小爱进程私有目录，模块进程无法直接读取，
 * 故由 {@code MainHook#pushCourseDataStatus} 在超级小爱进程算出状态后包内广播过来，
 * 本 Receiver 写入模块本地 {@code island_runtime}，{@code MainActivity} 再本地读取。
 * 与 {@link TotalWeekReceiver} 同一模式；额外 bump 刷新总线，使页面实时更新。</p>
 */
public class CourseDataStatusReceiver extends BroadcastReceiver {
    public static final String ACTION_PUSH_COURSE_DATA_STATUS =
            "com.xiaoai.islandnotify.ACTION_PUSH_COURSE_DATA_STATUS";
    public static final String EXTRA_SOURCE = "course_data_source";
    public static final String EXTRA_IMPORTED = "course_data_imported";

    /** 写入 island_runtime 的键：最近一次状态对应的数据源 */
    public static final String KEY_STATUS_SOURCE = "course_data_status_source";
    /** 写入 island_runtime 的键：是否已导入（true=导入成功，false=等待导入） */
    public static final String KEY_STATUS_IMPORTED = "course_data_status_imported";

    private static final String PREFS_RUNTIME_NAME = "island_runtime";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_PUSH_COURSE_DATA_STATUS.equals(intent.getAction())) return;
        String source = intent.getStringExtra(EXTRA_SOURCE);
        if (source == null || source.isEmpty()) source = "xiaoai";
        boolean imported = intent.getBooleanExtra(EXTRA_IMPORTED, false);
        try {
            SharedPreferences sp = context.getSharedPreferences(PREFS_RUNTIME_NAME, Context.MODE_PRIVATE);
            sp.edit()
                    .putString(KEY_STATUS_SOURCE, source)
                    .putBoolean(KEY_STATUS_IMPORTED, imported)
                    .apply();
            Log.d("IslandNotify", "CourseDataStatusReceiver: source=" + source + " imported=" + imported);
            ComposeRefreshBus.bump();
        } catch (Throwable t) {
            Log.w("IslandNotify", "CourseDataStatusReceiver failed: " + t.getMessage());
        }
    }
}
