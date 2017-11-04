/* $Id$ */
package com.muthuraj.soundprofilechanger;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by muthu-3955 on 12/02/17.
 */
public class SoundProfileTileService extends TileService {
    private static final String TAG = "SoundProfileTileService";//no i18n

    private AudioManager audioManager;
    private NotificationManager notificationManager;

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
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager.isNotificationPolicyAccessGranted()) {
            getQsTile().setState(Tile.STATE_ACTIVE);
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:
                    setTileSound();
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    setTileVibrate();
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    setTileSilent();
                    break;
            }
        } else {
            getQsTile().setState(Tile.STATE_INACTIVE);
            getQsTile().updateTile();
        }
    }

    private void setTileAction() {
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            getQsTile().setState(Tile.STATE_INACTIVE);
            getQsTile().updateTile();
            startActivityAndCollapse(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
            return;
        }

        if (getQsTile().getLabel().equals(getString(R.string.sound))) {
            Log.d(TAG, "setTileAction: sound to vibrate");
            setVibrateMode();
            return;
        }

        if (getQsTile().getLabel().equals(getString(R.string.vibrate))) {
            Log.d(TAG, "setTileAction: vibrate to silent");
            setSilentMode();
            return;
        }

        if (getQsTile().getLabel().equals(getString(R.string.silent))) {
            Log.d(TAG, "setTileAction: silent to sound");
            setSoundMode();
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

    private void setTileSilent() {
        getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_volume_off_white_24dp));
        getQsTile().setLabel(getString(R.string.silent));
        getQsTile().updateTile();
    }

    private void setTileVibrate() {
        getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_vibration_white_24dp));
        getQsTile().setLabel(getString(R.string.vibrate));
        getQsTile().updateTile();
    }

    private void setTileSound() {
        getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_volume_up_white_24dp));
        getQsTile().setLabel(getString(R.string.sound));
        getQsTile().updateTile();
    }
}
