var caniuse = require("../sources/caniuse/CanIUseLookup");
var mdnHtml = require("../sources/mdn/MdnHtmlLookup");

var queryHandler = {
    handle: function (lang, type, key, value, children) {

        var result = {};
        result.language = lang;
        result.type = type;
        result.key = key;
        result.value = value;
        result.caniuse = caniuse.query(lang, type, key);

        if (lang === "JS") {

        } else if (lang === "HTML") {
            result.mdn = mdnHtml.query();

        } else if (lang === "CSS") {

        } else {
            console.log("Unknown language discovered: " + lang);
            return '{"type": "error", "message": "Unknown language: ' + lang + '"}'
        }

        //for(child in children) {
        //    result.children.add(this.handle(child.language, child.type, child.key, child.value, child.children));
        //
        //}
    }
}

module.exports = queryHandler;
