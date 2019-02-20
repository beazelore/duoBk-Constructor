$(document).ready(function(){
    var taskId = findGetParameter("id")
    requestResult(taskId);
    getTaskHistory(taskId);
    $('#confirmTask').on('click',function(){
        updateBookValue(taskId);
    });
    $('#saveChanges').on('click', function(){
        updateResult(taskId);
    });

    $('#result').highlightWithinTextarea({
       highlight: [
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
    $('#checkNavItem').on('click', function(){
        $('#result').highlightWithinTextarea('update');
    });
});

function requestResult(taskId){
  url = "/tasks/integrateIntoBook?id="+taskId;
 $.ajax({
        type: "GET",
        url: url,
        success: function(data, textStatus, jqXHR) {
            document.getElementById("result").value = data;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("ERROR : ", jqXHR.responseText);
        }
    });
}

function updateBookValue(taskId){
  url = "/tasks/updateBookValue?id="+taskId;
  var value = document.getElementById("result").value;
  var message = document.getElementById("message").value;
  value += "!message!" + message;
  $.ajax({
         type: "POST",
         url: url,
         contentType: "text/plain",
         data: value,
         success: function(data, textStatus, jqXHR) {
             window.location.href = "/admin/tasks";
         },
         error: function(jqXHR, textStatus, errorThrown) {
             console.log("ERROR : ", jqXHR.responseText);
         }
     });
}

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
function getTaskHistory(taskId){
  var url = "/history/task?id=" + taskId;
  $.ajax({
         type: "GET",
         url: url,
         success: function(data, textStatus, jqXHR) {
            populateHistory(data);
         },
         error: function(jqXHR, textStatus, errorThrown) {
             console.log("ERROR : ", jqXHR.responseText);
         }
     });
}
function populateHistory(taskHistory){
    var container = document.getElementById("historyContainer");
    console.log(taskHistory);
    for(var i = 0; i < taskHistory.length; i++){
        var historyItem = taskHistory[i];
        var itemContainer = document.createElement("div");
        itemContainer.setAttribute("class","container history-item");
        var h5 = document.createElement("h5");
        var h5text = historyItem.statusBefore + " -> " + historyItem.statusAfter;
        h5.innerHTML = h5text;
        itemContainer.appendChild(h5);
        var dateEl = document.createElement("p");
        dateEl.innerHTML = historyItem.moment;
        itemContainer.appendChild(dateEl);
        var descEl = document.createElement("p");
        descEl.innerHTML = historyItem.explanation;
        itemContainer.appendChild(descEl);
        var messageEl = document.createElement("p");
        messageEl.innerHTML = "Message: " + historyItem.message;
        itemContainer.appendChild(messageEl);
        var userEl = document.createElement("p");
        userEl.innerHTML = "User ID: " + historyItem.userId;
        itemContainer.appendChild(userEl);
        container.appendChild(itemContainer);
    }
}

function updateResult(taskId){
    var url = "/tasks/updateResult?id="+taskId;
    var value = document.getElementById("result").value;
    $.ajax({
              type: "POST",
              url: url,
              contentType: "text/plain",
              data: value,
              success: function(data, textStatus, jqXHR) {
                alert("success");
                //window.location.href="/admin/tasks/check?id="+taskId;
              },
              error: function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                alert("error");
              }
           });
}