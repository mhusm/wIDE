var handlerRegistry = require("./handlerRegistry");

var queryHandler = {
    handle: function (lang, type, key, value, children, callback) {

        var result = {};
        result.lang = lang;
        result.type = type;
        result.key = key;
        result.value = value;
        result.children = [];
        result.documentation = {};

        // api handlers
        for (registryEntry in handlerRegistry.apiHandlers) {
            result.documentation[registryEntry] = handlerRegistry.apiHandlers[registryEntry].query(result, lang, type, key);
        }

        // parse handlers
        for (registryEntry in handlerRegistry.parseHandlers) {
            handlerRegistry.parseHandlers[registryEntry].query(result, lang, type, key, value, children, callback);
         }

        return result;
    }
}

module.exports = queryHandler;
