
$(document).ready(function() {

    $.ajax({
               type: "GET",
               url: "/user",
               success: function(data, textStatus, jqXHR) {
                        console.log(data);
                        $('#navbarDropdownMenuUser').html(data.userAuthentication.details.name);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                        alert("error");
                        console.log(jqXHR);
                }
    });

});