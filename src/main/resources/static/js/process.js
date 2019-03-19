$(document).ready(function(){
    var taskId = findGetParameter("id");
    checkPermission(taskId);
    getUnprocessed(taskId);
    getBad(taskId);

    $('.maincontainer').on('dblclick', 'select.first option',function() {
      var textArea = document.getElementById("active1");
      textArea.select();
      document.execCommand("copy");
    });
    $('.maincontainer').on('dblclick', 'select.second option',function() {
      var textArea = document.getElementById("active2");
      textArea.select();
      document.execCommand("copy");
    });
    $('.maincontainer').on('change', 'select.first', function(e){
        var options = $('select:focus option:selected');
        clearSelected(true);
        var value ="";
        for(var i=0; i< options.length;i++){
            options[i].selected = true;
            var myRegexp = /[0-9]+\.  /;
            var temp = options[i].innerHTML.split(myRegexp).pop();
            value += temp;
        }
        $('#active1').html(value);
    });
    $('.maincontainer').on('change', 'select.second', function(e){
        var options = $('select:focus option:selected');
        clearSelected(false);
        var value ="";
        for(var i=0; i< options.length;i++){
            options[i].selected = true;
            var myRegexp = /[0-9]+\.  /;
            var temp = options[i].innerHTML.split(myRegexp).pop();
            value += temp;
        }
        $('#active2').html(value);
    });

    $('#book1_list').on('change', function(e){
        var options = $('#book1_list option:selected');
        var value ="";
        for(var i =0; i < options.length; i++){
            var myRegexp = /[0-9]+\.  /;
            var temp = options[i].innerHTML.split(myRegexp).pop();
            value += temp;
        }
        $('#correctingActive1').html(value);
    });
    $('#book2_list').on('change', function(e){
        var options = $('#book2_list option:selected');
        var value ="";
        for(var i =0; i < options.length; i++){
            var myRegexp = /[0-9]+\.  /;
            var temp = options[i].innerHTML.split(myRegexp).pop();
            value += temp;
        }
        $('#correctingActive2').html(value);
    });

    $('#book1_list').on('dblclick', 'option',function() {
      var textArea = document.getElementById("correctingActive1");
      textArea.select();
      document.execCommand("copy");
    });
    $('#book2_list').on('dblclick', 'option',function() {
      var textArea = document.getElementById("correctingActive2");
      textArea.select();
      document.execCommand("copy");
    });
    $('.container-fluid').on('click','.btn-success',function(){
        var url = "/tasks/process/sent?id="+taskId+"&index="+this.id+ "&chapter=" + this.getAttribute("chapter") + "&fromProcess=true";
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
        unprocessedToBad(taskId,this.id);
        if (div) {
            div.parentNode.removeChild(div);
        }
    });
    $('#connect').on('click',function(){
        var ind1 = $('#book1_list').val();
        var ind2 = $('#book2_list').val();
        var selector = "#book1_list option[value=\"" + ind1[0] + "\"]";
        var chapter = $(selector)[0].getAttribute("chapter");
        if(ind1.length === 0 || ind2.length === 0)
            alert("Please select options from both sides");
        else{
            var url = "/tasks/process/sent?id="+taskId+"&chapter="+ chapter + "&index="+"&fromProcess=true";
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
    $("#correctingNavItem").on('click', function(){
         getBad(taskId);
    });
});
function getUnprocessed(taskId){
    var url = "/tasks/process/unprocessedToHTML?id=" + taskId;
    $.ajax({
        type: "GET",
        url: url,
        success: function(data, textStatus, jqXHR) {
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
                alert("error, check console for details");
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
function openInNewTab(url) {
  var win = window.open(url, '_blank');
  win.focus();
}

function unprocessedToBad(taskId, dpIndex){
    var url = "/tasks/process/moveToBad?id="+taskId+"&index="+dpIndex;
    $.ajax({
         type: "GET",
         url: url,
         error: function(jqXHR, textStatus, errorThrown) {
           alert("error, check console for details");
           console.log("ERROR : ", jqXHR.responseText);
         }
    });
}

function getBad(taskId){
    var url = "/tasks/process/getBadResponse?id="+taskId;
    $.ajax({
        type: "GET",
        url: url,
        success: function(data, textStatus, jqXHR) {
          var p1 = data.split('!separator!')[0];
          var p2 = data.split('!separator!')[1];
          document.getElementById("book1_list").innerHTML = p1;
          document.getElementById("book2_list").innerHTML = p2;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}

function clearSelected(first){
    if(first){
        var elements = $("select.first option");
        for(var i = 0; i < elements.length; i++)
          elements[i].selected = false;
    }
    else{
        var elements = $("select.second option");
        for(var i = 0; i < elements.length; i++)
          elements[i].selected = false;
    }
}

function checkPermission(taskId){
$.ajax({
       type: "GET",
       url: "/tasks/checkPermission?id="+taskId,
       error: function(jqXHR, textStatus, errorThrown) {
       console.log(jqXHR.status);
           if (jqXHR.status == 401){
                alert("Permission denied");
                window.location.href = "/tasks";
           }
           else{
               alert("error, check console for details");
               console.log("ERROR : ", jqXHR.responseText);
               console.log(textStatus);
               console.log(jqXHR);

           }
       }
   });
}