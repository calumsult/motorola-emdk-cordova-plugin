/*global cordova, module*/

module.exports = {
    start: function () {
        cordova.exec(null, null, 'MotorolaScanner', 'start', []);
    },
    registerForBarcode: function (callback) {
        cordova.exec(callback, null, 'MotorolaScanner', 'register', []);
    },
    trigger: function () {
        cordova.exec(null, null, 'MotorolaScanner', 'trigger', []);
    }
};