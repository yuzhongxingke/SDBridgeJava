;(function(window) {
    console.log("bridge11111111111111");
    if (window.WebViewJavascriptBridge) {
        return;
    }
    window.WebViewJavascriptBridge = {
        registerHandler: registerHandler,
        callHandler: callHandler,
        handleMessageFromNative: handleMessageFromNative
    };
    var messageHandlers = {};
    var responseCallbacks = {};
    var uniqueId = 1;
    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }
    function callHandler(handlerName, data, responseCallback) {
        if (arguments.length === 2 && typeof data == 'function') {
            responseCallback = data;
            data = null;
        }
        doSend({ handlerName:handlerName, data:data }, responseCallback);
    }
    function doSend(message, responseCallback) {
        if (responseCallback) {
            var callbackId = 'cb_'+(uniqueId++)+'_'+new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
            message['callbackId'] = callbackId;
        }
        window.normalPipe.postMessage(JSON.stringify(message));
    }
    function handleMessageFromNative(messageJSON) {
        var message = JSON.parse(messageJSON);
        var responseCallback;
        if (message.responseId) {
            responseCallback = responseCallbacks[message.responseId];
            if (!responseCallback) {
                return;
            }
            responseCallback(message.responseData);
            delete responseCallbacks[message.responseId];
        } else {
            if (message.callbackId) {
                var callbackResponseId = message.callbackId;
                responseCallback = function(responseData) {
                    doSend({ handlerName:message.handlerName, responseId:callbackResponseId, responseData:responseData });
                };
            }
            var handler = messageHandlers[message.handlerName];
            if (!handler) {
                console.log("WebViewJavascriptBridge: WARNING: no handler for message from ObjC:", message);
            } else {
                handler(message.data, responseCallback);
            }
        }
    }
})(window);
