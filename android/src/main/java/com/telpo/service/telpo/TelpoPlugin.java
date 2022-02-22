package com.telpo.service.telpo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.printer.UsbThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

/**
 * TelpoPlugin
 */
public class TelpoPlugin extends Activity implements FlutterPlugin, MethodCallHandler {
    private MethodChannel channel;
    Activity activity;
    UsbThermalPrinter mUsbThermalPrinter;
    private boolean lowBattery = false;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.telpo.service");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("CONNECT")) {
            IntentFilter pIntentFilter = new IntentFilter();

            registerReceiver(printReceive, pIntentFilter);

        } else if (call.method.equals("DISCONNECT")) {
            unregisterReceiver(printReceive);
        }


        if (call.method.equals("ACTION_PRINT_IMAGE")) {
            byte[] bytes = call.argument("bytes");
            try {
                assert bytes != null;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(6);
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                mUsbThermalPrinter.printLogo(bitmap, false);
                result.success(bytes);
            } catch (Exception e) {
                Toast.makeText(activity, "Error print image:" + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(printReceive);
        ThermalPrinter.stop();
        super.onDestroy();
    }

    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                //TPS390 can not print,while in low battery,whether is charging or not charging
                if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()) {
                    lowBattery = level * 5 <= scale;
                } else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        lowBattery = level * 5 <= scale;
                    } else {
                        lowBattery = false;
                    }
                }
            }
            //Only use for TPS550MTK devices
            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
                int status = intent.getIntExtra("action", 0);
                int level = intent.getIntExtra("level", 0);
                if (status == 0) {
                    lowBattery = level < 1;
                } else {
                    lowBattery = false;
                }
            }
        }
    };
}
