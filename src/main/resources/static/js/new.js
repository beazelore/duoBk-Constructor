$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        if (settings.type == 'POST' || settings.type == 'PUT' || settings.type == 'DELETE') {
            function getCookie(name) {
            console.log("in cookies");
                var cookieValue = null;
                if (document.cookie && document.cookie != '') {
                    var cookies = document.cookie.split(';');
                    for (var i = 0; i < cookies.length; i++) {
                        var cookie = jQuery.trim(cookies[i]);
                        // Does this cookie string begin with the name we want?
                        if (cookie.substring(0, name.length + 1) == (name + '=')) {
                            cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                            break;
                        }
                    }
                }
                console.log("returning cookie");
                console.log(cookieValue);
                return cookieValue;
            }
            if (!(/^http:.*/.test(settings.url) || /^https:.*/.test(settings.url))) {
                // Only send the token to relative URLs i.e. locally.
                xhr.setRequestHeader("X-XSRF-TOKEN", getCookie('XSRF-TOKEN'));
            }
        }
    }
});


$(document).ready(function() {
    sessionStorage.removeItem('indexesStart');
    sessionStorage.removeItem('indexesEnd');
    var data = sessionStorage.getItem('data');
    var book1 = data.split('!separator!')[0];
    var book2 = data.split('!separator!')[1];
    $('#book1_list').html(book1);
    $('#book2_list').html(book2);

    $('#book1_list').on('change', function(e){
    var value = $('#book1_list option:selected').text();
    $('#active1').html(value);
    });

    $('#book2_list').on('change', function(e){
    var value = $('#book2_list option:selected').text();
    $('#active2').html(value);
    });

    $('#startProcess').on('click', function(){
    var ind1 = $('#book1_list').val();
    var ind2 = $('#book2_list').val();
    var data = {indexes1 : ind1, indexes2 : ind2};
    if(sessionStorage.getItem('indexesStart') === null){
        sessionStorage.setItem('indexesStart', JSON.stringify(data));
        console.log('indexesstart set');
    }
    else {
        var startIndexes = JSON.parse(sessionStorage.getItem('indexesStart'));
        console.log('startindexes:');
        console.log(startIndexes);
        var indexes = {start1: startIndexes.indexes1, start2: startIndexes.indexes2, end1: data.indexes1, end2: data.indexes2};
        $.ajax({
            type: "POST",
            url: "/tasks/create/new/process",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(indexes),
            success: function(data, textStatus, jqXHR) {
                alert("success");
                if(jqXHR.statusText != "nocontent"){
                    console.log(data);
                    //sessionStorage.setItem('AStar', data.content);
                    //window.location.href = "/correcting"
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                if(jqXHR.status == 428){
                    alert("Books are not loaded");
                    //window.location.href = "/greetings";
                }
                else{
                    alert("error");
                    console.log(jqXHR);
                }
            }
        });
    }


    });
});
