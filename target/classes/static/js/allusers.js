$(document).ready(function(){
    requestUsers();
});
function requestUsers(){
 $.ajax({
        type: "GET",
        url: "/users/getAll",
        success: function(data, textStatus, jqXHR) {
            var table = document.getElementById("allUsersTable");
            populateUserTable(table,data);
            console.log(data);
            console.log(table);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function populateUserTable(table, arrayData){
        var tbody = table.getElementsByTagName('tbody')[0];
        tbody.innerHTML = "";
        for(var i =0; i < arrayData.length; i++){
            var userId = arrayData[i].id;
            var newRow = tbody.insertRow(table.length);
            var cell = newRow.insertCell(0);
            cell.innerHTML = "<a class=\"btn btn-default\" href=\"/admin/users/edit?id="+userId+"\">" +
            "<i class=\"fa fa-wrench\" ></i></a>";
            cell.setAttribute("class", "actionCell");
            cell = newRow.insertCell(1);
            cell.innerHTML = userId;
            cell.setAttribute("class", "idCell");
            cell = newRow.insertCell(2);
            cell.innerHTML = arrayData[i].mail;
            cell.setAttribute("class", "mailCell");
            cell = newRow.insertCell(3);
            cell.innerHTML = arrayData[i].userType;
            cell.setAttribute("class", "roleCell");
        }
}