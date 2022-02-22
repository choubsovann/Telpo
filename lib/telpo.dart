import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class Telpo {
  static const MethodChannel _channel = MethodChannel('com.telpo.service');

  static Future printImage(Uint8List image) async {
    await _channel.invokeMethod('ACTION_PRINT_IMAGE', {'bytes': image});
  }
}
