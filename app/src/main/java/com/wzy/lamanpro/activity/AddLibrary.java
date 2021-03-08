package com.wzy.lamanpro.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.utils.UsbUtils;

public class AddLibrary extends BaseDataCollectActivity {

    private static final String TAG = AddLibrary.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                LaManApplication.canUseUsb = UsbUtils.initUsbData(AddLibrary.this, true);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(AddLibrary.this, "设备已移除！", Toast.LENGTH_SHORT).show();
                LaManApplication.canUseUsb = UsbUtils.initUsbData(AddLibrary.this, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_library);
        setMode(modeLibraryActivity);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(AddLibrary.this, SettingTest.class));
                return true;
            case R.id.use_info:
                new AlertDialog.Builder(AddLibrary.this)
                        .setMessage("内容正在完善中。。。")
                        .setTitle("使用说明")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
//            case R.id.action_report:
//
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart = findViewById(R.id.lineChart);
        button_start = findViewById(R.id.button_start);
        state = findViewById(R.id.state);
        state.setMovementMethod(ScrollingMovementMethod.getInstance());
        LaManApplication.canUseUsb = UsbUtils.initUsbData(this, true);
        fab = findViewById(R.id.fab);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setOnClickListener(this);
        button_start.setOnClickListener(this);
        fab.setOnClickListener(this);
        handler.sendEmptyMessage(3);
        button_start_normal_text = getString(R.string.button_start_addlibrary_text_normal);
        button_start_pressed_text = getString(R.string.button_start_addlibrary_text_pressed);
    }
}
