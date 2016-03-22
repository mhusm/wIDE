var https = require('https');
var htmlParser = require("htmlparser2");

var mdnHtml = {
    query: function (tag, attributes) {
        console.log("Send request to MDN: [tag] " + tag + " [attributes] " + attributes);

        var options = {
            host: 'developer.mozilla.org',
            path: '/en-US/docs/Web/HTML/Element/' + tag,
            method: 'GET',
            headers: {
                'Content-Length': 0
            }
        };

        var result;
        console.log(attributes);
        var presentAttributes = JSON.parse(attributes);

        var httpreq = https.request(options, function (response) {
            response.setEncoding('utf8');

            // ERROR HANDLING
            response.on('error', function(error) {
                console.log('Error while receiving MDN response.');
                console.log(error);
            });

            // DATA RECEIVING
            response.on('data', function (chunk) {
                result += chunk;
            });

            // FULL DATA RECEIVED: DO SEARCH.
            response.on('end', function() {

                var tagAttributes = {};
                var currentKey = undefined;
                var currentValue = "";
                var inDD = false;

                // PARSE MDN RESPONSE.
                var parser = new htmlParser.Parser({

                    // PARSE ATTRIBUTES AND EXPLANATIONS.
                    onopentag: function(name, attribs){
                        if(name === "strong" && attribs.id !== undefined && attribs.id.indexOf("attr-") === 0){
                            currentKey = attribs.id.substr(5);
                        } else if (name === "dd" && currentKey !== undefined) {
                            inDD = true;
                        } else if (currentKey !== undefined && inDD) {
                            currentValue += " <" + name;
                            for (attr in attribs) {
                                currentValue += ' ' + attr + '="' + attribs[attr] + '"';
                            }

                            currentValue += ">";
                        }
                    },
                    ontext: function(text){
                        if (currentKey !== undefined && inDD) {
                            currentValue += text;

                        }
                    },
                    onclosetag: function(tagname){
                        if(tagname === "dd"){
                            tagAttributes[currentKey] = currentValue;
                            console.log();
                            console.log(currentKey);
                            console.log(currentValue);
                            currentKey = undefined;
                            currentValue = "";
                            inDD = false;

                        } else if (currentKey !== undefined && inDD) {
                            currentValue += "</" + tagname + ">";
                        }
                    }
                }, {decodeEntities: true});
                parser.write(result);
                parser.end();


                for (attribute in presentAttributes) {
                    console.log(tagAttributes[attribute]);

                    if (tagAttributes[attribute] !== undefined) {
                        console.log("Matching attribute: " + attribute);
                    }
                }

                result = "finish";
            });
        });

        httpreq.end();

        while(result === undefined) {}

        return result;
    }
}

module.exports = mdnHtml;
