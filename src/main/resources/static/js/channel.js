function loadChannels() {
    $.get("/chatrooms", function(data) {
        var $tbody = $("#channels").html("");
        $.each(data, function(index, channel) {
            console.log("channel: " + channel.name);
            var $tr = $("<tr />");
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
	            if((channels = JSON.parse(sessionStorage.getItem('jetchat:channels'))) == null) {
	                channels = [];
                    var $messages = $("#newMessages").html("");

                    $messages.append("<div id='messages-" + channel.id + "'/>");
	            }
	            console.log('currently subscribed channels: ' + channels);
	            if(!channels.includes(channel.id)) {
                    joinChannel(channel.id);
	            }
                scrollDownMessagesPanel(newMessages);
            });
        });
    });
}

