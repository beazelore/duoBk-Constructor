$(document).ready(function(){
    var taskId = findGetParameter("id");
    var dpIndex = findGetParameter("index");
    //if we come here from correcting tab of process, no dpIndex will be specified
    //in that case we retrieve our HTML from different endpoint
    if(dpIndex === ""){
        var indexes = JSON.parse(localStorage.getItem("sentIndexes"));
        var postUrl = "/tasks/process/sent/correcting/do?id=" + taskId;
        console.log("indexes", indexes);
        $.ajax({
            type: "POST",
            url: postUrl,
            contentType: "application/json",
            data: JSON.stringify(indexes),
            success: function(data, textStatus, jqXHR) {
                  var container = document.getElementById("checkContainer");
                  container.innerHTML = container.innerHTML + data;
                  console.log(data);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                alert("error");
            }
        });
    }
    else
        getSentProcessHTML(taskId,dpIndex);

    $('.container-fluid').on('click','select.first option', function(e){
         var valueSelected = this.value;
        $('#checkActive1').html(valueSelected);
    });
    $('.container-fluid').on('click','select.second option', function(e){
         var valueSelected = this.value;
        $('#checkActive2').html(valueSelected);
    });

    $('.container-fluid').on('click','.btn-success',function(){
        var cacheString = "<ds>";
        var options1 = $("")
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
function getSentProcessHTML(taskId, dpIndex){
    var url = "/tasks/process/sent/do?id=" + taskId+"&index="+dpIndex;
    $.ajax({
              type: "GET",
              url: url,
              success: function(data, textStatus, jqXHR) {
                  var container = document.getElementById("checkContainer");
                  container.innerHTML = container.innerHTML + data;
                  console.log(data);
              },
              error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                alert("error");
              }
           });
}