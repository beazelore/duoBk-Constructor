$(document).ready(function(){
    requestBooks();
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