$(document).ready(function(){
    var authorId = findGetParameter("id");
    var img = document.getElementById("authorImage");
    img.setAttribute("src","/authors/image?id=" + authorId);
    getAuthorInfoAjax(authorId);

    $("#submitAuthorInfo").on('click',function(){
        event.preventDefault();
        submitFormData(authorId);
    });
    $("#deleteAuthor").on('click',function(){
        deleteAuthor(authorId);
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

function getAuthorInfoAjax(authorId){
    var url = "/authors/getById?id=" + authorId;
    $.ajax({
           type: "GET",
           url: url,
           success: function(data, textStatus, jqXHR) {
               document.getElementById("name").setAttribute("value",data.name);
               var date = new Date(data.birthDate);
               console.log(date);
               var birthString = ("0" + date.getDate()).slice(-2).toString() + "." + ("0" + (date.getMonth() + 1)).slice(-2) +"."+ date.getFullYear();
               console.log(birthString);
               var date = new Date(data.deathDate);
               var deathString = ("0" + date.getDate()).slice(-2).toString() + "." + ("0" + (date.getMonth() + 1)).slice(-2) +"."+ date.getFullYear();
               document.getElementById("birth").setAttribute("value",birthString);
               document.getElementById("death").setAttribute("value",deathString);
               document.getElementById("biographyArea").innerHTML=data.biography;
           },
           error: function(jqXHR, textStatus, errorThrown) {
               alert("error, check console for details");
               console.log("ERROR : ", jqXHR.responseText);
           }
       });
}

function submitFormData(authorId){
    var fileInput = $("#image")[0];
    if(fileInput.value != ""){
         var imageFile = fileInput.files[0];
         var ext = imageFile.name.split('.').pop().toLowerCase();
         if(ext != "jpg" && ext != "jpeg"){
            alert("wrong image format");
            return;
         }
         if(imageFile.size > 1000000){
             alert("file is too large. Should be less than 1MB");
             return;
         }
    }
    var form = $('#infoForm')[0];
    var data = new FormData(form);
    var biography = document.getElementById("biographyArea").value;
    data.append("title1", biography);

    var url = "/authors/update?id=" + authorId;
    $.ajax({
        type: "POST",
        url: url,
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function(textStatus, jqXHR) {
            window.location.href = "/admin/authors";
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });


}
function deleteAuthor(authorId){
    $.ajax({
        type: "DELETE",
        url: "/authors/delete",
        contentType: "text/plain",
        data: authorId,
        success: function(textStatus, jqXHR) {
            window.location.href = "/admin/authors";
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if(jqXHR.status == 406){
                alert(jqXHR.responseText);
                return;
            }
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}