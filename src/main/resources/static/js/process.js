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

        $('.container-fluid').on('click','.btn-success',function(){
            var url = "/tasks/process/sent?id="+taskId+"&index="+this.id;
            openInNewTab(url);
            var divId = "row-connection"+this.id;
            var div = document.getElementById(divId);
            if (div) {
                div.parentNode.removeChild(div);
            }
        });

        $('.container-fluid').on('click','.btn-warning',function(){
            var divId = "row-connection"+this.id;
            var div = document.getElementById(divId);
            var selectOptions1 = $("#"+divId + " select.first option");
            var selectOptions2 = $("#"+divId + " select.second option");
            var selectCorrecting1 = document.getElementById("book1_list");
            var selectCorrecting2 = document.getElementById("book2_list");
            for(var i =0; i<selectOptions1.length;i++){
                //console.log(i);
                //console.log(selectOptions1[i].text);
                var option = new Option(selectOptions1[i].text);
                option.setAttribute("dpIndex", this.id);
                option.setAttribute("value", selectOptions1[i].getAttribute("value"));
                selectCorrecting1.options[selectCorrecting1.options.length] = option;
            }
            for(var i =0; i<selectOptions2.length;i++){
                var option = new Option(selectOptions2[i].text);
                option.setAttribute("dpIndex", this.id);
                option.setAttribute("value", selectOptions2[i].getAttribute("value"));
                selectCorrecting2.options[selectCorrecting2.options.length] = option;
            }
            if (div) {
                div.parentNode.removeChild(div);
            }
        });

        $('#connect').on('click',function(){
            var ind1 = $('#book1_list').val();
            var ind2 = $('#book2_list').val();
            if(ind1.length === 0 || ind2.length === 0)
                alert("Please select options from both sides");
            else{
                var url = "/tasks/process/sent?id="+taskId+"&index=";
                var indexes = {start1: ind1, start2: ind2};
                localStorage.setItem("sentIndexes",JSON.stringify(indexes));
                var select1 = document.getElementById("book1_list");
                var select2 = document.getElementById("book2_list");
                for(var i =0; i< ind1.length;i++){
                    var selector = "#book1_list option[value=\"" + ind1[i] + "\"]"
                    $(selector).remove();
                }
                for(var i =0; i< ind2.length;i++){
                    var selector = "#book2_list option[value=\"" + ind2[i] + "\"]"
                    $(selector).remove();
                }
                openInNewTab(url);
            }
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
function openInNewTab(url) {
  var win = window.open(url, '_blank');
  win.focus();
}