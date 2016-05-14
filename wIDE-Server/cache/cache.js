var queryHandler = require("../handler/queryHandler");
var suggestionHandler = require("../handler/suggestionHandler");

var mysql = require("mysql");
var sqlAccess = {
    host: 'localhost',
    user: 'wIDE-cache',
    password: 'P99~mSZ~Dx:q9nN',
    database: 'wIDE'
};

var cache = {
    lookupDocumentation: function (lang, type, key, value, parent, children, callback) {
        cache._selectEntryFromCache(lang, type, key, parent, function (err, rows, fields) {
            if (!err) {
                if (rows.length === 1) {
                    // Cached result found -> send as response
                    console.info("cache: Retreiving data from cache [key] " + key + " [type] " + type + " [parent] " + parent + " [lang] " + lang);
                    var result = {};
                    result.lang = lang;
                    result.type = type;
                    result.key = key;
                    result.value = value;
                    result.children = [];
                    //result.compatibility = rows[0].compatibility;
                    result.documentation = rows[0].documentation;

                    // are the children cached?
                    if (children.length > 0) {
                        cache._selectChildrenFromCache(lang, type, key, children, function (err, rows, fields) {
                            if (!err) {
                                if (rows.length > 0) {
                                    for (row in rows) {
                                        var child = rows[row];
                                        child.status = 1;
                                        result.children.push(child);
                                    }
                                }
                            } else {
                                // Error while querying cache -> show error & wait for lookup
                                console.error('cache: Error while querying children.');
                                console.error(err);
                            }

                            if (callback !== undefined) {
                                callback(result);
                            }
                        });
                    } else  if (callback !== undefined) {
                        callback(result);
                    }

                    // still refresh cache
                    queryHandler.handle(lang, type, key, value, children, cache.refreshDocumentationCache);

                    return result;

                } else if (rows.length === 0) {
                    // No cached result found -> wait for lookup
                    console.info("cache: No cached data for [key] " + key + " [type] " + type + " [parent] " + parent + " [lang] " + lang);
                    queryHandler.handle(lang, type, key, value, children, function (response, callback) {
                        cache.refreshDocumentationCache(response, callback);
                        return response;
                    });

                } else {
                    // Multiple rows found -> show error & wait for lookup
                    console.error("cache: Multiple entries for same key.");
                    return queryHandler.handle(lang, type, key, value, children, callback);

                }
            } else {
                // Error while querying cache -> show error & wait for lookup
                console.error('cache: Error while querying.');
                console.error(err);
                return queryHandler.handle(lang, type, key, value, children, callback);
            }
        });

    },

    refreshDocumentationCache: function (response, callback) {
        cache._selectEntryFromCache(response.lang, response.type, response.key, response.parent, function (err, rows, fields) {
            if (!err) {
                if (rows.length === 1) {
                    // Update
                    cache._updateEntryInCache(response);

                } else if (rows.length === 0) {
                    // Insert
                    cache._insertEntryToCache(response);

                } else {
                    // Fault
                    console.error("cache: Multiple entries for same key.");
                    queryHandler.handle(response.lang, response.type, response.key, response.value, response.children, callback);

                }
            } else {
                // Error while querying cache -> show error & wait for lookup
                console.error('cache: Error while performing Query.');
                console.error(err);
                queryHandler.handle(response.lang, response.type, response.key, response.value, response.children, callback);
            }
        });
    },

    _selectEntryFromCache: function (lang, type, key, parent, callback) {
        //TODO: handle db faults (not available or others)
        console.log("cache: Select entry for [key] " + key + " [type] " + type + " [parent] " + parent + " [lang] " + lang);
        // create connection to cache-db
        var connection = mysql.createConnection(sqlAccess);

        // allow :key syntax
        connection.config.queryFormat = function (query, values) {
            if (!values) return query;
            return query.replace(/\:(\w+)/g, function (txt, key) {
                if (values.hasOwnProperty(key)) {
                    return this.escape(values[key]);
                }
                return txt;
            }.bind(this));
        };

        connection.connect();

        if (parent === undefined || parent === null) {
            connection.query("" +
                "SELECT `key`, " +
                "       `lang`, " +
                "       `type`, " +
                "       `compatibility`, " +
                "       `documentation`, " +
                "       `parent` " +
                "FROM   cache " +
                "WHERE  `key` = :key " +
                "       AND `lang` = :lang " +
                "       AND `type` = :type " +
                "       AND `parent` IS NULL ",
                {
                    "key": key,
                    "lang": lang,
                    "type": type
                },

                callback);
        } else {
            connection.query("" +
                "SELECT `key`, " +
                "       `lang`, " +
                "       `type`, " +
                "       `compatibility`, " +
                "       `documentation`, " +
                "       `parent` " +
                "FROM   cache " +
                "WHERE  `key` = :key " +
                "       AND `lang` = :lang " +
                "       AND `type` = :type " +
                "       AND `parent` = :parent ",
                {
                    "key": key,
                    "lang": lang,
                    "type": type,
                    "parent": parent
                },

                callback);

        }

        connection.end();
    },

    _selectChildrenFromCache: function (lang, type, key, children, callback) {
        //TODO: handle db faults (not available or others)
        console.log("cache: Select children for [key] " + key + " [type] " + type + " [lang] " + lang);
        // create connection to cache-db
        var connection = mysql.createConnection(sqlAccess);

        // allow :key syntax
        connection.config.queryFormat = function (query, values) {
            if (!values) return query;
            return query.replace(/\:(\w+)/g, function (txt, key) {
                if (values.hasOwnProperty(key)) {
                    return this.escape(values[key]);
                }
                return txt;
            }.bind(this));
        };

        connection.connect();

            connection.query("" +
                "SELECT `key`, " +
                "       `lang`, " +
                "       `type`, " +
                "       `compatibility`, " +
                "       `documentation`, " +
                "       `parent` " +
                "FROM   cache " +
                "WHERE  `lang` = :lang " +
                "       AND `parent` = :key " +
                "       AND `key` IN (" + Object.keys(children).join(", ") + ") ",
                {
                    "key": key,
                    "lang": lang
                },

                callback);

        connection.end();
    },

    _searchSuggestionsInCache: function (lang, type, key, parent, callback) {
        console.log("cache: Search suggestions for [key] " + key + " [type] " + type + " [parent] " + parent + " [lang] " + lang);
        // create connection to cache-db
        var connection = mysql.createConnection(sqlAccess);

        // allow :key syntax
        connection.config.queryFormat = function (query, values) {
            if (!values) return query;
            return query.replace(/\:(\w+)/g, function (txt, key) {
                if (values.hasOwnProperty(key)) {
                    return this.escape(values[key]);
                }
                return txt;
            }.bind(this));
        };

        connection.connect();

        if (parent === undefined || parent === null) {
            connection.query("" +
                "SELECT `key`, " +
                "       `lang`, " +
                "       `type`, " +
                "       `compatibility`, " +
                "       `documentation`, " +
                "       `parent` " +
                "FROM   cache " +
                "WHERE  UPPER(`key`) LIKE UPPER(:key) " +
                "       AND `lang` = :lang " +
                "       AND `type` = :type " +
                "       AND `parent` IS NULL " +
                "ORDER BY `key` ASC ",
                {
                    "key": key + "%",
                    "lang": lang,
                    "type": type
                },

                callback);
        } else {
            connection.query("" +
                "SELECT `key`, " +
                "       `lang`, " +
                "       `type`, " +
                "       `compatibility`, " +
                "       `documentation`, " +
                "       `parent` " +
                "FROM   cache " +
                "WHERE  UPPER(`key`) LIKE UPPER(:key) " +
                "       AND `lang` = :lang " +
                "       AND `type` = :type " +
                "       AND `parent` = :parent " +
                "ORDER BY `key` ASC ",
                {
                    "key": key + "%",
                    "lang": lang,
                    "type": type,
                    "parent": parent
                },

                callback);

        }

        connection.end();
    },

    _updateEntryInCache: function (entry) {
        //TODO: handle db faults (not available or others)
        // create connection to cache-db
        var connection = mysql.createConnection(sqlAccess);

        // allow :key syntax
        connection.config.queryFormat = function (query, values) {
            if (!values) return query;
            return query.replace(/\:(\w+)/g, function (txt, key) {
                if (values.hasOwnProperty(key)) {
                    return this.escape(values[key]);
                }
                return txt;
            }.bind(this));
        };

        connection.connect();

        var flatEntry = entry;
        flatEntry.documentation = JSON.stringify(entry.documentation);
        flatEntry.compatibility = JSON.stringify(entry.compatibility);

        if (flatEntry.parent === undefined || flatEntry.parent === null) {
            // query for existing cached results
            connection.query("" +
                "UPDATE cache SET " +
                "   `key` = :key, " +
                "   `lang`= :lang, " +
                "   `type` = type, " +
                    // "   `compatibility` = :compatibility, " +
                "   `documentation` = :documentation, " +
                "   `parent` = NULL " +
                "WHERE  `key` = :key " +
                "       AND `lang` = :lang " +
                "       AND `type` = :type " +
                "       AND `parent` IS NULL ",
                flatEntry,
                function (err, rows, fields) {
                    if (!err) {
                        console.log("cache: Update successful for [key] " + entry.key + " [type] " + entry.type + " [parent] NULL [lang] " + entry.lang);
                    } else {
                        // Error while querying cache -> show error & wait for lookup
                        console.error("cache: Update failed for [key] " + entry.key + " [type] " + entry.type + " [parent] NULL [lang] " + entry.lang);
                        console.error(err);
                    }
                });
        } else {
            // query for existing cached results
            connection.query("" +
                "UPDATE cache SET " +
                "   `key` = :key, " +
                "   `lang`= :lang, " +
                "   `type` = type, " +
                    // "   `compatibility` = :compatibility, " +
                "   `documentation` = :documentation, " +
                "   `parent` = :parent " +
                "WHERE  `key` = :key " +
                "       AND `lang` = :lang " +
                "       AND `type` = :type " +
                "       AND `parent` = :parent ",
                flatEntry,
                function (err, rows, fields) {
                    if (!err) {
                        console.log("cache: Update successful for [key] " + entry.key + " [type] " + entry.type + " [parent] " + entry.parent + " [lang] " + entry.lang);
                    } else {
                        // Error while querying cache -> show error & wait for lookup
                        console.error("cache: Update failed for [key] " + entry.key + " [type] " + entry.type + " [parent] " + entry.parent + " [lang] " + entry.lang);
                        console.error(err);
                    }
                });
        }



        connection.end();
    },

    _insertEntryToCache: function (entry) {
        //TODO: handle db faults (not available or others)
        // create connection to cache-db
        var connection = mysql.createConnection(sqlAccess);

        // allow :key syntax
        connection.config.queryFormat = function (query, values) {
            if (!values) return query;
            return query.replace(/\:(\w+)/g, function (txt, key) {
                if (values.hasOwnProperty(key)) {
                    return this.escape(values[key]);
                }
                return txt;
            }.bind(this));
        };

        connection.connect();

        var flatEntry = entry;
        flatEntry.documentation = JSON.stringify(entry.documentation);
        flatEntry.compatibility = JSON.stringify(entry.compatibility);

        if (flatEntry.parent === undefined || flatEntry.parent === null) {
            // query for existing cached results
            connection.query("" +
                "INSERT INTO cache ( " +
                "   `key`, " +
                "   `lang`, " +
                "   `type`, " +
                    //"   `compatibility`, " +
                "    `documentation`, " +
                "    `parent`) " +
                "VALUES (" +
                "   :key, " +
                "   :lang, " +
                "   :type, " +
                    //"   :compatibility, " +
                "   :documentation, " +
                "   NULL) ",
                flatEntry,
                function (err, rows, fields) {
                    if (!err) {
                        console.log("cache: Insert successful for [key] " + entry.key + " [type] " + entry.type + " [parent] NULL [lang] " + entry.lang);
                    } else {
                        // Error while querying cache -> show error & wait for lookup
                        console.error("cache: Insert failed for [key] " + entry.key + " [type] " + entry.type + " [parent] NULL [lang] " + entry.lang);
                        console.error(err);
                    }
                });

        } else {
            // query for existing cached results
            connection.query("" +
                "INSERT INTO cache ( " +
                "   `key`, " +
                "   `lang`, " +
                "   `type`, " +
                    //"   `compatibility`, " +
                "    `documentation`, " +
                "    `parent`) " +
                "VALUES (" +
                "   :key, " +
                "   :lang, " +
                "   :type, " +
                    //"   :compatibility, " +
                "   :documentation, " +
                "   :parent) ",
                flatEntry,
                function (err, rows, fields) {
                    if (!err) {
                        console.log("cache: Insert successful for [key] " + entry.key + " [type] " + entry.type + " [parent] " + entry.parent + " [lang] " + entry.lang);
                    } else {
                        // Error while querying cache -> show error & wait for lookup
                        console.error("cache: Insert failed for [key] " + entry.key + " [type] " + entry.type + " [parent] " + entry.parent + " [lang] " + entry.lang);
                        console.error(err);
                    }
                });
        }


        connection.end();
    },

    suggest: function (lang, type, key, value, children, callback) {
        console.log("cache: load suggestions for [key] " + key + " [type] " + type + " [parent] " + value + " [lang] " + lang);
        cache._searchSuggestionsInCache(lang, type, key, value, function (err, rows, fields) {
            if (!err) {
                var result = {};
                result.lang = lang;
                result.type = type;
                result.key = key;
                result.value = value;
                result.children = [];

                if (rows.length > 0) {

                    for (var row in rows) {
                        var subResult = {};
                        subResult.lang = lang;
                        subResult.type = type;
                        subResult.key = rows[row].key;
                        subResult.value = value;
                        subResult.children = [];
                        subResult.documentation = rows[row].documentation;
                        result.children.push(subResult);

                    }
                    if (callback !== undefined) {
                        callback(result);
                    }

                } else {
                    // No cached result found -> wait for lookup
                    console.info("cache: No suggestions for [key] " + key + " [type] " + type + " [parent] " + value + " [lang] " + lang);

                    if (callback !== undefined) {
                        callback(result);
                    }
                }
            } else {
                // Error while querying cache -> show error & wait for lookup
                console.error('cache: Error while looking up Suggestions.');
                console.error(err);
                if (callback !== undefined) {
                    callback({
                        "type": "error",
                        "message": "cache: Error while looking up suggestions."
                    });
                }
            }
        });
    }
}

module.exports = cache;
