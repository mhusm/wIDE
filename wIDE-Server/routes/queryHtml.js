var express = require('express');
var caniuse = require('caniuse-api');
var queryHtml = express();

/* GET users listing. */
queryHtml.use('/', function(req, res) {
    var attribute_name = req.body.attribute_name;
    var attribute_value = req.body.attribute_value;

    if (attribute_name === undefined || attribute_value === undefined) {
        console.log('Incomplete HTML request received.');
        res.send("error");
        return;
    }

    console.log('HTML request received: [attribute] ' + attribute_name + ' [value] ' + attribute_value);

    // Search for potential matching features
    // -> Prevent empty return values.
    var attributes = caniuse.find(attribute_name);
    if (attributes != undefined) {
        if (attributes instanceof Array) {
            // multiple potential results
            for (var object in attributes) {
                if (attributes[object] === attribute_name) {
                    var response = caniuse.getSupport(attribute_name);
                    console.log(response);
                    res.send(response);
                    return;
                }
            }
        } else {
            // one potential result
            if (attributes === attribute_name) {
                var response = caniuse.getSupport(attribute_name);
                console.log(response);
                res.send(response);
                return;
            }
        }
    }
    console.log("No HTML lookup result found.");
    res.send("No HTML lookup result found.");
});

module.exports = queryHtml;
