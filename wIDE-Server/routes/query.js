var express = require('express');
var query = express();

var cache = require("../cache/cache");
var queryHandler = require("./../handler/queryHandler");

/* GET users listing. */
query.use('/', function(req, res) {
    var params = req.body.parameters;

    console.log("QUERY REQUEST: " + params);

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
    var response = cache.lookupDocumentation(lang, type, key, value, null, children, function(response) {
        var stringResponse = JSON.stringify(response);
        console.log("QUERY RESPONSE: " + stringResponse);
        res.send(stringResponse);
    });
});

module.exports = query;
