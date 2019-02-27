
$(document).ready(function() {

    $.ajax({
               type: "GET",
               url: "/users/currentName",
               success: function(data, textStatus, jqXHR) {
                    $('#navbarDropdownMenuUser').html(data);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    alert("error, check console for details");
                    console.log("ERROR : ", jqXHR.responseText);
                }
    });

});