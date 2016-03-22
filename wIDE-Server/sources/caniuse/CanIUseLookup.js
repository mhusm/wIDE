var https = require('https');
var caniuse = require('caniuse-api');

var caniuseLookup = {
    query: function (lang, type, key) {
        console.log("Send request to caniuse: [key] " + key);

        if (key === undefined) {
            console.log('Incomplete caniuse request received.');
            return '{"type": "error", "message": "Incomplete caniuse request."}'
        }

        // Search for potential matching features
        // -> Prevent empty return values.
        var attributes = caniuse.find(key);
        if (attributes != undefined) {
            if (attributes instanceof Array) {
                // multiple potential results
                for (var object in attributes) {
                    if (attributes[object] === key) {
                        var response = caniuse.getSupport(key);
                        console.log(response);
                        return
                    }
                }
            } else {
                // one potential result
                if (attributes === attribute_name) {
                    var response = caniuse.getSupport(attribute_name);
                    console.log("caniuse lookup: [lang] " + lang + " [type] " + type + " [key] " + key + " [response] " + response);
                    return response;
                }
            }
        }
        console.log("No caniuse result found.");
        return "No caniuse result.";
    }
}

module.exports = caniuseLookup;
