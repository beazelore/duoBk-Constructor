$(document).ready(function(){
    var bookId = findGetParameter("id");
    var img = document.getElementById("bookImage");
    img.setAttribute("src","/books/getImage?id=" + bookId);
    getBookInfoAjax(bookId);

    $("#submitBookInfo").on('click',function(){
        event.preventDefault();
        submitFormData(bookId);
    });
    $("#deleteBook").on('click',function(){
        deleteBook(bookId);
    });

    $('#book-value').highlightWithinTextarea({
       highlight:
       [
          {
              highlight: /(<dp|<\/dp>)/g,
              className: 'red'
          },
          {
              highlight: /(<ds>|<\/ds>)/g,
              className: 'green'
          },
          {
              highlight: /(?<=<s1.+?>)(.+?)(?=<)/g,
              className: 'blue'
          },
          {
              highlight: /(?<=<s .+?>)(.+?)(?=<)/g,
              className: 'yellow'
          }
       ]
    });
    $('#value-tab').on('click', function(){
        $('#book-value').highlightWithinTextarea('update');
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

function getBookInfoAjax(bookId){
    var url = "/books/getById?id=" + bookId;
    $.ajax({
           type: "GET",
           url: url,
           success: function(data, textStatus, jqXHR) {
               console.log(data);
               document.getElementById("name").setAttribute("value",data.name);
               document.getElementById("book-value").innerHTML=data.book;
               $('#statuspicker').selectpicker('val', data.status);
           },
           error: function(jqXHR, textStatus, errorThrown) {
               alert("error, check console for details");
               console.log("ERROR : ", jqXHR.responseText);
           }
       });
}

function submitFormData(bookId){
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
    console.log(imageFile);
    var form = $('#infoForm')[0];
    var data = new FormData(form);
    data.append("id",bookId);
    var value = document.getElementById("book-value").value;
    data.append("book", value);


    $.ajax({
        type: "POST",
        url: "/books/update",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 1000000,
        success: function(textStatus, jqXHR) {
            window.location.href = "/admin/books";
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });


}
function deleteBook(bookId){
    $.ajax({
        type: "DELETE",
        url: "/books/delete",
        contentType: "text/plain",
        data: bookId,
        success: function(textStatus, jqXHR) {
            window.location.href = "/admin/books";
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("error, check console for details");
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}