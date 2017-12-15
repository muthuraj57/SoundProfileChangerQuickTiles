package com.muthuraj.soundprofilechanger;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends AppCompatActivity {

    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_REQUEST_CODE = 101;

    private Button requestPermissionButton;
    private TextView helperText;

//    private View soundProfileContainer;
//    private CheckBox normalCheckBox;
//    private CheckBox vibrateCheckBox;
//    private CheckBox silentCheckBox;
//    private CheckBox musicOffCheckBox;

    private RecyclerView modeReorderView;
    private LinearLayout permissionRationaleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceUtil.initAudioModeIndex(this);

        setToolbar();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        helperText = findViewById(R.id.helper_text);
        requestPermissionButton = findViewById(R.id.button);
//        soundProfileContainer = findViewById(R.id.sound_profile_container);
//
//        normalCheckBox = findViewById(R.id.normal_checkbox);
//        vibrateCheckBox = findViewById(R.id.vibrate_checkbox);
//        silentCheckBox = findViewById(R.id.silent_checkbox);
//        musicOffCheckBox = findViewById(R.id.music_off_checkbox);

        modeReorderView = findViewById(R.id.mode_reorder_view);
        permissionRationaleView = findViewById(R.id.permission_rationale_view);

        requestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS), NOTIFICATION_REQUEST_CODE);
            }
        });
        setViewsForAccessGranted(notificationManager.isNotificationPolicyAccessGranted());
        initRecyclerView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    private void initRecyclerView(){
        modeReorderView.setLayoutManager(new LinearLayoutManager(this));
        modeReorderView.setAdapter(new ModeSelectionAdapter(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ModeSelectionMoveCallback());
        itemTouchHelper.attachToRecyclerView(modeReorderView);
//        modeReorderView.addItemDecoration(new ItemTouchHelper(new ModeSelectionMoveCallback()));
    }

    public void setSoundMode(View view) {
        checkNormalMode();

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
    }

    public void setVibrateMode(View view) {
        checkVibrateMode();

        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_VIBRATE);
    }

    public void setSilentMode(View view) {
        checkSilentMode();

        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public void setMusicOff(View view) {
        checkMusicOffMode();

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    private void setViewsForAccessGranted(boolean isGranted) {
        if (isGranted) {
            permissionRationaleView.setVisibility(View.GONE);
            modeReorderView.setVisibility(View.VISIBLE);
            helperText.setText(getString(R.string.permission_granted));
            requestPermissionButton.setVisibility(View.GONE);
//            soundProfileContainer.setVisibility(View.VISIBLE);
            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                        checkMusicOffMode();
                    } else {
                        checkNormalMode();
                    }
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    checkVibrateMode();
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    checkSilentMode();
                    break;
            }
        } else {
            permissionRationaleView.setVisibility(View.VISIBLE);
            modeReorderView.setVisibility(View.GONE);
            helperText.setText(getString(R.string.permission_rationale));
            requestPermissionButton.setVisibility(View.VISIBLE);
//            soundProfileContainer.setVisibility(View.GONE);
        }
    }

    private void checkNormalMode() {
//        normalCheckBox.setChecked(true);
//        vibrateCheckBox.setChecked(false);
//        silentCheckBox.setChecked(false);
//        musicOffCheckBox.setChecked(false);
    }

    private void checkVibrateMode() {
//        normalCheckBox.setChecked(false);
//        vibrateCheckBox.setChecked(true);
//        silentCheckBox.setChecked(false);
//        musicOffCheckBox.setChecked(false);
    }

    private void checkSilentMode() {
//        normalCheckBox.setChecked(false);
//        vibrateCheckBox.setChecked(false);
//        silentCheckBox.setChecked(true);
//        musicOffCheckBox.setChecked(false);
    }

    private void checkMusicOffMode() {
//        normalCheckBox.setChecked(false);
//        vibrateCheckBox.setChecked(false);
//        silentCheckBox.setChecked(false);
//        musicOffCheckBox.setChecked(true);
    }
}
