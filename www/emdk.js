	/*global cordova, module*/

    module.exports = {
        start: function () {
            cordova.exec(null, null, 'ZebraScanner', 'start', []);
        },
        registerForBarcode: function (callback) {
            cordova.exec(callback, null, 'ZebraScanner', 'register', []);
        },
        trigger: function (barcode) {
            cordova.exec(null, null, 'ZebraScanner', 'trigger', [barcode]);
        }
    };