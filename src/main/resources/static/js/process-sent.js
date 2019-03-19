$(document).ready(function(){
    var taskId = findGetParameter("id");
    var dpIndex = findGetParameter("index");
    var fromProcess = findGetParameter("formProcess");
    var chapterIndex = findGetParameter("chapter");
    sessionStorage.removeItem("ds");
    //if we come here from correcting tab of process, no dpIndex will be specified
    //in that case we retrieve our HTML from different endpoint
    if(dpIndex === ""){
        var indexes = JSON.parse(localStorage.getItem("sentIndexes"));
        var postUrl = "/tasks/process/sent/correcting/do?id=" + taskId;
         if(fromProcess == "true")
            postUrl += "&fromBad=true";
         else  postUrl += "&fromBad=false";
        $.ajax({
            type: "POST",
            url: postUrl,
            contentType: "application/json",
            data: JSON.stringify(indexes),
            success: function(data, textStatus, jqXHR) {
                  var container = document.getElementById("checkContainer");
                  container.innerHTML = container.innerHTML + data;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
            }
        });
    }
    else
        getSentProcessHTML(taskId,dpIndex);

    $('.container-fluid').on('click','select.first option', function(e){
         var textSelected = this.innerHTML;
         var myRegexp = /[0-9]+\.  /;
         $('#checkActive1').html(textSelected.split(myRegexp).pop());
    });
    $('.container-fluid').on('click','select.second option', function(e){
         var textSelected = this.innerHTML;
         var myRegexp = /[0-9]+\.  /;
         $('#checkActive2').html(textSelected.split(myRegexp).pop());
    });

    $('.container-fluid').on('dblclick', 'select.first option',function() {
      var textArea = document.getElementById("checkActive1");
      textArea.select();
      document.execCommand("copy");
    });
    $('.container-fluid').on('dblclick', 'select.second option',function() {
      var textArea = document.getElementById("checkActive2");
      textArea.select();
      document.execCommand("copy");
    });
    $('#book1_list').on('change', function(e){
        var options = $('#book1_list option:selected');
        var value ="";
        for(var i =0; i < options.length; i++){
            var myRegexp = /[0-9]+\. /;
            var temp = options[i].innerHTML.split(myRegexp).pop();
            value += temp;
        }
        $('#correctingActive1').html(value);
    });

    $('#book2_list').on('change', function(e){
        var options = $('#book2_list option:selected');
        var value ="";
        for(var i =0; i < options.length; i++){
            var myRegexp = /[0-9]+\. /;
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
        var cacheString  = sessionStorage.getItem("ds");
        if(cacheString === null){
            cacheString = "<dp chapter=\"" + chapterIndex + "\">";
        }
        cacheString += "<ds>"
        var options1 = document.getElementById(this.id).parentElement.parentElement.parentElement.firstChild.childNodes[0].childNodes;
        var options2 = document.getElementById(this.id).parentElement.parentElement.parentElement.childNodes[2].childNodes[0].childNodes;
        for(var i=0; i < options1.length;i++){
            cacheString += "<s1 pIndex=\"" + options1[i].getAttribute("pIndex") + "\" index=\"" + options1[i].getAttribute("value") + "\">";
            var myRegexp = /[0-9]+\.  /;
            cacheString += options1[i].innerHTML.split(myRegexp).pop() + "</s1>";
        }
        for(var i=0; i < options2.length;i++){
            cacheString += "<s2 pIndex=\"" + options2[i].getAttribute("pIndex") + "\" index=\"" + options2[i].getAttribute("value") + "\">";
            var myRegexp = /[0-9]+\.  /;
            cacheString += options2[i].innerHTML.split(myRegexp).pop() + "</s2>";
        }
        cacheString += "</ds>";
        var row = document.getElementById(this.id).parentElement.parentElement.parentElement;
        if (row)
            row.parentNode.removeChild(row);
        sessionStorage.setItem("ds", cacheString);
    });

    $('.container-fluid').on('click','.btn-warning',function(){

        var options1 = document.getElementById(this.id).parentElement.parentElement.parentElement.firstChild.childNodes[0].childNodes;
        var options2 = document.getElementById(this.id).parentElement.parentElement.parentElement.childNodes[2].childNodes[0].childNodes;
        var selectCorrecting1 = document.getElementById("book1_list");
        var selectCorrecting2 = document.getElementById("book2_list");
        for(var i =0; i<options1.length;i++){
                var option = new Option(options1[i].innerHTML);
                option.setAttribute("pIndex", options1[i].getAttribute("pIndex"));
                option.setAttribute("value", options1[i].getAttribute("value"));
                selectCorrecting1.options[selectCorrecting1.options.length] = option;
        }
        for(var i =0; i<options2.length;i++){
                var option = new Option(options2[i].innerHTML);
                option.setAttribute("pIndex", options2[i].getAttribute("pIndex"));
                option.setAttribute("value", options2[i].getAttribute("value"));
                selectCorrecting2.options[selectCorrecting2.options.length] = option;
        }
        var row = document.getElementById(this.id).parentElement.parentElement.parentElement;
        if (row)
            row.parentNode.removeChild(row);
    });

    $("#connectSent").on('click', function(){
            var ind1 = $('#book1_list').val();
            var ind2 = $('#book2_list').val();
            var cacheString  = sessionStorage.getItem("ds");
            if(cacheString === null){
                cacheString = "<dp chapter=\"" + chapterIndex + "\">";
            }
            cacheString += "<ds>";
            for(var i =0; i < ind1.length; i ++){
                 var option = $('#book1_list option[value="'+ ind1[i] + '"]')[0];
                 cacheString += "<s1 pIndex=\"" + option.getAttribute("pIndex") + "\" index=\"" + option.getAttribute("value") + "\">";
                 var myRegexp = /[0-9]+\.  /;
                 cacheString += option.innerHTML.split(myRegexp).pop() + "</s1>";
                 if(option)
                    option.parentNode.removeChild(option);
            }
            for(var i =0; i < ind2.length; i ++){
                 var option = $('#book2_list option[value="'+ ind2[i] + '"]')[0];
                 cacheString += "<s2 pIndex=\"" + option.getAttribute("pIndex") + "\" index=\"" + option.getAttribute("value") + "\">";
                 var myRegexp = /[0-9]+\.  /;
                 cacheString += option.innerHTML.split(myRegexp).pop() + "</s2>";
                 if(option)
                    option.parentNode.removeChild(option);
            }
            cacheString += "</ds>";
            sessionStorage.setItem("ds", cacheString);
    });

    $("#finishProcess").on('click', function(){
        var selects = $("#checkContainer select");
        if(selects.length > 0){
            alert("You should finish process first");
            return;
        }
        var options1 = $('#book1_list option');
        var options2 = $('#book2_list option');
        if(options1.length >0 && options2.length >0){
              var r = confirm("There are unconnected sentences in \"Correcting\" tab. Are you sure you want to finish process?");
              if (r == false){
                return;
              }
        }
        var cacheString  = sessionStorage.getItem("ds");
        var options1 =  $("#book1_list option");
        if(options1.length>0)
            cacheString += "<ds>";
        for(var i=0; i< options1.length;i++){
            var option = options1[i];
            cacheString += "<s1 pIndex=\"" + option.getAttribute("pIndex")+ "\" index=\"" + option.getAttribute("value") +"\">";
            var myRegexp = /[0-9]+\.  /;
            cacheString += option.innerHTML.split(myRegexp).pop() + "</s1>";
        }
        if(options1.length>0)
            cacheString += "</ds>";
        if(cacheString.includes("<dp"))
            cacheString += "</dp>";
        var url = "/tasks/process/sent/finish?id=" + taskId;
        $.ajax({
            type: "POST",
            url: url,
            contentType: "text/plain",
            data: cacheString,
            success: function(textStatus, jqXHR) {
                  window.close();
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
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
function getSentProcessHTML(taskId, dpIndex){
    var url = "/tasks/process/sent/do?id=" + taskId+"&index="+dpIndex;
    $.ajax({
              type: "GET",
              url: url,
              success: function(data, textStatus, jqXHR) {
                  var container = document.getElementById("checkContainer");
                  container.innerHTML = container.innerHTML + data;
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
        console.log(elements);
        for(var i = 0; i < elements.length; i++)
          elements[i].selected = false;
    }
}