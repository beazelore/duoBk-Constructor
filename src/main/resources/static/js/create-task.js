$(document).ready(function() {

    requestBooks();
    $("#submitfiles").click(function(event) {
        // Stop default form Submit.
        event.preventDefault();
        //get file names
        var file1 = $("#file1")[0].value;
        var file2 = $("#file2")[0].value;
        console.log(file1);
        console.log(file2);
        //check extension
        var ext1 = file1.split('.').pop();
        var ext2 = file1.split('.').pop();
        var lang1 = $("#lang1").value;
        var lang2 = $("#lang2").value;
        var author1 = $("#author1").value;
        var author1 = $("#author2").value;
        var title1 = $("#title1").value;
        var title1 = $("#title2").value;
        var option = $("select option:selected")[0];
        var status = option.getAttribute("status");
        var re = new RegExp("epub|fb2", "i");

        if (status == "NEW"){
            if (!re.test(file1)){
                alert("wrong file1 extension " + ext1);
                return;
            }
            if (!re.test(file2)){
                alert("wrong file2 extension " + ext2);
                return;
            }
            if (lang1 == "" || lang2 == ""){
            alert("empty language");
            return;
            }
            if(author1 =="" || author2 ==""){
                alert("empty author");
                return;
            }
            if(title1 =="" || title2 ==""){
                alert("empty title");
                return;
            }
        }
        else{
            if (!re.test(file1)){
                alert("wrong file1 extension " + ext1);
                return;
            }
            if (lang1 == ""){
            alert("empty language");
            return;
            }
            if(author1 ==""){
                alert("empty author");
                return;
            }
            if(title1 ==""){
                alert("empty title");
                return;
            }
        }
        ajaxSubmitForm(status);

    });
    $( "#bookpicker" ).change(function() {
        console.log( "Handler for .change() called." );
        var option = $("select option:selected")[0];
        var status = option.getAttribute("status");
        //var status = $("select option:selected")[0].attr("status");
        console.log(option);
        console.log(status);
        if(status == "NEW"){
            $("#form-row1")[0].classList.remove("gone");
            $("#form-row2")[0].classList.remove("gone");
            $("#form-row3")[0].classList.remove("gone");
        }
        else if(status == "FIRST_PROCESS"){
            alert("Chosen book has status \"FIRST_PROCESS\", tasks can't be added to this book.")
            $("#form-row1")[0].classList.add("gone");
            $("#form-row2")[0].classList.add("gone");
            $("#form-row3")[0].classList.add("gone");
        }
        else{
            $("#form-row1")[0].classList.remove("gone");
            var row2 = $("#form-row2")[0];
            $("#form-row2")[0].classList.add("gone");
            $("#form-row3")[0].classList.remove("gone");
        }
    });




});

function ajaxSubmitForm(bookStatus) {
    // Get form
    var form = $('#fileUploadForm')[0];

    var data = new FormData(form);
    data.append("bookStatus", bookStatus)
    console.log(data);

    $("#submitButton").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/tasks/create",
        data: data,
        // prevent jQuery from automatically transforming the data into a query string
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function(textStatus, jqXHR) {
            //console.log(data);
            //sessionStorage.setItem('data', data);
            //$("#result").html(data);
            console.log("SUCCESSSSS");
            window.location.href="/admin/tasks";
            //$("#submitButton").prop("disabled", false);
            //$('#fileUploadForm')[0].reset();
            //window.location.href = "/tasks/create/new"
            //console.log("after redirect");
            //console.log(data);
                    },
        error: function(jqXHR, textStatus, errorThrown) {

            //$("#result").html(jqXHR.responseText);
            console.log("ERROR : ", jqXHR.responseText);
            //$("#submitButton").prop("disabled", false);

        }
    });

}
function requestBooks(){
 $.ajax({
        type: "GET",
        url: "/books/getAll",
        success: function(data, textStatus, jqXHR) {
            console.log(data);
            var select = document.getElementById("bookpicker");
            populateSelect(select, data);
             $('.selectpicker').selectpicker('refresh');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}
function populateSelect(select, arrayData){
    for(var i =0; i < arrayData.length; i++){
        var option = document.createElement("option");
        option.text = arrayData[i].name;
        option.setAttribute("value", arrayData[i].id);
        option.setAttribute("status", arrayData[i].status);
        select.add(option);
    }
}

