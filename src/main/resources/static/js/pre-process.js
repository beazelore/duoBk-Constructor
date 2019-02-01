
$(document).ready(function() {
    var taskId = findGetParameter("id");
    console.log("fromget", taskId);
    sessionStorage.removeItem('indexesStart');
    sessionStorage.removeItem('indexesEnd');
    getEntriesDataAjax(taskId);
    /*var data = sessionStorage.getItem('data');
    var book1 = data.split('!separator!')[0];
    var book2 = data.split('!separator!')[1];
    $('#book1_list').html(book1);
    $('#book2_list').html(book2);*/

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
        document.getElementById("startProcess").innerHTML="Submit";
        document.getElementById("hint").innerHTML="Now please choose last matching sentences and press SUBMIT";
        console.log('indexesstart set');
    }
    else {
        var startIndexes = JSON.parse(sessionStorage.getItem('indexesStart'));
        console.log('startindexes:');
        console.log(startIndexes);
        var indexes = {start1: startIndexes.indexes1, start2: startIndexes.indexes2, end1: data.indexes1, end2: data.indexes2, taskId: taskId};
        $.ajax({
            type: "POST",
            url: "/tasks/preProcess/do",
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

function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    var items = location.search.substr(1).split("&");
    for (var index = 0; index < items.length; index++) {
        tmp = items[index].split("=");
        if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    }
    return result;
}
function getEntriesDataAjax(taskId){
console.log("before request: ", taskId);
var result;
  $.ajax({
            type: "POST",
            url: "/tasks/preProcess/getEntries",
            contentType: "text/plain",
            data: taskId,
            success: function(data, textStatus, jqXHR) {
                console.log("data in ajax", data);
                var book1 = data.split('!separator!')[0];
                var book2 = data.split('!separator!')[1];
                $('#book1_list').html(book1);
                $('#book2_list').html(book2);
                checkUnprocessedEmpty(taskId);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                    alert("error");
                    console.log(jqXHR);
            }
        });
}
function checkUnprocessedEmpty(taskId){
 $.ajax({
            type: "POST",
            data: taskId,
            contentType: "text/plain",
            url: "/tasks/preProcess/checkUnprocessed",
            success: function(data, textStatus, jqXHR) {
                if(data == false){
                    var r = confirm("This task is already in PROCESS stage.\n If you do pre-process again, all the process progress will be gone");
                    if (r == false) {
                        window.location.href="/tasks";
                    }
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                    alert("error");
                    console.log(jqXHR);
            }
        });
}
