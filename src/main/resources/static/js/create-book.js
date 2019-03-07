$(document).ready(function() {
    requestAuthors();
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
                 alert("file is too large. Should be less than 500 KB");
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
                window.location.href="/admin/books";
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("error, check console for details");
                console.log("ERROR : ", jqXHR.responseText);
            }
        });
    });
});

function requestAuthors(){
 $.ajax({
        type: "GET",
        url: "/authors/getAll",
        success: function(data, textStatus, jqXHR) {
            var select = document.getElementById("authorpicker");
            populateSelect(select, data);
            $('.selectpicker').selectpicker('refresh');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}

function populateSelect(select, arrayData){
    for(var i =0; i < arrayData.length; i++){
        var option = document.createElement("option");
        option.text = arrayData[i].name;
        option.setAttribute("value", arrayData[i].id);
        select.add(option);
    }
}