$(document).ready(function(){
    requestTasks();
});
function requestTasks(){
 $.ajax({
        type: "GET",
        url: "/tasks/all",
        success: function(data, textStatus, jqXHR) {
            var table = document.getElementById("allTasksTable");
            populateTaskTable(table,data);
            //var map = new Map(data);
            console.log("data:");
            console.log(data);
            /*for (var i =0; i< Object.keys(data).length; i++) {
              console.log(Object.keys(data)[i]);
              console.log(Object.values(data)[i]);
            }*/
            //console.log(table);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}

function populateTaskTable(table, map){
        var tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = "";
        for(var i =0; i < Object.keys(map).length; i++){
            var task = Object.values(map)[i];
            var mail = Object.keys(map)[i];
            var taskId = task.id;
            var newRow = tbody.insertRow(table.length);
            var cell = newRow.insertCell(0);
            if(task.status != "CHECK_NEEDED"){
                cell.innerHTML = "<a class=\"btn btn-default\" href=\"/admin/tasks/edit?id="+taskId+"\">" +
                "<i aria-hidden=\"true\" class=\"fa fa-wrench\" ></i></a>";
            }
            else{
                cell.innerHTML = "<a class=\"btn btn-default\" href=\"/admin/tasks/edit?id="+taskId+"\">" +
                "<i aria-hidden=\"true\" class=\"fa fa-wrench\" ></i></a><a class=\"btn btn-default\" href=\"/admin/tasks/check?id=" + taskId+ "\">"+
                "<i aria-hidden=\"true\" class=\"fa fa-check\" ></i></a>";
            }
            cell.setAttribute("class", "actionCell");
            cell = newRow.insertCell(1);
            cell.innerHTML = taskId;
            cell.setAttribute("class", "idCell");
            cell = newRow.insertCell(2);
            cell.innerHTML = task.status;
            cell.setAttribute("class", "statusCell");
            cell = newRow.insertCell(3);
            cell.innerHTML = mail;
            cell.setAttribute("class", "statusCell");
            cell = newRow.insertCell(4);
            cell.innerHTML = task.name;
            cell.setAttribute("class", "nameCell");
        }
}