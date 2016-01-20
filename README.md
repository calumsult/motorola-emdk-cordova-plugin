# Cordova Motorola EMDK Barcode Scanning Plugin
This is a Cordova/Phonegap plugin to interact with Motorola/Zebra ruggedized devices' Barcode Scanners (eg, ET1, MC40, TC55). The plugin works via Zebra EMDK Barcode API. Plugin required the device with Zebra EMDK v3.0 or higher installed.

To install, run the following from your project command line: 
$ cordova plugin add https://github.com/scor4er/motorola-emdk-cordova-plugin.git

Usage:

```
document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {
  if (emdk) {
      emdk.start();
      emdk.registerForBarcode(function (data) {
          var labelType = data.type,
              barcode = data.barcode;
      });
  }
}
```
