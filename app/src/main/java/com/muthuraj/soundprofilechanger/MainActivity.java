package com.muthuraj.soundprofilechanger;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_REQUEST_CODE = 101;

    private Button requestPermissionButton;
    private View soundProfileContainer;
    private TextView helperText;

    private CheckBox normalCheckBox;
    private CheckBox vibrateCheckBox;
    private CheckBox silentCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        helperText = (TextView) findViewById(R.id.helper_text);
        requestPermissionButton = (Button) findViewById(R.id.button);
        soundProfileContainer = findViewById(R.id.sound_profile_container);

        normalCheckBox = (CheckBox) findViewById(R.id.normal_checkbox);
        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrate_checkbox);
        silentCheckBox = (CheckBox) findViewById(R.id.silent_checkbox);

        requestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), NOTIFICATION_REQUEST_CODE);
            }
        });
        setViewsForAccessGranted(notificationManager.isNotificationPolicyAccessGranted());
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sound Profile Changer");
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setViewsForAccessGranted(notificationManager.isNotificationPolicyAccessGranted());
    }

    @Override
    protected void onStart() {
        super.onStart();

        setViewsForAccessGranted(notificationManager.isNotificationPolicyAccessGranted());
    }

    public void setSoundMode(View view) {
        normalCheckBox.setChecked(true);
        vibrateCheckBox.setChecked(false);
        silentCheckBox.setChecked(false);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
    }

    public void setVibrateMode(View view) {
        normalCheckBox.setChecked(false);
        vibrateCheckBox.setChecked(true);
        silentCheckBox.setChecked(false);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_VIBRATE);
    }

    public void setSilentMode(View view) {
        normalCheckBox.setChecked(false);
        vibrateCheckBox.setChecked(false);
        silentCheckBox.setChecked(true);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    private void setViewsForAccessGranted(boolean isGranted) {
        if (isGranted) {
            helperText.setText(getString(R.string.permission_granted));
            requestPermissionButton.setVisibility(View.GONE);
            soundProfileContainer.setVisibility(View.VISIBLE);
        } else {
            helperText.setText(getString(R.string.permission_rationale));
            requestPermissionButton.setVisibility(View.VISIBLE);
            soundProfileContainer.setVisibility(View.GONE);
        }
    }
}
