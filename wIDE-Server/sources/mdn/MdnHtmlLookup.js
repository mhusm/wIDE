var https = require('https');
var htmlParser = require("htmlparser2");
var queryHandler = require("../../handler/queryHandler");

var mdnHtml = {
    query: function (result, tag, attributes, callback) {
        console.log("mdnHtml: request [tag] " + tag + " [attributes] " + JSON.stringify(attributes));

        var page = "";
        mdnHtml._loadTagPage(result, tag, attributes, page, callback);
    },

    _loadTagPage: function (result, tag, attributes, page, callback) {
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

    _loadGlobalPage: function (result, tag, attributes, page, callback) {
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

    _parsePage: function (result, tag, attributes, page, callback) {
        result.documentation.mdn = {};

        var tagAttributes = {};
        var currentKey = undefined;
        var currentValue = "";
        var inDD = false;

        var currentCategory = "";
        var currentCategoryContent = "";
        var inCode = false;

        // for example snippets
        var examples = [];

        // PARSE MDN RESPONSE.
        var parser = new htmlParser.Parser({

                // PARSE ATTRIBUTES AND EXPLANATIONS.
                onopentag: function (name, attribs) {
                    // parse categories (Summary, Attributes, Examples, Compatibility, Notes, See also)
                    if (name === "h2") {
                        var prevCategoryName = currentCategory.toString();
                        if (prevCategoryName !== "") {
                            result.documentation.mdn[prevCategoryName] = currentCategoryContent;
                        }

                        switch (attribs.id) {
                            case "Summary":
                                currentCategory = "summary";
                                break;
                            case "Attributes":
                                currentCategory = "attributes";
                                break;
                            case "Examples":
                            case "Example":
                                currentCategory = "examples";
                                break;
                            case "Browser_compatibility":
                                currentCategory = "compatibility";
                                break;
                            case "Notes":
                                currentCategory = "notes";
                                break;
                            case "See_also":
                                currentCategory = "seeAlso";
                                break;
                            default:
                                // workaround for bad structured examples
                                if (attribs.id !== undefined && attribs.id.indexOf("Example") === 0) {
                                    currentCategory = "examples";
                                } else {

                                    // reset category & content
                                    currentCategory = "";
                                    currentCategoryContent = "";
                                }
                                return;
                        }

                        // reset category & content
                        currentCategoryContent = "";

                    }

                    // parse attributes (one-by-one).
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

                    if (currentCategory !== "") {
                        if (name === "pre") {
                            if (!inCode) {
                                inCode = true;
                                currentCategoryContent += '<pre><code class="language-html line-numbers">';
                            }
                        } else {
                            currentCategoryContent += " <" + name;
                            for (attr in attribs) {
                                currentCategoryContent += ' ' + attr + '="' + attribs[attr] + '"';
                            }

                            currentCategoryContent += ">";
                        }
                    }
                },
                ontext: function (text) {
                    if (currentKey !== undefined && inDD) {
                        currentValue += text;
                    }

                    if (currentCategory !== "") {
                        currentCategoryContent += text.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;").replace("\n", "<br />");
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

                    if (currentCategory !== "") {
                        if (tagname === "pre") {
                            if (inCode) {
                                currentCategoryContent += "</code></pre>";
                                inCode = false;
                            }
                            currentCategoryContent += "</code>";
                        } else {
                            if (tagname === "code" || tagname === "pre") {
                                inCode = false;
                            }

                            currentCategoryContent += "</" + tagname + ">";
                        }
                    }
                }
            },
            {
                decodeEntities: true
            }
            )
            ;
        parser.write(page);
        parser.end();

        mdnHtml._handleAttributes(result, tag, attributes, tagAttributes, callback);
    },

    _handleAttributes: function (result, tag, attributes, tagAttributes, callback) {

        var presentAttributes = "mdnHtml: Present attributes: ";
        var nonPresentAttributes = "mdnHtml: Non present attributes: ";
        var invalidAttributes = "mdnHtml: Invalid attributes: ";

        // present attributes
        for (var attribute in attributes) {
            var status = 0;

            // filter terminating null
            var parsedAttribute = JSON.parse(attributes[attribute]);
            if (parsedAttribute !== null && parsedAttribute !== undefined) {

                // present attribute is supported
                if (tagAttributes[parsedAttribute.key] !== null) {
                    status = 1;

                    // Handle sub JS or CSS
                    var subNodes = [];
                    for (var i in parsedAttribute.children) {
                        if (parsedAttribute.children[i] !== null
                            && (parsedAttribute.children[i].lang === "JS"
                            || parsedAttribute.children[i].lang === "CSS")) {

                            // Sub JS or CSS -> Get lookup results
                            var queryHandler = require("../../handler/queryHandler");
                            subNodes.push(queryHandler.handle(
                                parsedAttribute.children[i].lang,
                                parsedAttribute.children[i].type,
                                parsedAttribute.children[i].key,
                                parsedAttribute.children[i].value,
                                parsedAttribute.children[i].children));
                        }
                    }

                    // The attribute is supported
                    presentAttributes += parsedAttribute.key + ", ";
                    //console.log("mdnHtml: Present attribute: " + attributes[attribute].key);

                    var child = {
                        "lang": parsedAttribute.lang,
                        "type": parsedAttribute.type,
                        "key": parsedAttribute.key,
                        //"value": attributes[attribute].value,
                        //"status": 1,
                        "children": subNodes,
                        "parent": result.key,
                        "documentation": {
                            "mdn": {
                                "summary": tagAttributes[parsedAttribute.key],
                                "examples": [],
                            }
                        }
                    };
                    result.children.push(child);

                    var cache = require("../../cache/cache");
                    cache.refreshDocumentationCache(child);

                    delete tagAttributes[parsedAttribute.key];

                } else {
                    // The attribute is not supported
                    invalidAttributes += parsedAttribute.key + ", ";
                    //console.log("mdnHtml: Not supported attribute: " + attributes[attribute].key);
                    result.children.push(
                        {
                            "lang": parsedAttribute.lang,
                            "type": parsedAttribute.type,
                            "key": parsedAttribute.key,
                            //"value": attributes[attribute].value,
                            "documentation": {
                                "mdn": {
                                    "summary": tagAttributes[parsedAttribute.key],
                                    "examples": []
                                }
                            }
                        });

                    delete tagAttributes[parsedAttribute.key];
                }
            }
        }

        // add other available attributes
        for (var attribute in tagAttributes) {
            if (attributes !== undefined && attributes[attribute] === undefined) {

                // The attribute is not supported
                nonPresentAttributes += attribute + ", ";
                //console.log("mdnHtml: Non present attribute: " + attribute);
                var child = {
                    "lang": "HTML",
                    "type": "attribute",
                    "key": attribute,
                    "documentation": {
                        "mdn": {
                            "summary": tagAttributes[attribute],
                            "examples": []
                        }
                    },
                    "parent": result.key
                };

                result.children.push(child);

                var cache = require("../../cache/cache");
                cache.refreshDocumentationCache(child);
            }
        }

        console.info(presentAttributes);
        console.info(nonPresentAttributes);
        console.info(invalidAttributes);

        callback(result);
    }
}

module.exports = mdnHtml;
