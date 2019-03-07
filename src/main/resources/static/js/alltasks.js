$(document).ready(function(){
    requestTasks();

    $('#allTasksTable th.idCell').on('click', function(){
        var table = document.getElementById("allTasksTable");
        sortTable(table, 1);
    });
    $('#allTasksTable th.statusCell').on('click', function(){
        var table = document.getElementById("allTasksTable");
        sortTable(table, 2);
    });
    $('#allTasksTable th.userCell').on('click', function(){
        var table = document.getElementById("allTasksTable");
        sortTable(table, 3);
    });
    $('#allTasksTable th.nameCell').on('click', function(){
        var table = document.getElementById("allTasksTable");
        sortTable(table, 4);
    });
    $('#allTasksTable th.timeCell').on('click', function(){
        var table = document.getElementById("allTasksTable");
        sortTable(table, 5);
    });
});
function requestTasks(){
 $.ajax({
        type: "GET",
        url: "/tasks/all",
        success: function(data, textStatus, jqXHR) {
            var table = document.getElementById("allTasksTable");
            populateTaskTable(table,data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}

function populateTaskTable(table, list){
        var tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = "";
        for(var i =0; i < list.length; i++){
            var task = list[i].task;
            var mail = list[i].mail;
            var date = new Date(list[i].date);
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
            cell = newRow.insertCell(5);
            cell.innerHTML = date.toLocaleString();
            cell.setAttribute("class", "timeCell");
        }
}

function sortTable(table, n) {
  var rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
  switching = true;
  // Set the sorting direction to ascending:
  dir = "asc";
  /* Make a loop that will continue until
  no switching has been done: */
  while (switching) {
    // Start by saying: no switching is done:
    switching = false;
    rows = table.rows;
    /* Loop through all table rows (except the
    first, which contains table headers): */
    for (i = 1; i < (rows.length - 1); i++) {
      // Start by saying there should be no switching:
      shouldSwitch = false;
      /* Get the two elements you want to compare,
      one from current row and one from the next: */
      x = rows[i].getElementsByTagName("td")[n];
      y = rows[i + 1].getElementsByTagName("td")[n];
      /* Check if the two rows should switch place,
      based on the direction, asc or desc: */
      if (dir == "asc") {
        if(n==1 || n==2|| n==3 || n==4 ){
            // that are status or name cells that we have to compare
            if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
              // If so, mark as a switch and break the loop:
              shouldSwitch = true;
              break;
            }
        }
        else if(n==5){
            //that are dates that we compare
            var date1 = new Date(x.innerHTML);
            var date2 = new Date(y.innerHTML);
            console.log("date1: ", date1);
            console.log("date2: ", date2);
            if(date1 > date2){
                // If so, mark as a switch and break the loop:
                shouldSwitch = true;
                break;
            }
        }

      } else if (dir == "desc") {
        if(n==1 || n==2|| n==3 || n==4 ){
            // that are status or name cells that we have to compare
            if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
              // If so, mark as a switch and break the loop:
              shouldSwitch = true;
              break;
            }
        }
        else if(n==5){
            //that are dates that we compare
            var date1 = new Date(x.innerHTML);
            var date2 = new Date(y.innerHTML);
            if(date1 < date2){
                // If so, mark as a switch and break the loop:
                shouldSwitch = true;
                break;
            }
        }
      }
    }
    if (shouldSwitch) {
      /* If a switch has been marked, make the switch
      and mark that a switch has been done: */
      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
      switching = true;
      // Each time a switch is done, increase this count by 1:
      switchcount ++;
    } else {
      /* If no switching has been done AND the direction is "asc",
      set the direction to "desc" and run the while loop again. */
      if (switchcount == 0 && dir == "asc") {
        dir = "desc";
        switching = true;
      }
    }
  }
}

function searchTable() {
  // Declare variables
  var input, filter, table, tr, td, i, txtValue;
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("allTasksTable");
  tr = table.getElementsByTagName("tr");

  // Loop through all table rows, and hide those who don't match the search query
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[4];
    if (td) {
      txtValue = td.textContent || td.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      } else {
        tr[i].style.display = "none";
      }
    }
  }
}