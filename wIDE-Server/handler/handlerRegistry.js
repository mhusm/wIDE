var caniuse = require("../sources/caniuse/CanIUseLookup");
var mdn = require("../sources/mdn/MdnLookup");


var handlerRegistry = {
    compatibilityHandlers: {
        "caniuse" : caniuse
    },

    apiHandlers: {
        "caniuse" : caniuse
    },

    parseHandlers:  {
        "mdn": mdn
    }
}


module.exports = handlerRegistry;
