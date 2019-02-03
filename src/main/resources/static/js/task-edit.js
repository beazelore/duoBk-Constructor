$(document).ready(function(){
    var taskId = findGetParameter("id");
    getTaskInfoAjax(taskId);

    $(":submit").on('click',function(){
        event.preventDefault();
        submitFormData(taskId);
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
               console.log(data);
               document.getElementById("name").setAttribute("value",data.name);
               document.getElementById("unprocessedText").innerHTML=data.unprocessed;
               document.getElementById("processedText").innerHTML=data.processed;
               document.getElementById("resultText").innerHTML=data.result;
               requestUsers(data.userId)
               requestBooks(data.bookId);
               $('#statuspicker').selectpicker('val', data.status);
           },
           error: function(jqXHR, textStatus, errorThrown) {
               console.log("ERROR : ", jqXHR.responseText);
           }
       });
}
function requestBooks(selectedId){
 $.ajax({
        type: "GET",
        url: "/books/getAll",
        success: function(data, textStatus, jqXHR) {
            console.log(data);
            var select = document.getElementById("bookpicker");
            populateSelect(select, data);
            $('#bookpicker').selectpicker('refresh');
            //$('#bookpicker').val(selectedId);
            console.log("bookId", selectedId);
            $('#bookpicker').selectpicker('val', selectedId);
            //$('#bookpicker').selectpicker('refresh')
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function requestUsers(selectedId){
 $.ajax({
        type: "GET",
        url: "/users/getAll",
        success: function(data, textStatus, jqXHR) {
            console.log(data);
            var select = document.getElementById("userpicker");
            populateSelectUser(select, data);
            $('#userpicker').selectpicker('refresh');
            console.log("userId", selectedId);
            $('#userpicker').selectpicker('val', selectedId);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function populateSelect(select, arrayData){
    for(var i =0; i < arrayData.length; i++){
        var option = document.createElement("option");
        option.text = arrayData[i].name;
        option.setAttribute("value", arrayData[i].id);
        select.add(option);
    }
}
function populateSelectUser(select, arrayData){
    for(var i =0; i < arrayData.length; i++){
        var option = document.createElement("option");
        option.text = arrayData[i].mail;
        option.setAttribute("value", arrayData[i].id);
        select.add(option);
    }
}
function submitFormData(taskId){

    var form = $('#infoForm')[0];
    var data = new FormData(form);
    data.append("id",taskId);
    //var value = $('#book-value').text();
    var unprocessed = document.getElementById("unprocessedText").value;
    var processed = document.getElementById("processedText").value;
    var result = document.getElementById("resultText").value;
    data.append("unprocessed", unprocessed);
    data.append("processed", processed);
    data.append("result", result);

    console.log("task: ", data);


    $.ajax({
        type: "POST",
        url: "/tasks/update",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function(textStatus, jqXHR) {
            console.log("SUCCESSSSS");
            window.location.href = "/admin/tasks";
                    },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });

}