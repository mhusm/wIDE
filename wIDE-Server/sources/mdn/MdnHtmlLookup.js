var https = require('https');
var htmlParser = require("htmlparser2");
var queryHandler = require("../../routes/queryHandler");

var mdnHtml = {
    query: function (result, tag, attributes, callback) {
        console.log("mdnHtml: request [tag] " + tag + " [attributes] " + JSON.stringify(attributes));

        var page = "";
        mdnHtml._loadTagPage(result, tag, attributes, page, callback);
    },

    _loadTagPage: function(result, tag, attributes, page, callback) {
        console.log("mdnHtml: load [tag] " + tag + " attributes.");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/HTML/Element/' + tag,
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        var httpreq = https.request(options, function (response) {
            response.setEncoding('utf8');

            // ERROR HANDLING
            response.on('error', function (error) {
                console.log('mdnHtml: Error while receiving MDN response.');
                console.log(error);
            });

            // DATA RECEIVING
            response.on('data', function (chunk) {
                page += chunk;
            });

            // FULL DATA RECEIVED: DO SEARCH.
            response.on('end', function () {
                mdnHtml._loadGlobalPage(result, tag, attributes, page, callback);
            });
        });

        httpreq.end();
    },

    _loadGlobalPage: function(result, tag, attributes, page, callback) {
        console.log("mdnHtml: load global attributes.");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/HTML/Global_attributes',
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        var httpreq = https.request(options, function (response) {
            response.setEncoding('utf8');

            // ERROR HANDLING
            response.on('error', function (error) {
                console.log('mdnHtml: Error while receiving MDN response.');
                console.log(error);
            });

            // DATA RECEIVING
            response.on('data', function (chunk) {
                page += chunk;
            });

            // FULL DATA RECEIVED: DO SEARCH.
            response.on('end', function () {
                mdnHtml._parsePage(result, tag, attributes, page, callback);
            });
        });

        httpreq.end();
    },

    _parsePage: function(result, tag, attributes, page, callback) {
        var tagAttributes = {};
        var currentKey = undefined;
        var currentValue = "";
        var inDD = false;

        // PARSE MDN RESPONSE.
        var parser = new htmlParser.Parser({

            // PARSE ATTRIBUTES AND EXPLANATIONS.
            onopentag: function (name, attribs) {
                if (name === "strong" && attribs.id !== undefined && attribs.id.indexOf("attr-") === 0) {
                    currentKey = attribs.id.substr(5);
                } else if (name === "dt" && attribs.id !== undefined && attribs.id.indexOf("attr-") === 0) {
                    currentKey = attribs.id.substr(5);
                } else if (name === "dd" && currentKey !== undefined) {
                    inDD = true;
                } else if (currentKey !== undefined && inDD) {
                    currentValue += " <" + name;
                    for (attr in attribs) {
                        currentValue += ' ' + attr + '="' + attribs[attr] + '"';
                    }

                    currentValue += ">";
                }
            },
            ontext: function (text) {
                if (currentKey !== undefined && inDD) {
                    currentValue += text;

                }
            },
            onclosetag: function (tagname) {
                if (tagname === "dd") {
                    tagAttributes[currentKey] = currentValue;
                    currentKey = undefined;
                    currentValue = "";
                    inDD = false;

                } else if (currentKey !== undefined && inDD) {
                    currentValue += "</" + tagname + ">";
                }
            }
        }, {decodeEntities: true});
        parser.write(page);
        parser.end();

        mdnHtml._handleAttributes(result, tag, attributes, tagAttributes, callback);
    },

    _handleAttributes: function(result, tag, attributes, tagAttributes, callback) {
        for (var attribute in attributes) {
            if (attributes[attribute] !== null) {

                var subNodes = [];
                for (var i in attributes[attribute].children) {
                    if (attributes[attribute].children[i] !== null && (attributes[attribute].children[i].lang === "JS" || attribues[attribute].children[i].lang === "CSS")) {
                        var queryHandler = require("../../routes/queryHandler");
                        subNodes.push(queryHandler.handle(attributes[attribute].children[i].lang, attributes[attribute].children[i].type, attributes[attribute].children[i].key, attributes[attribute].children[i].value, attributes[attribute].children[i].children));
                    }
                }

                if (tagAttributes[attributes[attribute].key] !== undefined) {
                    console.log("mdnHtml: Matching attribute: " + attributes[attribute].key);
                    result.children.push(
                        {
                            "lang": attributes[attribute].lang,
                            "type": attributes[attribute].type,
                            "key": attributes[attribute].key,
                            "value": attributes[attribute].value,
                            "children": subNodes,
                            "mdn": tagAttributes[attributes[attribute].key]
                        });

                } else {
                    console.log("mdnHtml: Non-Matching attribute: " + attributes[attribute].key);
                    result.children.push(
                        {
                            "lang": attributes[attribute].lang,
                            "type": attributes[attribute].type,
                            "key": attributes[attribute].key,
                            "value": attributes[attribute].value,
                            "children": subNodes,
                            "mdn": "Not supported attribute."
                        });
                }
            }
        }

        result.mdn;

        callback(result);
    }
}

module.exports = mdnHtml;
