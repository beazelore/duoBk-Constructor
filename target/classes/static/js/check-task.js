$(document).ready(function(){
    var taskId = findGetParameter("id")
    requestResult(taskId);
    $('#confirmTask').on('click',function(){
        saveResult(taskId);
    });
});

function requestResult(taskId){
  url = "/tasks/integrateIntoBook?id="+taskId;
 $.ajax({
        type: "GET",
        url: url,
        success: function(data, textStatus, jqXHR) {
            document.getElementById("result").value = data;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}

function saveResult(taskId){
  url = "/tasks/updateBookValue?id="+taskId;
  var value = document.getElementById("result").value;
  $.ajax({
         type: "POST",
         url: url,
         contentType: "text/plain",
         data: value,
         success: function(data, textStatus, jqXHR) {
             window.location.href = "/admin/tasks";
         },
         error: function(jqXHR, textStatus, errorThrown) {
             console.log("ERROR : ", jqXHR.responseText);
         }
     });
}

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