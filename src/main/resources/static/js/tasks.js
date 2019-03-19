
$(document).ready(function() {

    populateMyTasksAjax();
    $.ajax({
               type: "GET",
               url: "/tasks/getTaskPool",
               dataType: "json",
               success: function(data, textStatus, jqXHR) {
                    var table = document.getElementById("alltasksTable");
                    populateTaskTable(table,data, true);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    alert("error, check console for details");
                    console.log("ERROR : ", jqXHR.responseText);
                }
    });

    $('.table').on('click', '.dotaskbtn', function (){
        var id = this.value;
        $.ajax({
             type: "POST",
             data: id,
             contentType: "text/plain",
             url: "/tasks/take",
             success: function(){
                var btnId = "pull"+id;
                var btn = document.getElementById(btnId);
                var i = btn.parentNode.parentNode.rowIndex;
                document.getElementById("alltasksTable").deleteRow(i);
                populateMyTasksAjax();
             }
        });
    });

    $('#mytasksTable th.statusCell').on('click', function(){
        var table = document.getElementById("mytasksTable");
        sortTable(table, 1);
    });
    $('#mytasksTable th.nameCell').on('click', function(){
        var table = document.getElementById("alltasksTable");
        sortTable(table, 2);
    });
    $('#alltasksTable th.timeCell').on('click', function(){
        var table = document.getElementById("alltasksTable");
        sortTable(table, 3);
    });
    $('#alltasksTable th.statusCell').on('click', function(){
        var table = document.getElementById("alltasksTable");
        sortTable(table, 1);
    });
    $('#alltasksTable th.nameCell').on('click', function(){
        var table = document.getElementById("alltasksTable");
        sortTable(table, 2);
    });
    $('#alltasksTable th.timeCell').on('click', function(){
        var table = document.getElementById("alltasksTable");
        sortTable(table, 3);
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
                   },
                   error: function(jqXHR, textStatus, errorThrown) {
                       alert("error, check console for details");
                       console.log("ERROR : ", jqXHR.responseText);
                   }
       });
}
function populateTaskTable(table, arrayData, onlyDo){
        var tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = "";
        for(var i =0; i < arrayData.length; i++){
            var taskId = arrayData[i][0];
            var status = arrayData[i][2];
            var newRow = tbody.insertRow(table.length);
            var cell = newRow.insertCell(0);
            if(onlyDo == true)
              cell.innerHTML = "<button class=\"btn btn-primary dotaskbtn\" href=\"#\" " +
              " id=\"" + "pull"+taskId+"\" "+"value=\""+taskId+"\">"+
              "<i class=\"fa fa-briefcase\" ></i>  Do</button>"
            else{
                if(status == "CHECK_NEEDED"){
                     cell.innerHTML = "<a class=\"btn btn-default checkTaskbtn\" href=\"/admin/tasks/check?id="+taskId+"\" value=\""+taskId+"\""+"id=\""+"checkTask"+taskId+"\">"+
                                       "<i class=\"fa fa-check\" aria-hidden=\"true\"></i></a>";
                }
                else{
                     cell.innerHTML =  "<a class=\"btn btn-default setIndexesbtn\" href=\"/tasks/preProcess?id="+taskId+"\" "+"value=\""+taskId+"\""+"id=\""+"setIndexes"+taskId+"\">"+
                                       "<i class=\"fa fa-cog\" aria-hidden=\"true\"></i></a>"+
                                       "<a class=\"btn btn-default processbtn\" href=\"/tasks/process?id="+taskId+"\" "+"value=\""+taskId+"\""+"id=\""+"process"+taskId+"\">"+
                                       "<i class=\"fa fa-wrench\" aria-hidden=\"true\"></i></a>"+
                                       "<a class=\"btn btn-default submitTaskbtn\" href=\"/tasks/submit?id="+taskId+"\" value=\""+taskId+"\""+"id=\""+"submitTask"+taskId+"\">"+
                                       "<i class=\"fa fa-check\" aria-hidden=\"true\"></i></a>";
                }
            }

            cell.setAttribute("class", "actionCell");
            cell = newRow.insertCell(1);
            cell.innerHTML = status;
            cell.setAttribute("class", "statusCell");
            cell = newRow.insertCell(2);
            cell.innerHTML = arrayData[i][1];
            cell.setAttribute("class", "nameCell");
            cell = newRow.insertCell(3);
            var date = new Date(arrayData[i][3]);
            cell.innerHTML = date.toLocaleString();
            cell.setAttribute("class", "timeCell");
        }
}

function htmlToElement(html) {
    var template = document.createElement('template');
    html = html.trim(); // Never return a text node of whitespace as the result
    template.innerHTML = html;
    return template.content.firstChild;
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
        if(n==1 || n==2 ){
            // that are status or name cells that we have to compare
            if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
              // If so, mark as a switch and break the loop:
              shouldSwitch = true;
              break;
            }
        }
        else if(n==3){
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
        if(n==1 || n==2 ){
            // that are status or name cells that we have to compare
            if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
              // If so, mark as a switch and break the loop:
              shouldSwitch = true;
              break;
            }
        }
        else if(n==3){
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