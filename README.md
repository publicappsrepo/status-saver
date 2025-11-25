# Status Saver - WhatsApp Status Downloader

Save WhatsApp statuses easily with this open-source Android app.

## Features
- Save WhatsApp status images and videos
- Easy to use interface
- Material Design
- No ads

## Credits
This app is based on [WhatSave](https://github.com/mardous/WhatSave) by Christians Martínez A.

Original project: https://github.com/mardous/WhatSave

## License
This app is licensed under GPL-3.0. See [LICENSE](LICENSE) file for details.

## Build Instructions

### Prerequisites
- Android Studio (Arctic Fox or newer)
- JDK 11 or higher
- Android SDK (API Level 21 or higher)
- Gradle 7.0+

### Steps to Build
1. Clone this repository:
```
   git clone https://github.com/publicappsrepo/status-saver.git
```

2. Open the project in Android Studio

3. Sync Gradle files (Android Studio will automatically download dependencies)

4. Build the project:
   - Menu: Build → Make Project

5. Run on device/emulator:
   - Menu: Run → Run 'app'

### Build APK via Command Line
```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing configuration)
./gradlew assembleRelease

# Output location: app/build/outputs/apk/
```

## Dependencies
All dependencies are listed in `app/build.gradle` file.

## Contributing
Contributions are welcome! Please feel free to submit issues or pull requests.

## Contact
- Email: appsease2001@gmail.com
- Play Store: [Status Saver](https://play.google.com/store/apps/details?id=com.appsease.status.saver)