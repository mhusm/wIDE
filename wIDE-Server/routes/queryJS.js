var express = require('express');
var caniuse = require('caniuse-api');
var queryJS = express();

/* GET users listing. */
queryJS.use('/', function(req, res) {
    var function_name = req.body.function_name;
    var file_name = req.body.file_name;

    if (function_name === undefined || file_name === undefined) {
        console.log('Incomplete JS request received.');
        res.send("error");
        return;
    }

    console.log('JS request received: [function] ' + function_name + ' [file] ' + file_name);

    // Search for potential matching features
    // -> Prevent empty return values.
    var functions = caniuse.find(function_name);
    if (functions != undefined) {
        if (functions instanceof Array) {
            // multiple potential results
            for (var object in functions) {
                if (functions[object] === function_name) {
                    var response = caniuse.getSupport(function_name);
                    console.log(response);
                    res.send(response);
                    return;
                }
            }
        } else {
            // one potential result
            if (functions === function_name) {
                var response = caniuse.getSupport(function_name);
                console.log(response);
                res.send(response);
                return;
            }
        }
    }
    console.log("No JS result found.");
    res.send("No JS result found.");
});

module.exports = queryJS;
