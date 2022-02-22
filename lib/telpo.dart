import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class Telpo {
  static const MethodChannel _channel = MethodChannel('com.telpo.service');

  static Future connect() async {
    await await _channel.invokeMethod('CONNECT');
  }

  static Future disconnect() async {
    await await _channel.invokeMethod('DISCONNECT');
  }

  static Future printImage(Uint8List image) async {
    await _channel.invokeMethod('ACTION_PRINT_IMAGE', {'bytes': image});
  }
}
