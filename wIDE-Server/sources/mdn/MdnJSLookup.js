var https = require('https');
var htmlParser = require("htmlparser2");
var queryHandler = require("../../routes/queryHandler");

var mdnJS = {
    resolveFullJSQuery: function(result, func, candidates, callback) {
        var cand = candidates.shift();
        if (cand !== null) {
            mdnJS.query(result, cand.key, cand.value.receiver, cand.value.file, candidates, mdnJS.resolveFullJSQuery, callback);
        } else if (callback !== undefined) {
            callback(result);
            console.log("callback");
        }
        //for (var i in children) {
            //        if (children[i] !== null) {
            //            var child = children[i];
            //            var res = queryHandler.handle(child.lang, child.type, child.key, child.value, child.children);
            //            result.children.push(res);
            //        }
            //    }
    },

    query: function (results, func, receiver, file, candidates, next, callback) {
        console.log("mdnJS: request [function] " + func + " [receiver] " + receiver + " [file] " + file);

        var result = {};
        result.lang = "JS";
        result.type = "callCandidate";
        result.key = func;
        result.value = {"receiver": receiver, "file": file};
        results.children.push(result);

        var page = "";

        if (file === "DOMCore.js" || file === "DOMEvents.js") {
            // Default JS-DOM functionality
            mdnJS._loadDOMFunctionPage(results, result, func, receiver, page, candidates, next, callback);
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

        // PARSE MDN RESPONSE.
        var parser = new htmlParser.Parser({

            // PARSE ATTRIBUTES AND EXPLANATIONS.
            onopentag: function (name, attribs) {

                // parse categories (Summary, Attributes, Examples, Compatibility, Notes, See also)
                if (name === "h2") {
                    var prevCategoryName = currentCategory.toString();
                    if (prevCategoryName !== "") {
                        // Write content of category to result
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
            },
            onclosetag: function (tagname) {
                if (currentCategory !== "") {
                    if (tagname === "code" || tagname === "pre") {
                        inCode = false;
                    }

                    currentCategoryContent += "</" + tagname + ">";

                }
            }
        }, {decodeEntities: true});
        parser.write(page);
        parser.end();

        result.mdn = mdn;

        if (callback !== undefined) {
            callback(results, func, candidates, next);
        }
    },

    _loadFunctionsPage: function(result, func, receiver, callback) {
        console.log("mdnJS: load functions for " + func + ".");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/JavaScript/Reference/Methods_Index',
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
                mdnHtml._parseFunctionsPage(result, func, callee, page, callback);
            });
        });

        httpreq.end();
    },

    _parseFunctionsPage: function(result, func, callee, page, callback) {
        var functions = {};
        var inArticle = false;
        var inUl = false;
        var inLi = false;
        var inA = false;
        var inCode = false;
        var currentLink = "";
        var currentKey = "";

        // PARSE MDN RESPONSE.
        var parser = new htmlParser.Parser({

            // PARSE ATTRIBUTES AND EXPLANATIONS.
            onopentag: function (name, attribs) {
                if (name === "article" && attibs.id === "wikiArticle") {
                    inArticle = true;
                } else if (inArticle && name === "ul") {
                    inUl = true;
                } else if (inArticle && inUl && name === "li") {
                    inLi = true;
                } else if (inArticle && inUl && inLi && name === "a") {
                    inA = true;
                    currentLink = attibs.href;
                } else if (inArticle && inUl && inLi && inA && name === "code") {
                    inCode = true;
                }
            },
            ontext: function (text) {
                if (inCode === true) {
                    currentKey = text;
                }
            },
            onclosetag: function (tagname) {
                if (tagname === "code") {
                    if (inCode) {
                        // add link to functions list
                        functions[currentKey] = currentLink;
                    }
                    inCode = false;
                } else if (tagname === "a") {
                    inA = false;
                } else if (tagname === "li") {
                    inLi = false;
                } else if (tagname === "ul") {
                    inUl = false;
                } else if (tagname === "article") {
                    inArticle = false;
                }
            }
        }, {decodeEntities: true});
        parser.write(page);
        parser.end();

        if (functions[func] != undefined) {
            // function was found directly
            mdnHtml._loadFunctionPage(result, func, callee, functions[func], callback);
        } else {
        }

        mdnHtml._loadFunctionPage(result, func, callee, pageAddress, callback);
    },

    _loadWindowPage: function (result, funcName, callback) {

    },

    _loadDocumentPage: function (result, funcName, callback) {

    },

    _parsePage: function (result, funcName, callback) {

    },

    _loadFunctionPage: function(result, func, callee, pageAddress, callback) {
        console.log("mdnJS: load function page for " + func + ".");
        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/CSS/' + pageAddress,
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
                mdnHtml._parseFunctionPage(result, func, callee, page, callback);
            });
        });

        httpreq.end();
    },

    _parseFunctionsPage: function(result, func, callee, page, callback) {
        var tagAttributes = {};

        // PARSE MDN RESPONSE.
        var parser = new htmlParser.Parser({

            // PARSE ATTRIBUTES AND EXPLANATIONS.
            onopentag: function (name, attribs) {

            },
            ontext: function (text) {

            },
            onclosetag: function (tagname) {
            }
        }, {decodeEntities: true});
        parser.write(page);
        parser.end();

        mdnHtml._handleFunctionPage(result, func, callee, content, callback);
    },

    _handleFunctionPage: function(result, func, callee, content, callback) {
        callback(result);
    }
}

module.exports = mdnJS;
