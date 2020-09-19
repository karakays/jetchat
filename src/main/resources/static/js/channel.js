function loadChannels() {
    $.get("/chatrooms", function(data) {
        var $list = $(".channel-list-group").html("");
        $.each(data, function(index, channel) {
            var $elem = $("<li class='list-group-item list-group-item-action'/>");
            $elem.attr("id", channel.id);
            $elem.text(channel.name);
            $elem.click(function() {
                $(".list-group-item").removeClass("active");
                $(this).addClass("active");
                updateChannelView(channel.id);
                joinChannel(channel.id);
                scrollDownMessagesPanel();
            });
            $list.append($elem);
            setTimeout(function(){$(".channel-list-group").children("li").trigger("click"); }, 2000);
        });
    });
}

function updateChannelView(channel) {
    $("#newMessages").children('.messages').hide();
    $("#users-panel-body-wrapper").children(".list-group").hide()
    if(!hasChannel(channel)) {
        newChannelDiv = $("<div id='messages-" + channel + "' class='messages'/>");
        $("#newMessages").append(newChannelDiv);
        var $userList = $("<div/>");
        $userList.addClass("list-group");
        $userList.attr("id", "users-panel-body-" + channel);
        var $userListGroup = $("<ul/>");
        $userList.append($userListGroup);
        $("#users-panel-body-wrapper").append($userList);
    } else {
        newChannelDiv = $("#messages-" + channel);
        newChannelDiv.show();
        $("#users-panel-body-" + channel).show();
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
