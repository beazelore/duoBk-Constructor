
$(document).ready(function() {

    $.ajax({
               type: "GET",
               url: "/users/current",
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