
$(document).ready(function() {

    populateMyTasksAjax();
    $.ajax({
               type: "GET",
               url: "/tasks/allWithNoUser",
               dataType: "json",
               success: function(data, textStatus, jqXHR) {
                    var table = document.getElementById("alltasksTable");
                    populateTaskTable(table,data, true);
                    console.log(data);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                        alert("error");
                        console.log(jqXHR);
                }
    });
    $('#logout').on('click',function(){
    console.log("click");
        $.ajax({
               type: "POST",
               url: "/logout",
               success: function(){
                window.location.href = "/";
               }
        });

    });
    $('.table').on('click', '.dotaskbtn', function (){
      var id = this.id;
      $.ajax({
                     type: "POST",
                     data: id,
                     contentType: "text/plain",
                     url: "/tasks/take",
                     success: function(){
                        console.log("good");
                        var btn = document.getElementById(id);
                        var i = btn.parentNode.parentNode.rowIndex;
                        console.log(i);
                        document.getElementById("alltasksTable").deleteRow(i);
                        populateMyTasksAjax();
                     }
              });
    });

});
function populateMyTasksAjax(){
    $.ajax({
                  type: "GET",
                  url: "/tasks/user",
                  dataType: "json",
                  success: function(data, textStatus, jqXHR) {
                       var table = document.getElementById("mytasksTable");
                       populateTaskTable(table,data, false);
                       console.log(data);
                   },
                   error: function(jqXHR, textStatus, errorThrown) {
                           alert("error");
                           console.log(jqXHR);
                   }
       });
}
function populateTaskTable(table, arrayData, onlyDo){
        var tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = "";
        for(var i =0; i < arrayData.length; i++){
            var newRow = tbody.insertRow(table.length);
            var cell = newRow.insertCell(0);
            if(onlyDo == true)
              cell.innerHTML = "<button class=\"btn btn-primary dotaskbtn\" href=\"#\" " +"id=\""+arrayData[i].id+"\">"+
              "<i class=\"fa fa-briefcase\" ></i>  Do</button>"
            else
              cell.innerHTML = "<a class=\"btn btn-default\" href=\"#\">"+
                                "<i class=\"fa fa-cog\" aria-hidden=\"true\"></i></a>"+
                                "<a class=\"btn btn-default\" href=\"#\">"+
                                "<i class=\"fa fa-wrench\" aria-hidden=\"true\"></i></a>"+
                                "<a class=\"btn btn-default\" href=\"#\">"+
                                "<i class=\"fa fa-check\" aria-hidden=\"true\"></i></a>";
            cell.setAttribute("class", "actionCell");
            cell = newRow.insertCell(1);
            cell.innerHTML = arrayData[i].status;
            cell.setAttribute("class", "statusCell");
            cell = newRow.insertCell(2);
            cell.innerHTML = arrayData[i].name;
            cell.setAttribute("class", "nameCell");
        }
}

function htmlToElement(html) {
    var template = document.createElement('template');
    html = html.trim(); // Never return a text node of whitespace as the result
    template.innerHTML = html;
    return template.content.firstChild;
}