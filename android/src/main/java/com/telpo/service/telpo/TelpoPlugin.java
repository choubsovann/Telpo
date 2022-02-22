package com.telpo.service.telpo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.telpo.tps550.api.printer.UsbThermalPrinter;

/**
 * TelpoPlugin
 */
public class TelpoPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    Activity activity;
    UsbThermalPrinter mUsbThermalPrinter;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.telpo.service");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
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
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        mUsbThermalPrinter = new UsbThermalPrinter(activity);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
