var https = require('https');
var htmlParser = require("htmlparser2");
var queryHandler = require("../../routes/queryHandler");

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
        result.mdn = {};

        var tagAttributes = {};
        var currentKey = undefined;
        var currentValue = "";
        var inDD = false;

        var currentCategory = "";
        var currentCategoryContent = "";
        var inCode = false;

        // for example snippets
        var examples = [];
        var currentExampleTitle = "";
        var inExampleTitle = false;
        var currentExampleCode = "";
        var inExampleCode = false;
        var currentExampleText = "";

        // PARSE MDN RESPONSE.
        var parser = new htmlParser.Parser({

                // PARSE ATTRIBUTES AND EXPLANATIONS.
                onopentag: function (name, attribs) {
                    // parse categories (Summary, Attributes, Examples, Compatibility, Notes, See also)
                    if (name === "h2") {
                        var prevCategoryName = currentCategory.toString();
                        if (prevCategoryName !== "") {
                            // Write content of category to result
                            if (prevCategoryName === "examples") {
                                if (currentExampleTitle != ""
                                    || currentExampleCode != ""
                                    || currentExampleText != "") {
                                    examples.push({
                                        "title": currentExampleTitle,
                                        "code": currentExampleCode,
                                        "text": currentExampleText
                                    });
                                }
                                result.mdn[prevCategoryName] = examples;

                            } else {
                                result.mdn[prevCategoryName] = currentCategoryContent;
                            }
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

                    if (currentCategory === "examples") {

                        if ((name === "h3" || name === "h2")
                            && (currentExampleTitle != "" || currentExampleCode != "" || currentExampleText != "")) {
                            examples.push({
                                "title": currentExampleTitle,
                                "code": currentExampleCode,
                                "text": currentExampleText
                            });

                            currentExampleTitle = "";
                            currentExampleCode = "";
                            currentExampleText = "";
                        }

                        // add content to part of example
                        if (inExampleTitle) {
                            currentExampleTitle += " <" + name;
                            for (attr in attribs) {
                                currentExampleTitle += ' ' + attr + '="' + attribs[attr] + '"';
                            }
                            currentExampleTitle += ">";

                        } else if (inExampleCode) {
                            currentExampleCode += " <" + name;
                            for (attr in attribs) {
                                currentExampleCode += ' ' + attr + '="' + attribs[attr] + '"';
                            }
                            currentExampleCode += ">";

                        } else if (name !== "pre" && name !== "h3") {
                            currentExampleText += " <" + name;
                            for (attr in attribs) {
                                currentExampleText += ' ' + attr + '="' + attribs[attr] + '"';
                            }

                            currentExampleText += ">";
                        }

                        // store examples one-by-one
                        if (name === "h3") {
                            inExampleTitle = true;
                        } else if (name === "pre") {
                            inExampleCode = true;
                        }
                    }

                    if (currentCategory !== "") {
                        currentCategoryContent += " <" + name;
                        for (attr in attribs) {
                            currentCategoryContent += ' ' + attr + '="' + attribs[attr] + '"';
                        }

                        currentCategoryContent += ">";

                        if (name === "code" || name === "pre") {
                            inCode = true;
                        }
                    }
                },
                ontext: function (text) {
                    if (currentKey !== undefined && inDD) {
                        currentValue += text;
                    }

                    if (currentCategory !== "") {
                        currentCategoryContent += text.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;").replace("\n", "")
                    }

                    if (currentCategory === "examples") {
                        // text for examples
                        if (inExampleTitle) {
                            currentExampleTitle += text;
                        } else if (inExampleCode) {
                            currentExampleCode += text;
                        } else {
                            currentExampleText += text;
                        }
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
                        if (tagname === "code" || tagname === "pre") {
                            inCode = false;
                        }

                        currentCategoryContent += "</" + tagname + ">";

                    }

                    if (currentCategory === "examples") {
                        if (tagname === "h3") {
                            inExampleTitle = false;
                        } else if (tagname === "pre") {
                            inExampleCode = false;
                        } else if (inExampleTitle) {
                            currentExampleTitle += "</" + tagname + ">";
                        } else if (inExampleCode) {
                            currentExampleCode += "</" + tagname + ">";
                        } else {
                            currentExampleText += "</" + tagname + ">";
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
            if (attributes[attribute] !== null) {

                // present attribute is supported
                if (tagAttributes[attributes[attribute].key] !== null) {
                    status = 1;

                    // Handle sub JS or CSS
                    var subNodes = [];
                    for (var i in attributes[attribute].children) {
                        if (attributes[attribute].children[i] !== null
                            && (attributes[attribute].children[i].lang === "JS"
                            || attributes[attribute].children[i].lang === "CSS")) {

                            // Sub JS or CSS -> Get lookup results
                            var queryHandler = require("../../routes/queryHandler");
                            subNodes.push(queryHandler.handle(
                                attributes[attribute].children[i].lang,
                                attributes[attribute].children[i].type,
                                attributes[attribute].children[i].key,
                                attributes[attribute].children[i].value,
                                attributes[attribute].children[i].children));
                        }
                    }

                    // The attribute is supported
                    presentAttributes += attributes[attribute].key + ", ";
                    //console.log("mdnHtml: Present attribute: " + attributes[attribute].key);
                    result.children.push(
                        {
                            "lang": attributes[attribute].lang,
                            "type": attributes[attribute].type,
                            "key": attributes[attribute].key,
                            "value": attributes[attribute].value,
                            "status": 1,
                            "children": subNodes,
                            "info": tagAttributes[attributes[attribute].key]
                        });

                    delete tagAttributes[attributes[attribute].key];

                } else {
                    // The attribute is not supported
                    invalidAttributes += attributes[attribute].key + ", ";
                    //console.log("mdnHtml: Not supported attribute: " + attributes[attribute].key);
                    result.children.push(
                        {
                            "lang": attributes[attribute].lang,
                            "type": attributes[attribute].type,
                            "key": attributes[attribute].key,
                            "value": attributes[attribute].value,
                            "status": -1,
                            "info": tagAttributes[attributes[attribute].key]
                        });

                    delete tagAttributes[attributes[attribute].key];
                }
            }
        }

        // add other available attributes
        for (var attribute in tagAttributes) {
            if (attributes[attribute] === undefined) {

                // The attribute is not supported
                nonPresentAttributes += attribute + ", ";
                //console.log("mdnHtml: Non present attribute: " + attribute);
                result.children.push(
                    {
                        "lang": "HTML",
                        "type": "attribute",
                        "key": attribute,
                        "status": 0,
                        "info": tagAttributes[attribute]
                    });
            }
        }

        console.info(presentAttributes);
        console.info(nonPresentAttributes);
        console.info(invalidAttributes);

        callback(result);
    }
}

module.exports = mdnHtml;
