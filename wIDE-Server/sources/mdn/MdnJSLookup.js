var https = require('follow-redirects').https;
var htmlParser = require("htmlparser2");
var queryHandler = require("../../handler/queryHandler");
var caniuse = require("../caniuse/CanIUseLookup");

var mdnJS = {
    resolveFullJSQuery: function (result, func, candidates, callback) {

        if (result.type === "call") {
            // Query a function call -> Potentially multiple candidates

            if (candidates !== undefined && candidates.length > 0) {

                // traverse list of candidates -> send response after the last one
                var cand = JSON.parse(candidates.shift());
                if (cand !== null) {
                    mdnJS.query(result,
                        cand.key,
                        JSON.parse(cand.value).receiver,
                        JSON.parse(cand.value).file,
                        candidates,
                        mdnJS.resolveFullJSQuery,
                        callback);

                } else if (callback !== undefined) {
                    // no valid candidate
                    callback(result);
                }
            } else if (callback !== undefined) {
                // no more candidates
                callback(result);
            }
        } else if (result.type === "reference") {
            // Query a reference
            mdnJS.queryReference(result, callback);
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
        result.documentation.caniuse = caniuse.query(result, "JS", "callCandidate", func);
        results.children.push(result);

        var page = "";

        if (file === "DOMCore.js" || file === "DOMEvents.js" || file === "DHTML.js" || file === "DOMTraversalAndRange.js") {
            // Default JS-DOM functionality
            mdnJS._loadDOMFunctionPage(results, result, func, receiver, page, candidates, next, callback);
        } else {
            mdnJS._loadGlobalObjectFunctionPage(results, result, func, receiver, page, candidates, next, callback);
        }

        //else if (next !== undefined) {
        //    next(results, func, candidates, callback);
        //}
        //mdnHtml._loadFunctionsPage(result, func, callee, callback);
    },

    queryReference: function(result, callback) {
        console.log("mdnJS: request [reference] " + result.key);
        var page = "";
        result.documentation.mdn = {};
        mdnJS._loadGlobalObjectPage(result, page, callback);
    },

    _loadGlobalObjectPage: function (result, page, callback) {
        console.log("mdnJS: load object page " + result.key);
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/JavaScript/Reference/Global_Objects/' + result.key + "/",
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        console.log(options.host + options.path);
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
                mdnJS._parseObjectPage(result, page, callback);
            });
        });

        httpreq.end();
    },

    _loadDOMFunctionPage: function (results, result, func, receiver, page, candidates, next, callback) {
        console.log("mdnJS: load DOM function " + receiver + "." + func + "()");
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

    _loadGlobalObjectFunctionPage: function (results, result, func, receiver, page, candidates, next, callback) {
        console.log("mdnJS: load  object function " + receiver + "." + func + "()");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/JavaScript/Reference/Global_Objects/' + receiver + "/" + func,
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

    _parseFunctionPage: function (results, result, func, receiver, page, candidates, next, callback) {
        console.log("mdnJS: Parse function page");
        var mdn = {};

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
                        mdn[prevCategoryName] = currentCategoryContent;
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

                if (currentCategory !== "") {
                    if (name === "pre") {
                        if (!inCode) {
                            inCode = true;
                            currentCategoryContent += '<pre><code class="language-javascript line-numbers">';
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

        result.documentation.mdn = mdn;

        if (next !== undefined) {
            next(results, func, candidates, callback);
        }

        var cache = require("../../cache/cache");
        cache.refreshDocumentationCache(result);

        // create a reference entry in the cache -> support suggestions
        var objectResult = {};
        objectResult.lang = "JS";
        objectResult.type = "reference";
        objectResult.key = result.key;
        cache.refreshDocumentationCache(objectResult);
    },

    _parseObjectPage: function (result, page, callback) {
        console.log("mdnJS: Parse object page");
        var mdn = {};

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
                        mdn[prevCategoryName] = currentCategoryContent;
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
                            currentCategory = attribs.id;
                    }

                    // reset category & content
                    currentCategoryContent = "";

                }

                if (currentCategory !== "") {
                    if (name === "pre") {
                        if (!inCode) {
                            inCode = true;
                            currentCategoryContent += '<pre><code class="language-javascript line-numbers">';
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

        result.documentation.mdn = JSON.stringify(mdn); //mdn;

        if (callback !== undefined) {
            callback(result);
        }

        var cache = require("../../cache/cache");
        cache.refreshDocumentationCache(result);
    }
}

module.exports = mdnJS;
