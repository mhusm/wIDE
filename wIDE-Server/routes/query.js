var express = require('express');
var query = express();

var queryHandler = require("./queryHandler");

/* GET users listing. */
query.use('/', function(req, res) {
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

    var response = queryHandler.handle(lang, type, key, value, children, function(response) {
        var stringResponse = JSON.stringify(response);
        console.log("RESPONSE: " + stringResponse);
        res.send(stringResponse);
    });

    //var attribute_name = req.body.attribute_name;
    //var attribute_value = req.body.attribute_value;
    //var attributes = req.body.attributes;
    //
    //if (attribute_name === undefined || (attribute_value === undefined && attributes === undefined)) {
    //    console.log('Incomplete HTML request received.');
    //    res.send("error");
    //    return;
    //}
    //
    //console.log('HTML request received: [attribute] ' + attribute_name + ' [value] ' + attribute_value);
    //
    //if (attributes !== undefined) {
    //    console.log(attributes);
    //    mdnHtml.queryFunction(attribute_name, attributes);
    //}
    //
    //// Search for potential matching features
    //// -> Prevent empty return values.
    //var attributes = caniuse.find(attribute_name);
    //if (attributes != undefined) {
    //    if (attributes instanceof Array) {
    //        // multiple potential results
    //        for (var object in attributes) {
    //            if (attributes[object] === attribute_name) {
    //                var response = caniuse.getSupport(attribute_name);
    //                console.log(response);
    //                res.send(response);
    //                return;
    //            }
    //        }
    //    } else {
    //        // one potential result
    //        if (attributes === attribute_name) {
    //            var response = caniuse.getSupport(attribute_name);
    //            console.log(response);
    //            res.send(response);
    //            return;
    //        }
    //    }
    //}
    //console.log("No lookup result found.");
    //res.send("No lookup result found.");
});

module.exports = query;
