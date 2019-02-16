$(document).ready(function(){
    var userId = findGetParameter("id");
    getUserInfoAjax(userId);

    $(":submit").on('click',function(){
        event.preventDefault();
        submitFormData(userId);
    });
});

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

function getUserInfoAjax(userId){
    var url = "/users/getById?id=" + userId;
     $.ajax({
            type: "GET",
            url: url,
            success: function(data, textStatus, jqXHR) {
                console.log(data);
                document.getElementById("roleLabel").innerHTML = "Role for "+ data.mail;
                document.getElementById("role").value =data.userType;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("ERROR : ", jqXHR.responseText);
            }
        });
    }

function submitFormData(userId){
    var form = $('#userForm')[0];
    var data = new FormData(form);
    data.append("id", userId);
    $.ajax({
        type: "POST",
        url: "/users/update",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function(textStatus, jqXHR) {
            console.log("SUCCESSSSS");
            window.location.href = "/admin/users";
                    },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });

}