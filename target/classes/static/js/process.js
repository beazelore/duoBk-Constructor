$(document).ready(function(){
    var taskId = findGetParameter("id");
    getUnprocessed(taskId);

        $('.container-fluid').on('click','select.first option', function(e){
             var valueSelected = this.value;
            $('#active1').html(valueSelected);
        });

        $('.container-fluid').on('click','select.second option', function(e){
             var valueSelected = this.value;
            $('#active2').html(valueSelected);
        });
});
function getUnprocessed(taskId){
    var url = "/tasks/process/unprocessedToHTML?id=" + taskId;
    $.ajax({
                  type: "GET",
                  url: url,
                  success: function(data, textStatus, jqXHR) {
                       console.log(data);
                       if(jqXHR.status === 204){
                            alert("Pre-process must be done first");
                            var href = "/tasks/preProcess?id=" + taskId;
                            window.location.href= href;
                       }
                       var container =document.getElementById("mainContainer");
                       container.innerHTML = container.innerHTML + data;
                   },
                   error: function(jqXHR, textStatus, errorThrown) {
                           if(jqXHR.status === 204){
                                alert("Pre-process must be done first");
                                var href = "/tasks/preProcess?id=" + taskId;
                                window.location.href= href;
                           }
                           alert("error");
                           console.log(jqXHR);
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