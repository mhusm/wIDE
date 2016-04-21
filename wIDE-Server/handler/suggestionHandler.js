var mdnHtml = require("../sources/mdn/MdnHtmlLookup");
var mdnJS = require("../sources/mdn/MdnJSLookup");
var mdnCSS = require("../sources/mdn/MdnCssLookup");

var suggestionHandler = {
    handle: function (lang, type, key, value, children, callback) {

        var result = {};
        result.lang = lang;
        result.type = type;
        result.key = key;
        result.value = value;
        result.children = [];
        result.caniuse = caniuse.query(result, lang, type, key);

        if (lang === "HTML") {
            mdnHtml.query(result, key, children, callback);

        } else if (lang === "JS") {
            mdnJS.resolveFullJSQuery(result, key, children, callback);

        } else if (lang === "CSS") {
            mdnCSS.query(result, key, children, callback);

        } else {
            console.log("Unknown language discovered: " + lang);
            return '{"type": "error", "message": "Unknown language: ' + lang + '"}'
        }

        return result;
    }
}

module.exports = suggestionHandler;
