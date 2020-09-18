function loadChannels() {
    $.get("/chatrooms", function(data) {
        var $tbody = $("#channels").html("");
        $.each(data, function(index, channel) {
            console.log("channel: " + channel.name);
            var $tr = $("<tr id=" + channel.id + "/>");
            var $td = $('<td />', {
                "text" : channel.name
            });
            $td.appendTo($tr);
            $tr.appendTo($tbody);
            $tr.click(function() {
                $(this).addClass('selected').siblings().removeClass('selected');
                var value=$(this).find('td:first').html();
                //spanSendTo.text(value);
                $("#chatroom-banner").html(channel.name);

                updateChannelView(channel.id);
                joinChannel(channel.id);
                scrollDownMessagesPanel();
                $td.css("font-weight", "normal");
            });
            setTimeout(function() {
                $("#channels").children("tr").trigger("click");
            }, 2000);
        });
    });
}

function updateChannelView(channel) {
    $("#newMessages").children('.messages').hide();
    $("#users-panel-wrapper").children(".tables").hide()
    if(!hasChannel(channel)) {
        newChannelDiv = $("<div id='messages-" + channel + "' class='messages'/>");
        $("#newMessages").append(newChannelDiv);
        newUsersTable = $("<div id='users-panel-" + channel + "' class='tables'><table/></div>");
        $("#users-panel-wrapper").append(newUsersTable);
    } else {
        newChannelDiv = $("#messages-" + channel);
        newChannelDiv.show();
        newUsersTable = $("#users-panel-" + channel);
        newUsersTable.show();
    }
}

function addChannel(channel) {
    channels = getChannels();
    channels.push(channel);
	sessionStorage.setItem('jetchat:channels', JSON.stringify(channels));
}

function setCurrentChannel(channel) {
    sessionStorage.setItem('jetchat:currentChannel', channel);
}

function hasChannel(channel) {
    return getChannels().includes(channel);
}

function getChannels() {
	var channels = JSON.parse(sessionStorage.getItem('jetchat:channels'));
	return channels != null ? channels : [];
}

function getCurrentChannelId() {
    return sessionStorage.getItem('jetchat:currentChannel');
}
