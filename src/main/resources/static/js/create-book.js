$(document).ready(function() {

    $("#submitBook").click(function(event){
        event.preventDefault();
        var fileInput = $("#image")[0];
        if(fileInput.value != ""){
             var imageFile = fileInput.files[0];
             var ext = imageFile.name.split('.').pop().toLowerCase();
             if(ext != "jpg" && ext != "jpeg"){
                alert("wrong image format");
                return;
             }
             if(imageFile.size > 500000){
                 alert("file is too large. Should be less than 1 MB");
                 return;
             }
        }
        // Get form
        var form = $('#bookCreateForm')[0];
        var data = new FormData(form);
        console.log(data);
        $("#submitBook").prop("disabled", true);
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "/books/create",
            data: data,
            // prevent jQuery from automatically transforming the data into a query string
            processData: false,
            contentType: false,
            cache: false,
            timeout: 1000000,
            success: function(textStatus, jqXHR) {
                console.log("SUCCESSSSS");
                window.location.href="/admin/books";
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("ERROR : ", jqXHR.responseText);
            }
        });
    });
});