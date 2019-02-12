$(document).ready(function(){
    var taskId = findGetParameter("id");
    requestResult(taskId);

    $("#submitTask").on('click', function(){
            var url = "/tasks/process/submit?id="+taskId;
            $.ajax({
                      type: "POST",
                      url: url,
                      success: function(data, textStatus, jqXHR) {
                        window.location.href= "/tasks";
                      },
                      error: function(jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        alert("error");
                      }
                   });
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
function requestResult(taskId){
    var url = "/tasks/getResult?id="+taskId;
    $.ajax({
              type: "GET",
              url: url,
              success: function(data, textStatus, jqXHR) {
                document.getElementById("result").value = data;
              },
              error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                alert("error");
              }
           });
}