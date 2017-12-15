package com.muthuraj.soundprofilechanger;

import android.content.Context;

/**
 * Created by Muthuraj on 18/11/17.
 * <p>
 * Jambav, Zoho Corporation
 */

final class PreferenceUtil {
    private static final String FILE_NAME = "audio_prefs";

    static final String MODE_NORMAL = "mode_normal";
    static final String MODE_SILENT = "mode_silent";
    static final String MODE_VIBRATE = "mode_vibrate";
    static final String MODE_NO_MEDIA = "mode_no_media";

    private static final String INDEX = "index";
    private static final String MODE_ENABLED = "mode_enabled";

    private static final String IS_FIRST_TIME = "is_first_time";

    static void initAudioModeIndex(Context context) {

        boolean isFirstTime = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(IS_FIRST_TIME, true);

        if (isFirstTime) {
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(IS_FIRST_TIME, false)
                    .putInt(MODE_NORMAL + INDEX, 0)
                    .putInt(MODE_VIBRATE + INDEX, 1)
                    .putInt(MODE_SILENT + INDEX, 2)
                    .putInt(MODE_NO_MEDIA + INDEX, 3)
                    .apply();
        }
    }

    static String getModeLabelForIndex(Context context, int index) {
        if (getIndex(context, MODE_NORMAL) == index) {
            return MODE_NORMAL;
        }
        if (getIndex(context, MODE_VIBRATE) == index) {
            return MODE_VIBRATE;
        }
        if (getIndex(context, MODE_SILENT) == index) {
            return MODE_SILENT;
        }
        if (getIndex(context, MODE_NO_MEDIA) == index) {
            return MODE_NO_MEDIA;
        }
        throw new IllegalStateException("No mode contains " + index + " index");
    }

    private static int getIndex(Context context, String keyName) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                .getInt(keyName + INDEX, 0);
    }

    public static void putIndex(Context context, String keyName, int index){
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(keyName + INDEX, index)
                .apply();
    }

    static boolean isModeEnabled(Context context, String modeLabel) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(modeLabel + MODE_ENABLED, false);
    }

    static void setModeEnabled(Context context, String modeLabel, boolean enable) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(modeLabel + MODE_ENABLED, enable)
                .apply();
    }
}
