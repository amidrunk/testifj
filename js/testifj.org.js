var Navigation = (function($) {
    var getHash = function() {
        var hash = window.location.hash;

        if (hash && hash.charAt(0) == '#') {
            hash = hash.substring(1);
        }

        return hash;
    };

    var hash = getHash();
    var handlers = [];

    function dispatch() {
        for (var i = 0; i < handlers.length; i++) {
            if (handlers[i].pattern.matches(hash)) {
                handlers[i].callback();
            }
        }
    }

    window.setInterval(function() {
        var currentHash = getHash();

        if (currentHash != hash) {
            hash = currentHash;
            dispatch();
        }
    }, 100);

    return {
        "on": function(pattern) {
            return {
                "then": function(callback) {
                    handlers.push({
                        "pattern": pattern,
                        "callback": callback
                    });
                }
            }
        },

        "current": function() {
            return hash;
        }
    }
})(jQuery);

function actionThat(matcher) {
    return {
        "matches": function(action) {
            return matcher(action);
        }
    }
}

function isEqualTo(string) {
    return function(action) {
        return string == action;
    }
}