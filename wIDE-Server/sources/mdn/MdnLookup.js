var https = require('https');
var mdnHtml = require("./MdnHtmlLookup");
var mdnJS = require("./MdnJSLookup");
var mdnCSS = require("./MdnCssLookup");

var mdnLookup = {
    query: function (result, lang, type, key, value, children, callback) {
        if (lang === "HTML") {
            mdnHtml.query(result, key, children, callback);

        } else if (lang === "JS") {
            mdnJS.resolveFullJSQuery(result, key, children, callback);

        } else if (lang === "CSS") {
            mdnCSS.query(result, key, children, callback);

        } else {
            console.log("Unknown language discovered: " + lang);
            if (callback !== undefined) {
                callback('{"type": "error", "message": "Unknown language: ' + lang + '"}');
            }

            return '{"type": "error", "message": "Unknown language: ' + lang + '"}'
        }

        return result;
    }
}

module.exports = mdnLookup;
