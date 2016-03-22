var caniuse = require("../sources/caniuse/CanIUseLookup");
var mdnHtml = require("../sources/mdn/MdnHtmlLookup");

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
            //for (var i in children) {
            //    if (children[i] !== null && children[i].children.length > 0) {
            //        for (var j in children[i].children) {
            //            if (children[i].children[j].lang === "JS") {
            //                console.log("nested JS");
            //            } else if (children[i].children[j].lang === "CSS") {
            //                console.log("nested CSS");
            //            }
            //        }
            //    }
            //}
            mdnHtml.query(result, key, children, callback);
        } else if (lang === "JS") {
            if (type === "call") {
                for (var i in children) {
                    if (children[i] !== null) {
                        var child = children[i];
                        result.children.push(queryHandler.handle(child.lang, child.type, child.key, child.value, child.children));
                    }
                }

                if (callback !== undefined) {
                    callback(result);
                }
            }
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
