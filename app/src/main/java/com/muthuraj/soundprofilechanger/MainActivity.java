package com.muthuraj.soundprofilechanger;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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

    private NotificationManager notificationManager;
    private static final int NOTIFICATION_REQUEST_CODE = 101;

    private Button requestPermissionButton;
    private TextView helperText;

    private RecyclerView modeReorderView;
    private LinearLayout permissionRationaleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceUtil.initAudioModeIndex(this);

        setToolbar();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        helperText = findViewById(R.id.helper_text);
        requestPermissionButton = findViewById(R.id.button);

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

    private void initRecyclerView() {
        modeReorderView.setLayoutManager(new LinearLayoutManager(this));
        modeReorderView.setAdapter(new ModeSelectionAdapter(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ModeSelectionMoveCallback());
        itemTouchHelper.attachToRecyclerView(modeReorderView);
    }

    private void setViewsForAccessGranted(boolean isGranted) {
        if (isGranted) {
            permissionRationaleView.setVisibility(View.GONE);
            modeReorderView.setVisibility(View.VISIBLE);
            helperText.setText(getString(R.string.permission_granted));
            requestPermissionButton.setVisibility(View.GONE);
        } else {
            permissionRationaleView.setVisibility(View.VISIBLE);
            modeReorderView.setVisibility(View.GONE);
            helperText.setText(getString(R.string.permission_rationale));
            requestPermissionButton.setVisibility(View.VISIBLE);
        }
    }
}
