var express = require('express');
var suggestion = express();

var cache = require("../cache/cache");

/* GET users listing. */
suggestion.use('/', function(req, res) {
    var params = req.body.parameters;

    console.log("REQUEST: " + params);

    if (params === undefined) {
        console.error("Incomplete query request received.")
        res.send('{ "type": "error", "message": "Incomplete request."}');
        return;
    }

    var parameters = JSON.parse(params);
    var lang = parameters.lang;
    var type = parameters.type;
    var key = parameters.key;
    var value = parameters.value;
    var children = parameters.children;

    //queryHandler.handle(lang, type, key, value, children,
    var response = cache.suggest(lang, type, key, value, children, function(response) {
        var stringResponse = JSON.stringify(response);
        console.log("RESPONSE: " + stringResponse);
        res.send(stringResponse);
    });
});

module.exports = suggestion;
