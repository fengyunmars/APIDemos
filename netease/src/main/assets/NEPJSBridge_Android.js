
;(function() {

	if (window.NEPJSBridge) {return}

	var messageHandlers = {};
	var responseCallbacks = {};
	var sendMessageQueue = [];
	var NEP_PROTOCOL_SCHEME = 'epaysdk';
	var NEP_HAS_MESSAGE = '__NEPJSBridge_HAS_MESSAGE__';

	var uniqueId = 0;


	var base64encodechars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	function base64encode(str) {
		if (str === undefined) {
			return str;
		}

		var out, i, len;
		var c1, c2, c3;
		len = str.length;
		i = 0;
		out = "";
		while (i < len) {
			c1 = str.charCodeAt(i++) & 0xff;
			if (i == len) {
				out += base64encodechars.charAt(c1 >> 2);
				out += base64encodechars.charAt((c1 & 0x3) << 4);
				out += "==";
				break;
			}
			c2 = str.charCodeAt(i++);
			if (i == len) {
				out += base64encodechars.charAt(c1 >> 2);
				out += base64encodechars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xf0) >> 4));
				out += base64encodechars.charAt((c2 & 0xf) << 2);
				out += "=";
				break;
			}
			c3 = str.charCodeAt(i++);
			out += base64encodechars.charAt(c1 >> 2);
			out += base64encodechars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xf0) >> 4));
			out += base64encodechars.charAt(((c2 & 0xf) << 2) | ((c3 & 0xc0) >> 6));
			out += base64encodechars.charAt(c3 & 0x3f);
		}
		return out;
	}

	function UTF8Encode(string) {
		string = string.replace(/\r\n/g, "\n");
			var utftext = "";

			for (var n = 0; n < string.length; n++) {

				var c = string.charCodeAt(n);

				if (c < 128) {
					utftext += String.fromCharCode(c);
				} else if ((c > 127) && (c < 2048)) {
					utftext += String.fromCharCode((c >> 6) | 192);
					utftext += String.fromCharCode((c & 63) | 128);
				} else {
					utftext += String.fromCharCode((c >> 12) | 224);
					utftext += String.fromCharCode(((c >> 6) & 63) | 128);
					utftext += String.fromCharCode((c & 63) | 128);
				}

			}

			return utftext;
	}

	function UTF8Decode(utftext) {
		var string = "";
			var i = 0;
			var c = c1 = c2 = 0;

			while (i < utftext.length) {

				c = utftext.charCodeAt(i);

				if (c < 128) {
					string += String.fromCharCode(c);
					i++;
				} else if ((c > 191) && (c < 224)) {
					c2 = utftext.charCodeAt(i + 1);
					string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
					i += 2;
				} else {
					c2 = utftext.charCodeAt(i + 1);
					c3 = utftext.charCodeAt(i + 2);
					string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
					i += 3;
				}

			}

			return string;
	}


	function registerDefaultHandler(messageHandler) {
		if (NEPJSBridge.messageHandler) {throw new Error('NEPJSBridge.init called twice')}

			NEPJSBridge.messageHandler = messageHandler;
	}

	function registerHandler(handlerName, handler) {
		messageHandlers[handlerName] = handler;
	}

	function sendData(message, responseCallback) {
		_sendData({message:message}, responseCallback);
	}

	function _sendData(message, responseCallback) {
		if (responseCallback) {
			var callbackId = 'callbackId_'+(uniqueId++)+'_'+new Date().getTime();
			responseCallbacks[callbackId] = responseCallback;
			message['callbackId'] = callbackId;
		}
		sendMessageQueue.push(message);

		fetchQueue();
	}

	function fetchQueue() {
		var messageQueueString = JSON.stringify(sendMessageQueue);
		sendMessageQueue = [];

		var _temp = prompt(NEP_PROTOCOL_SCHEME + '://' + NEP_HAS_MESSAGE + '/' + base64encode(UTF8Encode(messageQueueString)),'');
	}

	function handleMessageFromNative(messageJSON) {
		setTimeout(function _timeoutHandleMessageFromNative() {
			var message = JSON.parse(messageJSON);
			var messageHandler;
			var responseCallback;
			if (message.responseId) {
				responseCallback = responseCallbacks[message.responseId];
				if (!responseCallback) {return;}
				responseCallback(message.message);
				delete responseCallbacks[message.responseId];
			}
			else {
				if (message.callbackId) {
					var callbackResponseId = message.callbackId;
					responseCallback = function(responseData) {
						_sendData({responseId : callbackResponseId, message:responseData});
					}
				}
				
				var handler = NEPJSBridge.messageHandler;

				if (message.handlerName) {
					handler = messageHandlers[message.handlerName];
				}

				try {
					handler(message.message, responseCallback);
				} catch(exception) {
					if (typeof console != 'undefined') {
						console.log("NEPJSBridge: WARNING: javascript handler threw.", message, exception);
					}
				}
			}

		})
	}

	window.NEPJSBridge = {
		registerDefaultHandler:registerDefaultHandler,
		sendData:sendData,
		registerHandler:registerHandler,
		fetchQueue:fetchQueue,
		handleMessageFromNative:handleMessageFromNative
	};

	var doc = document;
	var readyEvent = doc.createEvent('Events');
	readyEvent.initEvent('NEPJSBridgeReady');
	readyEvent.bridge = NEPJSBridge;
	doc.dispatchEvent(readyEvent);

})();