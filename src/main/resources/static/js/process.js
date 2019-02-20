$(document).ready(function(){
    var taskId = findGetParameter("id");
    getUnprocessed(taskId);
    getBad(taskId);

      /*$('.container-fluid').on('click','select.first option', function(e){
             var textSelected = this.text;
             var myRegexp = /[0-9]+\. /;
            $('#active1').html(textSelected.split(myRegexp).pop());
        });
        $('.container-fluid').on('click','select.second option', function(e){
             var textSelected = this.text;
             var myRegexp = /[0-9]+\. /;
            $('#active2').html(textSelected.split(myRegexp).pop());
        });*/

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


/*        $('#book1_list').on('click', 'option', function(){
            var textSelected = this.text;
            var myRegexp = /[0-9]+\. /;
            $('#correctingActive1').html(textSelected.split(myRegexp).pop());
        });
        $('#book2_list').on('click', 'option',function(){
            var textSelected = this.text;
            var myRegexp = /[0-9]+\. /;
            $('#correctingActive2').html(textSelected.split(myRegexp).pop());
        });*/
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



        $('.container-fluid').on('click','.btn-success',function(){
            var url = "/tasks/process/sent?id="+taskId+"&index="+this.id+ "&chapter=" + this.getAttribute("chapter");
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
            /*var selectOptions1 = $("#"+divId + " select.first option");
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
            }*/
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
                var url = "/tasks/process/sent?id="+taskId+"&chapter="+ chapter + "&index=";
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
        $("#finishProcess").on('click', function(){
            var options1 = $('#book1_list option');
            var options2 = $('#book2_list option');
            if(options1.length >0 && options2.length >0){
                var r = confirm("There are unconnected paragraphs in \"Correcting\" tab. Are you sure you want to finish process?");
                if (r == false) {
                    return;
                }
            }
            var url = "/tasks/process/finish?id=" + taskId;
            $.ajax({
                 type: "GET",
                 url: url,
                 success: function(data, textStatus, jqXHR) {
                        var href = "/tasks/submit?id=" + taskId;
                        window.location.href = href;
                 },
                 error: function(jqXHR, textStatus, errorThrown) {
                         alert("error");
                         console.log(jqXHR);
                 }
               });
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

function unprocessedToBad(taskId, dpIndex){
    var url = "/tasks/process/moveToBad?id="+taskId+"&index="+dpIndex;
    $.ajax({
              type: "GET",
              url: url,
              success: function(textStatus, jqXHR) {
              },
              error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                alert("error");
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
                //$('#book1_list').html(p1);
                //$('#book2_list').html(p2);
              },
              error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                alert("error");
              }
           });
}

function clearSelected(first){
    if(first){
        var elements = $("select.first option");
        for(var i = 0; i < elements.length; i++){
          elements[i].selected = false;
    }
   }
    else{
        var elements = $("select.second option");
        console.log(elements);
        for(var i = 0; i < elements.length; i++){
            console.log("unselecting", elements[i])
          elements[i].selected = false;
    }
   }
 }