var caniuse = require("../sources/caniuse/CanIUseLookup");
var mdnHtml = require("../sources/mdn/MdnHtmlLookup");
var mdnJS = require("../sources/mdn/MdnJSLookup");

var queryHandler = {
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
            //if (type === "call") {
            //    for (var i in children) {
            //        if (children[i] !== null) {
            //            var child = children[i];
            //            var res = queryHandler.handle(child.lang, child.type, child.key, child.value, child.children);
            //            result.children.push(res);
            //        }
            //    }
            //
            //    if (callback !== undefined) {
            //        callback(result);
            //    }
            //} else if (type === "callCandidate") {
            //    mdnJS.query(result, key, value.receiver, value.file);
            //}

        } else if (lang === "CSS") {
            if (callback !== undefined) {
                callback(result);
            }
        } else {
            console.log("Unknown language discovered: " + lang);
            return '{"type": "error", "message": "Unknown language: ' + lang + '"}'
        }

        return result;
    }
}

module.exports = queryHandler;
