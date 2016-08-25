var https = require('https');
var htmlParser = require("htmlparser2");
var queryHandler = require("../../handler/queryHandler");

var mdnCss = {
    query: function (result, attribute, value, callback) {
        console.log("mdnCss: request [attribute] " + attribute + " [value] " + value);

        var page = "";
        mdnCss._loadPage(result, attribute, value, page, callback);
    },

    _loadPage: function(result, attribute, value, page, callback) {
        console.log("mdnCss: load [attribute] " + attribute + " page.");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/CSS/' + attribute,
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        var httpreq = https.request(options, function (response) {
            response.setEncoding('utf8');

            // ERROR HANDLING
            response.on('error', function (error) {
                console.log('mdnCss: Error while receiving MDN response.');
                console.log(error);
            });

            // DATA RECEIVING
            response.on('data', function (chunk) {
                page += chunk;
            });

            // FULL DATA RECEIVED: DO SEARCH.
            response.on('end', function () {
                mdnCss._parsePage(result, attribute, value, page, callback);
            });
        });

        httpreq.end();
    },

    _parsePage: function(result, attribute, value, page, callback) {

        result.documentation.mdn = {};
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
                        case "Syntax":
                            currentCategory = "syntax";
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

                if (currentCategory !== "") {
                    if (name === "pre") {
                        if (!inCode) {
                            inCode = true;
                            currentCategoryContent += '<pre><code class="language-css line-numbers">';
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
                if (currentCategory !== "") {
                    currentCategoryContent += text.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;").replace("\n", "<br />")
                }
            },
            onclosetag: function (tagname) {

                if (currentCategory !== "") {
                    if (tagname === "pre") {
                        if (inCode) {
                            currentCategoryContent += "</code></pre>";
                            inCode = false;
                        }
                        currentCategoryContent += "</code>";
                    } else {
                        currentCategoryContent += "</" + tagname + ">";
                    }
                }
            }
        }, {decodeEntities: true});
        parser.write(page);
        parser.end();

        if (callback !== undefined) {
            callback(result);
        }
    }
}

module.exports = mdnCss;
