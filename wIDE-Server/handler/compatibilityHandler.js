var caniuse = require("../sources/caniuse/CanIUseLookup");
var https = require('https');
var http = require('http');
var htmlParser = require("htmlparser2");

var compatibilityHandler = {
    getBrowserVersions: function(callback) {
        var page = "";
        compatibilityHandler._loadBrowserPage(page, callback);
    },

    _updateBrowserVersions: function() {

    },

    _loadBrowserPage: function (page, callback) {
        console.log("compatibilityHandler: load browser versions.");
        var options = {
            host: 'caniuse.com',
            path: '/usage-table',
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        var httpreq = http.request(options, function (response) {
            response.setEncoding('utf8');

            // ERROR HANDLING
            response.on('error', function (error) {
                console.log('compatibilityHandler: Error while receiving caniuse response.');
                console.log(error);
            });

            // DATA RECEIVING
            response.on('data', function (chunk) {
                page += chunk;
            });

            // FULL DATA RECEIVED: DO SEARCH.
            response.on('end', function () {
                compatibilityHandler._parsePage(page, callback);
            });
        });

        httpreq.end();
    },

    _parsePage: function (page, callback) {


        var browsers = [];
        var currentBrowser = {};
        var currentVersion = {};

        var inContainer = false;
        var inList = false;
        var inCell = false;
        var inTitle = false;
        var inVersion = false;
        var inPercentage = false;

        // PARSE CANIUSE RESPONSE.
        var parser = new htmlParser.Parser({

                // PARSE BROWSER LIST
                onopentag: function (name, attribs) {
                    if (name === "div") {
                        if (attribs.class !== undefined
                            && attribs.class.indexOf("support-container") === 0) {

                            inContainer = true;
                        }

                        else if (inContainer
                            &&attribs.class !== undefined
                            && attribs.class.indexOf("support-list") === 0) {

                            inList = true;
                        }
                    }

                    else if (name === "li") {
                        if (inList
                            && attribs.class !== undefined
                            && attribs.class.indexOf("stat-cell") === 0) {

                            inCell = true;
                        }
                    }

                    else if (name === "h4") {
                        if (inList) {
                            inTitle = true;
                        }
                    }

                    else if (name === "b") {
                        if (inCell) {
                            inVersion = true;
                        }
                    }

                    else if (name === "span") {
                        if (inCell) {
                            inPercentage = true;
                        }
                    }
                },
                ontext: function (text) {
                    if (inTitle) {
                        console.log("Browser name: " + text);
                        currentBrowser.name = text;

                    } else if (inVersion) {
                        console.log("Browser version: " + text.replace(":", "").replace(" ", ""));
                        currentVersion.version = text.replace(":", "").replace(" ", "");

                    } else if (inPercentage) {
                        console.log("Browser percentage: " + text.replace("%", ""));
                        currentVersion.usage = text.replace("%", "");

                    }
                },
                onclosetag: function (tagname) {
                    if (tagname === "div") {
                        if (inList) {
                            inList = false;

                            if (currentBrowser !== {}) {
                                browsers.push(currentBrowser);
                                currentBrowser = {};
                            }

                        } else if (inContainer) {
                            inContainer = false;
                        }
                    }

                    else if (tagname === "li") {
                        if (inCell) {
                            inCell = false;

                            if (currentVersion !== {}) {
                                if (currentBrowser.versions === undefined) {
                                    currentBrowser.versions = [];
                                }

                                currentBrowser.versions.push(currentVersion);
                                currentVersion = {};
                            }
                        }
                    }

                    else if (tagname === "h4") {
                        if (inTitle) {
                            inTitle = false;
                        }
                    }

                    else if (tagname === "b") {
                        if (inVersion) {
                            inVersion = false;
                        }
                    }

                    else if (tagname === "span") {
                        if (inPercentage) {
                            inPercentage = false;
                        }
                    }
                }
            },
            {
                decodeEntities: true
            });
        parser.write(page);
        parser.end();

        if (callback !== undefined) {
            callback(browsers);
        }
    },
}

module.exports = compatibilityHandler;
