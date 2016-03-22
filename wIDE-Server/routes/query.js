var express = require('express');
var query = express();

var queryHandler = require("./queryHandler");

/* GET users listing. */
query.use('/', function(req, res) {
    var parameters = req.body.parameters;

    if (parameters === undefined) {
        console.log("Incomplete query request received.")
        res.send('{ "type": "error", "message": "Incomplete request."}');
        return;
    }

    var language = parameters.language;
    var type = parameters.type;
    var key = parameters.key;
    var value = parameters.value;
    var children = parameters.children;

    queryHandler.handle(language, type, key, value, children);

    var attribute_name = req.body.attribute_name;
    var attribute_value = req.body.attribute_value;
    var attributes = req.body.attributes;

    if (attribute_name === undefined || (attribute_value === undefined && attributes === undefined)) {
        console.log('Incomplete HTML request received.');
        res.send("error");
        return;
    }

    console.log('HTML request received: [attribute] ' + attribute_name + ' [value] ' + attribute_value);

    if (attributes !== undefined) {
        console.log(attributes);
        mdnHtml.queryFunction(attribute_name, attributes);
    }

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
    console.log("No lookup result found.");
    res.send("No lookup result found.");
});

module.exports = query;
