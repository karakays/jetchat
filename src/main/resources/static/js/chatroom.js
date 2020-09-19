function connect() {
	sessionId = sessionStorage.getItem('jetchat:ws-session-id');

    if(sessionId != null) {
        console.log("Using existing websocket session id: " + sessionId);
        socket = new SockJS('/ws', [], {
           sessionId: () => { return sessionId; }
        });
        stompClient = Stomp.over(socket);
        stompClient.connect({ 'chatRoomId' : 'n/a' }, stompSuccess, stompFailure);
    } else {
        wsSessionId = Math.random().toString(36).substring(2,10);
	    sessionStorage.setItem('jetchat:ws-session-id', wsSessionId);
        console.log("Generated new websocket session id: " + wsSessionId);
        socket = new SockJS('/ws', [], {
           sessionId: () => { return wsSessionId; }
        });
        stompClient = Stomp.over(socket);
        stompClient.connect({ 'chatRoomId' : 'n/a' }, stompSuccess, stompFailure);
    }
}

function stompSuccess(frame) {
    enableInputMessage();
    successMessage("Welcome back!");
}

function stompFailure(error) {
    errorMessage("Oh no, something went wrong :(");
    disableInputMessage();
    setTimeout(connect, 10000);
}

function joinChannel(channelId) {
	console.log('stomp ', stompClient);
	console.log('stomp-json ', JSON.stringify(stompClient));

    if(!hasChannel(channelId)) {
        // get current users/message history from subscription response
        stompClient.subscribe('/chatroom/' + channelId + '/connected.users', function(response) {
            updateConnectedUsers(response, channelId);
        });
        stompClient.subscribe('/chatroom/' + channelId + '/old.messages', oldMessages);

        // subscribe to topics
        stompClient.subscribe('/topic/' + channelId + '.public.messages', publicMessages);
        stompClient.subscribe('/user/queue/' + channelId + '.private.messages', privateMessages);
        stompClient.subscribe('/topic/' + channelId + '.connected.users', function(response) {
            updateConnectedUsers(response, channelId);
        });

        addChannel(channelId);
    }
    setCurrentChannel(channelId);
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    window.location.href = "/login";
}

function updateConnectedUsers(response, channelId) {
    var connectedUsers = JSON.parse(response.body);

    var $users = $("#users-panel-body-" + channelId).html("");
    var $userListGroup = $("<ul class='list-group list-group-flush'/>");
    $users.append($userListGroup);

    $.each(connectedUsers, function(index, connectedUser) {
        var $elem = $("<li class='list-group-item list-group-item-action'/>");
        $elem.text(connectedUser.username);
        $elem.click(function() {
            spanSendTo.text($(this).text());
        });
        $userListGroup.append($elem);
    });
}

function oldMessages(response) {
    var instantMessages = JSON.parse(response.body);

    $.each(instantMessages, function(index, instantMessage) {
        if (instantMessage.public == true) {
            appendPublicMessage(instantMessage);
        } else {
            appendPrivateMessage(instantMessage);
        }
    });

    scrollDownMessagesPanel();
}

function publicMessages(message) {
    var instantMessage = JSON.parse(message.body);
    appendPublicMessage(instantMessage);
    $("#channels").find("#" + instantMessage.chatRoomId).children("td").css("font-weight","bold");
    scrollDownMessagesPanel();
}

function appendPublicMessage(instantMessage) {
    messages = $("#messages-" + instantMessage.chatRoomId);
    console.log('found message-panel: ' + messages);
    if (instantMessage.fromUser == "admin") {
        var $message = $("<p class='alert alert-warning'><strong>" + instantMessage.fromUser + "</strong> <small> "
            + new Date(instantMessage.date).toLocaleTimeString() + "</small> </br>" + instantMessage.text + "</p>");
        messages.append($message);
    } else {
        var $message = $("<p class='alert alert-primary'><strong>" + instantMessage.fromUser + "</strong> <small> "
            + new Date(instantMessage.date).toLocaleTimeString() + "</small> </br>" + instantMessage.text + "</p>");
        messages.append($message);
    }
}

function privateMessages(message) {
    var instantMessage = JSON.parse(message.body);
    appendPrivateMessage(instantMessage);
    scrollDownMessagesPanel();
}

function appendPrivateMessage(instantMessage) {
    newMessages
    .append('<p class="alert alert-info">[private] ' +
            '<strong>' + instantMessage.fromUser +
            ' <span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span> ' +
            instantMessage.toUser + '</strong>: ' +
            instantMessage.text + '</p>');
}

function sendMessage() {
    var instantMessage;

    if (inputMessageIsEmpty()) {
        inputMessage.focus();
        return;
    }

    if (spanSendTo.text() == "public") {
        instantMessage = {
            'text' : inputMessage.val()
        }
    } else {
        instantMessage = {
            'text' : inputMessage.val(),
            'toUser' : spanSendTo.text()
        }
    }
    channel = sessionStorage.getItem('jetchat:currentChannel');
    stompClient.send("/chatroom/" + channel + "/send.message", {}, JSON.stringify(instantMessage));
    inputMessage.val("").focus();
}

function inputMessageIsEmpty() {
    return inputMessage.val() == "";
}

function sendTo(e) {
    spanSendTo.text(e.toElement.textContent);
    inputMessage.focus();
}

function checkEnter(e) {
    var key = e.which;
    if(key == 13) {
       btnSend.click();
       return false;
    }
}

function scrollDownMessagesPanel() {
    var newMessages = $("#newMessages");
    newMessages.animate({"scrollTop": newMessages[0].scrollHeight}, "fast");
}

function enableInputMessage() {
    var inputMessage = $("#message");
    inputMessage.prop("disabled", false);
}

function disableInputMessage() {
    inputMessage.prop("disabled", true);
}

function successMessage(msg){
    noty({
        text: msg,
        layout: 'top',
        type: 'success',
        timeout: 5000
    });
}

function errorMessage(msg){
    noty({
        text: msg,
        layout: 'top',
        type: 'error',
        timeout: 5000
    });
}
