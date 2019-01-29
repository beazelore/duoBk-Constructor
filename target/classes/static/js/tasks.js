$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        if (settings.type == 'POST' || settings.type == 'PUT' || settings.type == 'DELETE') {
            function getCookie(name) {
            console.log("in cookies");
                var cookieValue = null;
                if (document.cookie && document.cookie != '') {
                    var cookies = document.cookie.split(';');
                    for (var i = 0; i < cookies.length; i++) {
                        var cookie = jQuery.trim(cookies[i]);
                        // Does this cookie string begin with the name we want?
                        if (cookie.substring(0, name.length + 1) == (name + '=')) {
                            cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                            break;
                        }
                    }
                }
                console.log("returning cookie");
                console.log(cookieValue);
                return cookieValue;
            }
            if (!(/^http:.*/.test(settings.url) || /^https:.*/.test(settings.url))) {
                // Only send the token to relative URLs i.e. locally.
                xhr.setRequestHeader("X-XSRF-TOKEN", getCookie('XSRF-TOKEN'));
            }
        }
    }
});

$(document).ready(function() {

    $.ajax({
               type: "GET",
               url: "/user",
               success: function(data, textStatus, jqXHR) {
                        console.log(data);
                        $('#navbarDropdownMenuUser').html(data.userAuthentication.details.name);
                        var role = data.authorities[0].authority;
                        console.log(role);
                        /*if(role == "ROLE_ADMIN"){
                            var addTaskButton = "<div class=\"container-fluid btn-block\">"+
                                                     "<button class=\"btn btn-large btn-block btn-info\" id=\"addtaskbtn\" type=\"button\">Add Task</button>"+
                                                 "</div>\"";
                            var addTaskButtonNode = htmlToElement(addTaskButton);
                            var allTasksBlock = document.getElementById('alltasks');
                            allTasksBlock.insertBefore(addTaskButtonNode, allTasksBlock.firstChild);
                        }*/
                },
                error: function(jqXHR, textStatus, errorThrown) {
                        alert("error");
                        console.log(jqXHR);
                }
    });
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

function populateTaskTable(table, arrayData){
      for(var i =0; i < arrayData.length; i++){
          var newRow = table.insertRow(table.length);
          var cell = newRow.insertCell(0);
          cell.innerHTML = "<div class=\"btn-group\" role=\"group\" aria-label=\"Basic example\">"+
                               "<button type=\"button\" class=\"btn btn-secondary\">Do</button>"+
                               "<button type=\"button\" class=\"btn btn-secondary\">Done</button>"
                           "</div>\"";
          cell = newRow.insertCell(1);
          cell.innerHTML = arrayData[i].name;
          cell = newRow.insertCell(2);
          cell.innerHTML = arrayData[i].wordCount;
          cell = newRow.insertCell(3);
          cell.innerHTML = arrayData[i].status;
      }
}

function htmlToElement(html) {
    var template = document.createElement('template');
    html = html.trim(); // Never return a text node of whitespace as the result
    template.innerHTML = html;
    return template.content.firstChild;
}