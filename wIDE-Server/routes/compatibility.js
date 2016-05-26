var express = require('express');
var compatibility = express();

var compatibilityHandler = require("./../handler/compatibilityHandler");

/* GET browser_versions. */
compatibility.use('/browser_versions/', function(req, res) {
    var params = req.body.parameters;

    console.log("COMPATIBILITY REQUEST: " + params);

    if (params === undefined) {
        console.error("Incomplete compatibility request received.")
        res.send('{ "type": "error", "message": "Incomplete request."}');
        return;
    }

    //queryHandler.handle(lang, type, key, value, children,
    var response = compatibilityHandler.getBrowserVersions(function(response) {
        var stringResponse = JSON.stringify(response);
        console.log("COMPATIBILITY RESPONSE: " + stringResponse);
        res.send(stringResponse);
    });
});

// TODO: find compatibility

module.exports = compatibility;
