/* $Id$ */
package com.muthuraj.soundprofilechanger;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by muthu-3955 on 12/02/17.
 */
public class SoundProfileTileService extends TileService {
    private static final String TAG = "SoundProfileTileService";//no i18n

    private AudioManager audioManager;
    private NotificationManager notificationManager;

    private String soundString;
    private String silentString;
    private String vibrateString;
    private String noMusicString;

    @Override
    public void onStopListening() {
        super.onStopListening();

        Log.d(TAG, "onStopListening() called");
        audioManager = null;
        notificationManager = null;
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        Log.d(TAG, "onStartListening() called");

        initTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        if (notificationManager == null) {
            initTile();
        }
        Log.d(TAG, "onClick() called");
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            setTileAction();
        } else {
            showDialog();
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.allow_permission_dialog, null);
        view.findViewById(R.id.allow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityAndCollapse(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setTitle("Sound Profile Changer");

        showDialog(dialog);
    }

    private void showDialogToEnableModes() {
//        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
//                .setMessage(R.string.no_enabled_tiles)
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(new Intent(getBaseContext(), MainActivity.class));
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .create()
//                .show();
        final Dialog dialog = new Dialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.allow_permission_dialog, null);
        TextView content = view.findViewById(R.id.content);
        content.setText(R.string.no_enabled_tiles);
        TextView allow = view.findViewById(R.id.allow);
        allow.setText(R.string.ok);
        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setTitle("Sound Profile Changer");

        showDialog(dialog);
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.d(TAG, "onTileRemoved() called");
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.d(TAG, "onTileAdded() called");
    }

    private void initTile() {
        initStrings();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager.isNotificationPolicyAccessGranted()) {
            getQsTile().setState(Tile.STATE_ACTIVE);

            Context context = getBaseContext();
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                        if (PreferenceUtil.isModeEnabled(context, PreferenceUtil.MODE_NO_MEDIA)) {
                            setMusicOffMode();
                        } else {
                            setTileInactive();
                        }
                    } else {
                        if (PreferenceUtil.isModeEnabled(context, PreferenceUtil.MODE_NORMAL)) {
                            setTileSound();
                        } else {
                            setTileInactive();
                        }
                    }
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    if (PreferenceUtil.isModeEnabled(context, PreferenceUtil.MODE_VIBRATE)) {
                        setTileVibrate();
                    } else {
                        setTileInactive();
                    }
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    if (PreferenceUtil.isModeEnabled(context, PreferenceUtil.MODE_SILENT)) {
                        setTileSilent();
                    } else {
                        setTileInactive();
                    }
                    break;
            }
        } else {
            setTileInactive();
        }
    }

    private void initStrings() {
        if (soundString == null) {
            soundString = getString(R.string.sound);
        }
        if (silentString == null) {
            silentString = getString(R.string.silent);
        }
        if (vibrateString == null) {
            vibrateString = getString(R.string.vibrate);
        }
        if (noMusicString == null) {
            noMusicString = getString(R.string.music_off);
        }
    }

    private void setTileInactive() {
        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    private void setTileAction() {
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            getQsTile().setState(Tile.STATE_INACTIVE);
            getQsTile().updateTile();
            startActivityAndCollapse(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
            return;
        }

        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
        setNextMode();
    }

    private void setNextMode() {
        String nexModeLabel = PreferenceUtil.getNexTEnabledMode(getBaseContext(), stringToPrefLabel((String) getQsTile().getLabel()));
        if (nexModeLabel == null) {
            showDialogToEnableModes();
            return;
        }
        switch (nexModeLabel) {
            case PreferenceUtil.MODE_NORMAL:
                setSoundMode();
                break;
            case PreferenceUtil.MODE_SILENT:
                setSilentMode();
                break;
            case PreferenceUtil.MODE_VIBRATE:
                setVibrateMode();
                break;
            case PreferenceUtil.MODE_NO_MEDIA:
                setMusicOffMode();
                break;
        }
    }

    private String stringToPrefLabel(String string) {
        if (string.equals(soundString)) {
            return PreferenceUtil.MODE_NORMAL;
        } else if (string.equals(silentString)) {
            return PreferenceUtil.MODE_SILENT;
        } else if (string.equals(vibrateString)) {
            return PreferenceUtil.MODE_VIBRATE;
        } else if (string.equals(noMusicString)) {
            return PreferenceUtil.MODE_NO_MEDIA;
        } else {
            throw new IllegalArgumentException(string + " is not a valid label");
        }
    }

    public void setSoundMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        setTileSound();
    }

    public void setVibrateMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_VIBRATE);
        setTileVibrate();
    }

    public void setSilentMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        setTileSilent();
    }

    public void setMusicOffMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        setTileMusicOff();
    }

    private final SparseArray<Icon> iconSparseArray = new SparseArray<>();

    private Icon getIconForResId(@DrawableRes int drawableRes) {
        Icon cachedIcon = iconSparseArray.get(drawableRes);
        if (cachedIcon != null) {
            return cachedIcon;
        }
        Icon createdIcon = Icon.createWithResource(this, drawableRes);
        iconSparseArray.put(drawableRes, createdIcon);
        return createdIcon;
    }

    private void setTileSilent() {
        getQsTile().setIcon(getIconForResId(R.drawable.ic_volume_off_white_24dp));
        getQsTile().setLabel(silentString);
        getQsTile().updateTile();
    }

    private void setTileVibrate() {
        getQsTile().setIcon(getIconForResId(R.drawable.ic_vibration_white_24dp));
        getQsTile().setLabel(vibrateString);
        getQsTile().updateTile();
    }

    private void setTileSound() {
        getQsTile().setIcon(getIconForResId(R.drawable.ic_volume_up_white_24dp));
        getQsTile().setLabel(soundString);
        getQsTile().updateTile();
    }

    private void setTileMusicOff() {
        getQsTile().setIcon(getIconForResId(R.drawable.ic_music_off));
        getQsTile().setLabel(noMusicString);
        getQsTile().updateTile();
    }
}
