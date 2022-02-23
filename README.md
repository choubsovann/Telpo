## Telpo POS system
This project for telpo POS mobile.
###### pubspec.yaml
```yaml
telpo:
  git: https://github.com/choubsovann/telpo.git
```
###### Using Telpo
```dart
import 'package:telpo/telpo.dart';
```
```Dart
Telpo.connect();
Telpo.printImage(Uint8list image);
Telpo.disconnect();
```
