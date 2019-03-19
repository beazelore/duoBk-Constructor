
$(document).ready(function() {
    var taskId = findGetParameter("id");
    checkPermission(taskId);
    sessionStorage.removeItem('indexesStart');
    sessionStorage.removeItem('indexesEnd');
    getEntriesDataAjax(taskId);

    $('#book1_list').on('change', function(e){
    var value = $('#book1_list option:selected').text();
    $('#active1').html(value);
    });

    $('#book2_list').on('change', function(e){
    var value = $('#book2_list option:selected').text();
    $('#active2').html(value);
    });
   $('#connect').on('click', function(){
       var ind1 = $('#book1_list').val();
       var ind2 = $('#book2_list').val();
       if(ind1.length === 0 || ind2.length === 0)
           alert("Please select options from both sides");
       else{
           var selector = "#book1_list option[value=\"" + ind1[0] + "\"]";
           var chapter = $(selector)[0].getAttribute("chapter");
           var url = "/tasks/process/sent?id="+taskId+"&chapter="+ chapter + "&index="+ "&fromProcess=false";
           var indexes = {start1: ind1, start2: ind2};
           localStorage.setItem("sentIndexes",JSON.stringify(indexes));
           var select1 = document.getElementById("book1_list");
           var select2 = document.getElementById("book2_list");
           for(var i =0; i< ind1.length;i++){
               var selector = "#book1_list option[value=\"" + ind1[i] + "\"]";
               $(selector).remove();
           }
           for(var i =0; i< ind2.length;i++){
               var selector = "#book2_list option[value=\"" + ind2[i] + "\"]";
               $(selector).remove();
           }
           openInNewTab(url);
       }
   });
   $('#startProcess').on('click', function(){
    var ind1 = $('#book1_list').val();
    var ind2 = $('#book2_list').val();
    var data = {indexes1 : ind1, indexes2 : ind2};
    if(sessionStorage.getItem('indexesStart') === null){
        sessionStorage.setItem('indexesStart', JSON.stringify(data));
        toLastMatchState();
    }
    else {
        var startIndexes = JSON.parse(sessionStorage.getItem('indexesStart'));
        var indexes = {start1: startIndexes.indexes1, start2: startIndexes.indexes2, end1: data.indexes1, end2: data.indexes2, taskId: taskId};
        // check if there is no gap between start and end indexes
        var option = $("#book1_list option[value=\"" + indexes.start1 + "\"]")[0];
        var gapFound = false;
        var gapValue;
        var currValue = option.getAttribute("value");
        while(option.nextSibling.getAttribute("value") !== indexes.end1[0]){
            option = option.nextSibling;
            if(Number(currValue) != Number(option.getAttribute("value")) - 1){
                gapFound = true;
                gapValue = Number(currValue) +1;
                break;
            }
            currValue = option.getAttribute("value");
        }
        if(gapFound){
            alert("There is a gap between start and end indexes in list1, index " + gapValue + " is missing.");
            toFirstMatchState();
            return;
        }
        // check second list
        option = $("#book2_list option[value=\"" + indexes.start2 + "\"]")[0];
        gapFound = false;
        gapValue;
        currValue = option.getAttribute("value");
        while(option.nextSibling.getAttribute("value") !== indexes.end2[0]){
            option = option.nextSibling;
            if(Number(currValue) != Number(option.getAttribute("value")) - 1){
                gapFound = true;
                gapValue = Number(currValue) +1;
                break;
            }
            currValue = option.getAttribute("value");
        }
        if(gapFound){
            alert("There is a gap between start and end indexes in list2, index " + gapValue + " is missing.");
            toFirstMatchState();
            return;
        }

        $.ajax({
            type: "POST",
            url: "/tasks/preProcess/do",
            contentType: "application/json",
            data: JSON.stringify(indexes),
            success: function(textStatus, jqXHR) {
                //remove options from selects
                for(var i = indexes.start1; i<=indexes.end1; i++){
                    $("#book1_list option[value=\"" + i + "\"]").remove();
                }
                for(var i = indexes.start2; i<=indexes.end2; i++){
                    $("#book2_list option[value=\"" + i + "\"]").remove();
                }

                var href = "/tasks/process?id="+taskId;
                openInNewTab(href);
                //window.location.href = href;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
                console.log(textStatus);
                console.log(jqXHR);
            }
        });
    }


    });
});
document.onkeydown = function (e) {
    var keyCode = e.keyCode;
    if(keyCode == 46) {
        deleteActiveParagraphsFromUnprocessed(findGetParameter("id"));
    }
};

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
  $.ajax({
            type: "GET",
            url: "/tasks/preProcess/getUnprocessedAsHTML?id="+taskId,
            success: function(data, textStatus, jqXHR) {
                var book1 = data.split('!separator!')[0];
                var book2 = data.split('!separator!')[1];
                $('#book1_list').html(book1);
                $('#book2_list').html(book2);
                //checkUnprocessedEmpty(taskId);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
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
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
            }
        });
}
function toFirstMatchState(){
        sessionStorage.removeItem('indexesStart');
        sessionStorage.removeItem('indexesEnd');
        document.getElementById("startProcess").innerHTML="OK";
        document.getElementById("hint").innerHTML="* Please select first matching paragraphs of books and press OK.";
}
function toLastMatchState(){
        document.getElementById("startProcess").innerHTML="Submit";
        document.getElementById("hint").innerHTML="* Now please choose last matching sentences and press SUBMIT";
}
function openInNewTab(url) {
  var win = window.open(url, '_blank');
  win.focus();
}
function deleteActiveOptions(select){
    var values = $(select).val();
    for(var i =0; i < values.length; i++){
        var option = $(select).find("option[value=\"" + values[i] + "\"]")[0];
        option.remove();
    }
}
function deleteActiveParagraphsFromUnprocessed(taskId){
   var ind1 = $('#book1_list').val();
   var ind2 = $('#book2_list').val();
   var indexes = {start1: ind1, start2: ind2};
   $.ajax({
       type: "POST",
       url: "/tasks/deleteFromUnprocessed?id="+taskId,
       contentType: "application/json",
       data: JSON.stringify(indexes),
       success: function(textStatus, jqXHR) {
           deleteActiveOptions(document.getElementById("book1_list"));
           deleteActiveOptions(document.getElementById("book2_list"));
       },
       error: function(jqXHR, textStatus, errorThrown) {
           alert("error, check console for details");
           console.log("ERROR : ", jqXHR.responseText);
           console.log(textStatus);
           console.log(jqXHR);
       }
   });

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


