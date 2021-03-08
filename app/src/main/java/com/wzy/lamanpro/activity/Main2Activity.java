package com.wzy.lamanpro.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.bean.ReportData;
import com.wzy.lamanpro.common.LaManApplication;
import com.wzy.lamanpro.dao.ProductDataDaoUtils;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.utils.FileUtils;
import com.wzy.lamanpro.utils.PermissionGetting;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.SystemUtils;
import com.wzy.lamanpro.utils.UsbHelper;
import com.wzy.lamanpro.utils.UsbUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.wzy.lamanpro.common.LaManApplication.isManager;
import static com.wzy.lamanpro.utils.UsbUtils.showTmsg;

public class Main2Activity extends BaseDataCollectActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = Main2Activity.class.getSimpleName();

    private Toolbar toolbar;
    private DrawerLayout drawer;

    private NavigationView nav_view;
    private DrawerLayout drawer_layout;

    /**
     * 新版本算法函数接口案例
     */
    private void test_algorithm_interface() {
        //3.调试数据库数据，增加了本地网络方式，同一局域网内浏览器访问http://[拉曼设备ip地址]:8080/，可以对数据进行增删改查

        //6.查询classifierProduct表中的数据用法如下：
        List<ProductData> classifierProducts = new ProductDataDaoUtils(this).queryAllData();//查询所有样本库数据
        Log.d(TAG, String.format("共%d项数据。", classifierProducts.size()));
        for (ProductData product : classifierProducts) {
            Log.d(TAG, String.format("Id: %d, Name: %s, threshold: %.2f, priority: %d", product.getId(), product.getProName(),
                    product.getProductThreshold(), product.getProductPriority()));
        }
//        List<ClassifierProduct> classifierProductx = new ClassifierDaoUtils(this).queryOne("name");//通过物品名称查询该物品所有样本库数据
    }

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
                LaManApplication.canUseUsb = UsbUtils.initUsbData(Main2Activity.this, true);

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(Main2Activity.this, "设备已移除！", Toast.LENGTH_SHORT).show();
                LaManApplication.canUseUsb = UsbUtils.initUsbData(Main2Activity.this, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setMode(modeMainActivity);
        initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_VOLUME_UP){
//            Toast.makeText(Main2Activity.this,"关机",Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
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
                startActivity(new Intent(Main2Activity.this, SettingTest.class));
                return true;
            case R.id.use_info:
                new AlertDialog.Builder(Main2Activity.this)
                        .setMessage("内容正在完善中。。。")
                        .setTitle("使用说明")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            case R.id.data_copy:
                try {
                    UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this /* Context or Activity */);
                    if (devices.length == 0) {
                        showTmsg("备份失败，请插入u盘！！");
                        return false;
                    }
                    for (UsbMassStorageDevice device : devices) {
                        // before interacting with a device you need to call init()!
                        device.init();
                        // Only uses the first partition on the device
                        FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
//                        Log.d("u盘连接成功", "Capacity: " + currentFs.getCapacity());

                        UsbFile root = currentFs.getRootDirectory();
                        UsbFile[] files = root.listFiles();
                        UsbFile destination = null;
                        for (UsbFile file : files) {
                            if (file.getName().equalsIgnoreCase("拉曼数据导出") && file.isDirectory()) {
                                destination = file;
                            }
                        }
                        if (destination == null || !destination.getName().equalsIgnoreCase("拉曼数据导出")) {
                            destination = root.createDirectory("拉曼数据导出");
                        }
                        String path = Environment.getExternalStorageDirectory() + File.separator + "拉曼测试报告";
                        File[] dirFiles = new File(path).listFiles();
                        int count = 0;
                        for (File dirFile : dirFiles) {
//                            FileChannel inputChannel = null;
                            if (dirFile.getName().startsWith("拉曼测试报告-") && dirFile.getName().endsWith(".pdf")) {
//                              showTmsg(sdDirect);
                                FileUtils.saveSDFileToUsb(dirFile, destination, new UsbHelper.DownloadProgressListener() {
                                    @Override
                                    public void downloadProgress(int progress) {
//                                        Toast.makeText(Main2Activity.this, "复制文件中，已复制" + progress + "%", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                count++;
                            }
                        }

                        if (count == 0) {
                            showTmsg("没有数据文件！");
                        } else {
                            showTmsg("导出数据完成，一共导出" + count + "个文件！");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                    showTmsg("备份失败，请插入u盘！！");
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    LaManApplication.canUseUsb = UsbUtils.initUsbData(this, true);
                    showTmsg("请给u盘授予读写权限！！");
                }
                break;
            case R.id.data_bak:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("您确定要导出数据库吗？");
                builder.setTitle("温 馨 提 示 :");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean b = FileUtils.importDatabase(Main2Activity.this, Environment.getExternalStorageDirectory() +
                                File.separator + "RamanTest" + File.separator + "Database" + File.separator + "laman.db");
                        if (b)
                            Toast.makeText(Main2Activity.this, "导出完成", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Main2Activity.this, "导出失败", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_add_library:
                //未标定之前不能进入建库界面
                if (!checkCalibrationTable())
                    startActivity(new Intent(Main2Activity.this, AddLibrary.class));
                else
                    Toast.makeText(Main2Activity.this, "未标定之前不能进入建库界面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_manage_library:
                startActivity(new Intent(Main2Activity.this, ManageData.class));
                break;
            case R.id.nav_manage_his:
                startActivity(new Intent(Main2Activity.this, ManageHis.class));
                break;
            case R.id.nav_manage_report:
                startActivity(new Intent(Main2Activity.this, ReportActivity.class));
                break;
            case R.id.nav_manage_user:
//                if (isManager)
                startActivity(new Intent(Main2Activity.this, ManageUsers.class));
//                else
//                    Toast.makeText(Main2Activity.this, "非管理员用户不可使用此功能！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                startActivity(new Intent(Main2Activity.this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                showStyleDialog();
                break;
            case R.id.nav_shutdown:
                SystemUtils.shutDowm();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void showStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您确定注销吗？");
        builder.setTitle("温 馨 提 示 :");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtility.putSPBoolean(Main2Activity.this, "isAutoLogin", false);
                finish();
                Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void initView() {
//        stateText = new StringBuffer();
        lineChart = findViewById(R.id.lineChart);
        button_start = findViewById(R.id.button_start);
        button_calibrate = findViewById(R.id.button_calibrate);
        text_report = findViewById(R.id.text_report);
        debug_message = findViewById(R.id.debug_message);
        text_location = findViewById(R.id.text_location);

        button_start.setOnClickListener(this);
        button_calibrate.setEnabled(false);
        button_calibrate.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(this);
        state = findViewById(R.id.state);
        state.setMovementMethod(ScrollingMovementMethod.getInstance());

        setSupportActionBar(toolbar);
        LaManApplication.canUseUsb = UsbUtils.initUsbData(this, true);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = headerView.findViewById(R.id.textView);
        textView.setText(new UserDaoUtils(this).queryUserName(SPUtility.getUserId(this)));

        fab = findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.setOnClickListener(this);
        handler.sendEmptyMessage(3);

        //获取定位服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(Main2Activity.this, "请检查网络或GPS是否打开",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(Main2Activity.this, "我们需要获取位置的权限，请授予！", Toast.LENGTH_SHORT).show();
            PermissionGetting.showToAppSettingDialog();
        } else {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                //获取当前位置，这里只用到了经纬度
                locationName = getLocationAddress(location);
                handler.sendEmptyMessage(4);
            } else

                //绑定定位事件，监听位置是否改变
                //第一个参数为控制器类型第二个参数为监听位置变化的时间间隔（单位：毫秒）
                //第三个参数为位置变化的间隔（单位：米）第四个参数为位置监听器
                locationManager.requestLocationUpdates(provider, 2000, 2,
                        locationListener);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setMessage(LaManApplication.canUseUsb ? "仪器自检正常！" : "仪器没有连接！");
        builder.setTitle("仪器自检:");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setOnClickListener(this);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setOnClickListener(this);
        drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.setOnClickListener(this);

        button_start_normal_text = getString(R.string.button_start_main_text_normal);
        button_start_pressed_text = getString(R.string.button_start_main_text_pressed);

        //检查是否已标定
        checkCalibrationTable();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //初始化分类数据表
                    LaManApplication.matClassifier.initializeClassifierTable();
                    test_algorithm_interface();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location arg0) {
            // TODO Auto-generated method stub
            // 更新当前经纬度
            if (TextUtils.isEmpty(locationName)) {
                locationName = getLocationAddress(arg0);
                handler.sendEmptyMessage(4);
            }
//            locationName = getLocationAddress(arg0);
//            stateText.append("位置是：" + locationName);
//            handler.sendEmptyMessage(2);
        }
    };

    /**
     * 将经纬度转换成中文地址
     *
     * @param location
     * @return
     */
    private String getLocationAddress(Location location) {
        String add = "";
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.CHINESE);
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    1);
            Address address = addresses.get(0);
            Log.i(TAG, "getLocationAddress: " + address.toString());
            // Address[addressLines=[0:"中国",1:"北京市海淀区",2:"华奥饭店公司写字间中关村创业大街"]latitude=39.980973,hasLongitude=true,longitude=116.301712]
//            int maxLine = address.getMaxAddressLineIndex();
//            if (maxLine >= 2) {
//                add = address.getAddressLine(1) + address.getAddressLine(2);
//            } else {
//                add = address.getAddressLine(1);
//            }
            add = address.getAddressLine(0);
        } catch (IOException e) {
            add = "";
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            add = "获取位置失败！";
        }
        return add;
    }

    //关闭时解除监听器
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
