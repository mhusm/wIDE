var caniuse = require("../sources/caniuse/CanIUseLookup");
var mdn = require("../sources/mdn/MdnLookup");

var queryHandler = {
    handle: function (lang, type, key, value, children, callback) {

        var result = {};
        result.lang = lang;
        result.type = type;
        result.key = key;
        result.value = value;
        result.children = [];
        result.documentation = {};
        result.documentation.caniuse = caniuse.query(result, lang, type, key);

        mdn.query(result, lang, type, key, value, children, callback);

        return result;
    }
}

module.exports = queryHandler;
