$(document).ready(function() {

    $("#submitBook").click(function(event){
        event.preventDefault();
        var name = $("#name")[0].value;
            $.ajax({
                type: "POST",
                url: "/books/create",
                data: name,
                contentType: "text/plain",
                success: function(textStatus, jqXHR) {
                    console.log("/createBook SUCCESS");
                    window.location.href="/tasks";
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    alert("ERROR. Check console for details");
                    console.log("ERROR : ", jqXHR.responseText);
                }
            });
    });

});