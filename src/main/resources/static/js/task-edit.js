$(document).ready(function(){
    var taskId = findGetParameter("id");
    getTaskInfoAjax(taskId);

    $("#submitTaskInfo").on('click',function(){
        event.preventDefault();
        submitFormData(taskId);
    });
    $("#deleteTask").on('click',function(){
        deleteTask(taskId);
    });
    $('#unprocessedText').highlightWithinTextarea({
       highlight:
       [
           {
               highlight: /(<dp|<\/dp>)/g,
               className: 'red'
           },
           {
               highlight: /(?<=<p1.+?>)(.+?)(?=<)/g,
               className: 'blue'
           },
           {
               highlight: /(?<=<p2.+?>)(.+?)(?=<)/g,
               className: 'yellow'
           }
       ]
    });
    $('#unprocessed1Text').highlightWithinTextarea({
       highlight:
       [
           {
               highlight: /(?<=<p.+?>)(.+?)(?=<)/g,
               className: 'blue'
           }
       ]
    });
    $('#unprocessed2Text').highlightWithinTextarea({
       highlight:
       [
           {
               highlight: /(?<=<p.+?>)(.+?)(?=<)/g,
               className: 'blue'
           }
       ]
    });
    $('#unprocessed-tab').on('click', function(){
        $('#unprocessedText').highlightWithinTextarea('update');
    });
    $('#unproc1-tab').on('click', function(){
        $('#unprocessed1Text').highlightWithinTextarea('update');
    });
    $('#unproc2-tab').on('click', function(){
        $('#unprocessed2Text').highlightWithinTextarea('update');
    });
    $('#resultText').highlightWithinTextarea({
       highlight:
       [
           {
               highlight: /(<dp|<\/dp>)/g,
               className: 'red'
           },
           {
               highlight: /(<ds>|<\/ds>)/g,
               className: 'green'
           },
           {
               highlight: /(?<=<s1.+?>)(.+?)(?=<)/g,
               className: 'blue'
           },
           {
               highlight: /(?<=<s2.+?>)(.+?)(?=<)/g,
               className: 'yellow'
           }
       ]
    });
    $('#result-tab').on('click', function(){
        $('#resultText').highlightWithinTextarea('update');
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
function getTaskInfoAjax(taskId){
    var url = "/tasks/getById?id=" + taskId;
    $.ajax({
           type: "GET",
           url: url,
           success: function(data, textStatus, jqXHR) {
               document.getElementById("name").setAttribute("value",data.name);
               document.getElementById("unprocessedText").innerHTML=data.unprocessed;
               document.getElementById("resultText").innerHTML=data.result;
               document.getElementById("unprocessed1Text").innerHTML=data.unprocessed1;
               document.getElementById("unprocessed2Text").innerHTML=data.unprocessed2;
               if(data.userId == null)
                requestUsers(-1);
               else  requestUsers(data.userId);
               requestBooks(data.bookId);
               $('#statuspicker').selectpicker('refresh');
               $('#statuspicker').selectpicker('val', data.status);
           },
           error: function(jqXHR, textStatus, errorThrown) {
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
           }
    });
}
function requestBooks(selectedId){
 $.ajax({
        type: "GET",
        url: "/books/getBookMenuItems?withImage=0",
        success: function(data, textStatus, jqXHR) {
            var select = document.getElementById("bookpicker");
            populateSelect(select, data);
            $('#bookpicker').selectpicker('refresh');
            $('#bookpicker').selectpicker('val', selectedId);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
 });
}
function requestUsers(selectedId){
 $.ajax({
        type: "GET",
        url: "/users/getAll",
        success: function(data, textStatus, jqXHR) {
            var select = document.getElementById("userpicker");
            populateSelectUser(select, data);
            $('#userpicker').selectpicker('refresh');
            $('#userpicker').selectpicker('val', selectedId);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function populateSelect(select, arrayData){
    for(var i =0; i < arrayData.length; i++){
        var option = document.createElement("option");
        option.text = arrayData[i].title;
        option.setAttribute("value", arrayData[i].id);
        select.add(option);
    }
}
function populateSelectUser(select, arrayData){
    //add NULL vith value -1
    var option = document.createElement("option");
    option.text = "null";
    option.setAttribute("value", -1);
    select.add(option);
    for(var i =0; i < arrayData.length; i++){
        var option = document.createElement("option");
        option.text = arrayData[i].mail;
        option.setAttribute("value", arrayData[i].id);
        select.add(option);
    }
}
function deleteTask(taskId){
    $.ajax({
        type: "DELETE",
        url: "/tasks/delete",
        contentType: "text/plain",
        data: taskId,
        success: function(textStatus, jqXHR) {
            window.location.href = "/admin/tasks";
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function submitFormData(taskId){

    var form = $('#infoForm')[0];
    var data = new FormData(form);
    data.append("id",taskId);
    var unprocessed = document.getElementById("unprocessedText").value;
    var result = document.getElementById("resultText").value;
    var unprocessed1 = document.getElementById("unprocessed1Text".value);
    var unprocessed2 = document.getElementById("unprocessed2Text".value);
    data.append("unprocessed", unprocessed);
    data.append("result", result);
    data.append("unprocessed1", unprocessed1);
    data.append("unprocessed2", unprocessed2);

    $.ajax({
        type: "POST",
        url: "/tasks/update",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function(textStatus, jqXHR) {
            window.location.href = "/admin/tasks";
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}