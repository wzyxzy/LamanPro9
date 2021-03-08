package com.wzy.lamanpro.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.os.Message;
import android.text.BoringLayout;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;
import com.wzy.lamanpro.common.LaManApplication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

public class UsbUtils {

    private static final String ACTION_DEVICE_PERMISSION = "ACTION_DEVICE_PERMISSION";
    //设备列表
    private static HashMap<String, UsbDevice> deviceList;
    //USB管理器:负责管理USB设备的类
    private static UsbManager manager;
    //找到的USB设备
    private static UsbDevice mUsbDevice;
    //代表USB设备的一个接口
    private static UsbInterface mInterface;
    private static UsbDeviceConnection mDeviceConnection;
    //代表一个接口的某个节点的类:写数据节点
    private static UsbEndpoint usbEpOut;
    //代表一个接口的某个节点的类:读数据节点
    private static UsbEndpoint usbEpIn;
    //要发送信息字节
    private static byte[] sendbytes;
    //接收到的信息字节
    private static byte[] receiveytes;
    private static Context context;

    public static boolean initUsbData(Context context, boolean attach) {
        UsbUtils.context = context;

        // 获取USB设备
        manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取到设备列表
        deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
//            Log.e("ldm", "vid=" + deviceIterator.next().getVendorId() + "---pid=" + deviceIterator.next().getProductId());
            //if (mVendorID == usb.getVendorId() && mProductID == usb.getProductId()) {//找到指定设备
            mUsbDevice = deviceIterator.next();
        }
        if (mUsbDevice == null || mUsbDevice.getInterfaceCount() == 0) {
            showTmsg("请连接光谱仪设备");
            return false;
        }
        //获取设备接口
        for (int i = 0; i < mUsbDevice.getInterfaceCount(); ) {
            // 一般来说一个设备都是一个接口，你可以通过getInterfaceCount()查看接口的个数
            // 这个接口上有两个端点，分别对应OUT 和 IN
            UsbInterface usbInterface = mUsbDevice.getInterface(i);
            mInterface = usbInterface;
            break;
        }
//        if (mInterface.getEndpointCount() < 3 && !attach) {
//            showTmsg("u盘已拔出！");
//            return false;
//        }
        if (mInterface.getEndpointCount() < 3) {
            showTmsg(attach ? "u盘连接成功！" : "u盘已拔出！");
            if (attach){
                PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_DEVICE_PERMISSION), 0);
                manager.requestPermission(mUsbDevice, permissionIntent);
            }

            return false;
        }

        if (mInterface != null && attach) {
            // 判断是否有权限
            UsbPermissionReceiver usbPermissionReceiver = new UsbPermissionReceiver();
            if (manager.hasPermission(mUsbDevice)) {
                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                mDeviceConnection = manager.openDevice(mUsbDevice);
                if (mDeviceConnection == null) {
                    return false;
                }
                if (mDeviceConnection.claimInterface(mInterface, true)) {

                    //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
                    if (mInterface.getEndpoint(3) != null) {
                        usbEpOut = mInterface.getEndpoint(3);
                    }
                    if (mInterface.getEndpoint(2) != null) {
                        usbEpIn = mInterface.getEndpoint(2);
                    }

                    showTmsg("光谱仪连接成功！");

                    return true;

                } else {
                    mDeviceConnection.close();
                    return false;
                }
            } else {

                Intent intent = new Intent(ACTION_DEVICE_PERMISSION);
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                IntentFilter permissionFilter = new IntentFilter(ACTION_DEVICE_PERMISSION);
                context.registerReceiver(usbPermissionReceiver, permissionFilter);
                manager.requestPermission(mUsbDevice, mPermissionIntent);

                showTmsg("没有权限，请授权！");
                return false;
            }
        } else {
            showTmsg("没有找到设备接口！");
            return false;
        }


    }

    private static class UsbPermissionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device.getDeviceName().equals(mUsbDevice.getDeviceName())) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            //授权成功,在这里进行打开设备操作
                            LaManApplication.canUseUsb = true;
                            //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
                            initUsbData(context, true);

                        } else {
                            showTmsg("USB授权失败！");
                            //授权失败
                            LaManApplication.canUseUsb = false;
                        }
                    }
                }
            }
        }
    }

    public static byte[] sendToUsb(byte[] content) {
        return sendToUsb(content, false);
    }

    public static byte[] sendToUsb(byte[] content, boolean isLast) {
        sendbytes = content;
        mDeviceConnection.bulkTransfer(usbEpOut, sendbytes, sendbytes.length, 5000);
        if (isLast) {
            // 接收发送成功信息(相当于读取设备数据)
            receiveytes = new byte[LaManApplication.dataLength * 2];   //根据设备实际情况写数据大小
            mDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 10000);
            return receiveytes;
        }
        return null;
    }

    public static byte[] readFromUsb() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(LaManApplication.dataLength * 2);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(mDeviceConnection, usbEpIn);
        usbRequest.queue(byteBuffer, LaManApplication.dataLength * 2);
        if (mDeviceConnection.requestWait() == usbRequest) {
            return byteBuffer.array();
        }
        return null;

    }


    public static void showTmsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexToByteArray(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }


    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    public static String repeat(int count) {
        return repeat(count, " ");
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的结果
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String bytesToHexFun3(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
    public static byte[] intTobyteLH(int n) {
        byte[] b = new byte[4];// 为什么是4？因为java中int占4个字节，一个字节占8位，总共32位
        b[0] = (byte) (n & 0xff);      // 从右到左，取第1到第8位
        b[1] = (byte) (n >> 8 & 0xff); // 从右到左,取第9到16位
        b[2] = (byte) (n >> 16 & 0xff);// 从右到左,取第17到24位
        b[3] = (byte) (n >> 24 & 0xff);// 从右到左,取第25到32位
        return b;
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
    public static byte[] intTobyteHH(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     *  
     *  *  
     *  * @param data1 
     *  * @param data2 
     *  * @return data1 与 data2拼接的结果 
     *  
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * 将两个byte数据转化为有符号int
     *
     * @param high : 高八位
     * @param low  : 低八位
     * @return
     */
    public static int twoByteToSignedInt(byte high, byte low) {
        return (high << 8) | low;
    }

    /**
     * 将两个byte数据转化为无符号int
     *
     * @param high : 高八位
     * @param low  : 低八位
     * @return
     */
    public static int twoByteToUnsignedInt(byte high, byte low) {
        return ((high << 8) & 0xffff) | (low & 0x00ff);
    }

//    private void readDeviceList() {
//        Log.i(TAG, "開始讀取設備列表...");
//        //獲取存儲設備
//        storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        for (UsbMassStorageDevice device : storageDevices) {
//            //可能有幾個 一般只有一個 因爲大部分手機只有1個otg插口
//            if (usbManager.hasPermission(device.getUsbDevice())) {
//                //有就直接讀取設備是否有權限
//                Log.i(TAG, "檢測到有權限，直接讀取");
//                AppSession.getInstance().isHostMode = true;
//                readDevice(device);
//            } else {//沒有就去發起意圖申請
//                Log.i(TAG, "檢測到設備，但是沒有權限，進行申請");
//                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
//                //該代碼執行後，系統彈出一個對話框，
//            }
//        }
//        if (storageDevices.length == 0) {
//            AppSession.getInstance().isHostMode = false;
//            Log.i(TAG, "未檢測到有任何存儲設備插入");
//        }
//    }
//
//    private void readDevice(UsbMassStorageDevice device) {
//        // before interacting with a device you need to call init()!
//        try {
//            device.init();//初始化
//            //Only uses the first partition on the device
//            Partition partition = device.getPartitions().get(0);
//            FileSystem currentFs = partition.getFileSystem();
//            //fileSystem.getVolumeLabel()可以獲取到設備的標識
//            //通過FileSystem可以獲取當前U盤的一些存儲信息，包括剩餘空間大小，容量等等
//            UsbFile root = currentFs.getRootDirectory();//獲取根目錄
//            String deviceName = currentFs.getVolumeLabel();//獲取設備標籤
//            Log.i(TAG,"正在讀取U盤" + deviceName);
//            cFolder = root;//設置當前文件對象
//            createDir(device);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i(TAG,"讀取失敗，異常：" + e.getMessage());
//        }
//    }
//
//    private void createDir(UsbMassStorageDevice device) throws IOException {
//        String localPath = ConfigManager.getInstance().getConfig().getLocalpath();
//        if(localPath.startsWith("/")){
//            localPath = localPath.substring(1);
//        }
//
//        if(localPath.endsWith("/")){
//            localPath = localPath.substring(0, localPath.length()-1);
//        }
//        String[] dirs =  localPath.split("/");
//        if(dirs == null){
//            return;
//        }
//
//
//        UsbFile tmpFile;
//        for (int i = 0; i < dirs.length; i++ ){
//            tmpFile = cFolder.search(dirs[i]);
//            if(tmpFile == null){
//                cFolder = cFolder.createDirectory(dirs[i]);
//            }else {
//                cFolder = tmpFile;
//            }
//        }
//
//        device.close();
//    }
}
