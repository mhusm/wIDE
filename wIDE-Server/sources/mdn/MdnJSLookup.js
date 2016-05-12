var https = require('https');
var htmlParser = require("htmlparser2");
var queryHandler = require("../../handler/queryHandler");

var mdnJS = {
    resolveFullJSQuery: function(result, func, candidates, callback) {
        var cand = JSON.parse(candidates.shift());
        if (cand !== null) {
            mdnJS.query(result, cand.key, JSON.parse(cand.value).receiver, JSON.parse(cand.value).file, candidates, mdnJS.resolveFullJSQuery, callback);

        } else if (callback !== undefined) {
            callback(result);
            console.log("callback");
        }
    },

    query: function (results, func, receiver, file, candidates, next, callback) {
        console.log("mdnJS: request [function] " + func + " [receiver] " + receiver + " [file] " + file);

        if (receiver === "HTMLDocument") {
            receiver = "Document";
        }

        var result = {};
        result.lang = "JS";
        result.type = "callCandidate";
        result.key = receiver;
        result.parent = func;
        result.value = {"receiver": receiver, "file": file};
        result.documentation = {};
        results.children.push(result);

        var page = "";

        if (file === "DOMCore.js" || file === "DOMEvents.js" || file === "DHTML.js") {
            // Default JS-DOM functionality
            mdnJS._loadDOMFunctionPage(results, result, func, receiver, page, candidates, next, callback);
        }

        else if (callback !== undefined) {
            callback(results);
        }
        //mdnHtml._loadFunctionsPage(result, func, callee, callback);
    },

    _loadDOMFunctionPage: function (results, result, func, receiver, page, candidates, next, callback) {
        console.log("mdnJS: load function " + receiver + "." + func + "()");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/API/' + receiver + "/" + func,
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        var httpreq = https.request(options, function (response) {
            response.setEncoding('utf8');

            // ERROR HANDLING
            response.on('error', function (error) {
                console.log('mdnJS: Error while receiving MDN response.');
                console.log(error);
            });

            // DATA RECEIVING
            response.on('data', function (chunk) {
                page += chunk;
            });

            // FULL DATA RECEIVED: DO SEARCH.
            response.on('end', function () {
                mdnJS._parseFunctionPage(results, result, func, receiver, page, candidates, next, callback);
            });
        });

        httpreq.end();
    },

    _parseFunctionPage: function(results, result, func, receiver, page, candidates, next, callback) {
        console.log("mdnJS: Parse function page");
        var mdn = {};

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
                                ||currentExampleText != "") {
                                examples.push({
                                    "title": currentExampleTitle,
                                    "code": currentExampleCode,
                                    "text": currentExampleText});
                            }
                            mdn[prevCategoryName] = examples;

                        } else {
                            mdn[prevCategoryName] = currentCategoryContent;
                        }
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
                        default:
                            // reset category & content
                            currentCategory = "";
                            currentCategoryContent = "";
                            return;
                    }

                    // reset category & content
                    currentCategoryContent = "";

                }

                if (currentCategory === "examples") {

                    if ((name === "h3" || name === "h2")
                        && (currentExampleTitle != "" || currentExampleCode != "" || currentExampleText != "")) {
                        examples.push({
                            "title": currentExampleTitle,
                            "code": currentExampleCode,
                            "text": currentExampleText});

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

                    } else {
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
                if (currentCategory !== "") {
                    currentCategoryContent += text.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;").replace("\n", "")
                }

                // text for examples
                if (inExampleTitle) {
                    currentExampleTitle += text;
                } else if (inExampleCode) {
                    currentExampleCode += text;
                }
            },
            onclosetag: function (tagname) {
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
                    }
                }
            }
        }, {decodeEntities: true});
        parser.write(page);
        parser.end();

        result.documentation.mdn = mdn;

        if (callback !== undefined) {
            callback(results, func, candidates, next);
        }

        var cache = require("../../cache/cache");
        cache.refreshDocumentationCache(result);

    }
}

module.exports = mdnJS;
