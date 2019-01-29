
$(document).ready(function() {

    $.ajax({
               type: "GET",
               url: "/tasks/user",
               dataType: "json",
               success: function(data, textStatus, jqXHR) {
                    var table = document.getElementById("mytasksTable");
                    populateTaskTable(table,data);
                    console.log(data);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                        alert("error");
                        console.log(jqXHR);
                }
    });
    $.ajax({
               type: "GET",
               url: "/tasks/all",
               dataType: "json",
               success: function(data, textStatus, jqXHR) {
                    var table = document.getElementById("alltasksTable");
                    populateTaskTable(table,data);
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
    $('#addtaskbtn').on('click',function(){
        console.log('here');
        window.location.href = "/tasks/create";
    });

});

function populateTaskTable(table, arrayData, onlyDo){
      for(var i =0; i < arrayData.length; i++){
          var newRow = table.getElementsByTagName('tbody')[0].insertRow(table.length);
          var cell = newRow.insertCell(0);
          //if(onlyDo == true)
          //  cell.innerHTML = "<a class=\"btn btn-primary\" href=\"#\"><i class=\"fa fa-briefcase\" ></i>  Do</a>"
          //else
            cell.innerHTML = "<a class=\"btn btn-default\" href=\"#\">"+
                              "<i class=\"fa fa-cog\" aria-hidden=\"true\"></i></a>"+
                              "<a class=\"btn btn-default\" href=\"#\">"+
                              "<i class=\"fa fa-wrench\" aria-hidden=\"true\"></i></a>"+
                              "<a class=\"btn btn-default\" href=\"#\">"+
                              "<i class=\"fa fa-check\" aria-hidden=\"true\"></i></a>";
          /*cell.innerHTML = "<div class=\"btn-group\" role=\"group\" aria-label=\"Basic example\">"+
                               "<button type=\"button\" class=\"btn btn-secondary\">Do</button>"+
                               "<button type=\"button\" class=\"btn btn-secondary\">Done</button>"
                           "</div>\"";*/
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