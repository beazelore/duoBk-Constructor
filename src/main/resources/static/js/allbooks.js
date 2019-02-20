$(document).ready(function(){
    requestBooks();


    $('#allBooksTable th.idCell').on('click', function(){
        var table = document.getElementById("allBooksTable");
        sortTable(table, 1);
    });
    $('#allBooksTable th.statusCell').on('click', function(){
        var table = document.getElementById("allBooksTable");
        sortTable(table, 2);
    });
    $('#allBooksTable th.nameCell').on('click', function(){
        var table = document.getElementById("allBooksTable");
        sortTable(table, 3);
    });

});
function requestBooks(){
 $.ajax({
        type: "GET",
        url: "/books/getAll",
        success: function(data, textStatus, jqXHR) {
            var table = document.getElementById("allBooksTable");
            populateTaskTable(table,data);
            console.log(data);
            console.log(table);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function populateTaskTable(table, arrayData){
        var tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = "";
        for(var i =0; i < arrayData.length; i++){
            var bookId = arrayData[i].id;
            var newRow = tbody.insertRow(table.length);
            var cell = newRow.insertCell(0);
            cell.innerHTML = "<a class=\"btn btn-default\" href=\"/admin/books/edit?id="+bookId+"\">" +
            "<i class=\"fa fa-wrench\" ></i></a>";
            cell.setAttribute("class", "actionCell");
            cell = newRow.insertCell(1);
            cell.innerHTML = bookId;
            cell.setAttribute("class", "idCell");
            cell = newRow.insertCell(2);
            cell.innerHTML = arrayData[i].status;
            cell.setAttribute("class", "statusCell");
            cell = newRow.insertCell(3);
            cell.innerHTML = arrayData[i].name;
            cell.setAttribute("class", "nameCell");
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
        if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
          // If so, mark as a switch and break the loop:
          shouldSwitch = true;
          break;
        }
      } else if (dir == "desc") {
         if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
           // If so, mark as a switch and break the loop:
           shouldSwitch = true;
           break;
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