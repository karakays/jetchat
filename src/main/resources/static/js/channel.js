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
                console.log('Clicked on channel ' + channel.id + '/' + channel.name);
                chatRoomId = channel.id;
                connect(channel.id);
                bindConnectedUsers();
                scrollDownMessagesPanel(newMessages);
            });
        });
    });
}

