

window.onload= function (){
    var buttonOn = document.querySelector('#filter_on_button');
    var buttonOff = document.querySelector('#filter_off_button');
    var apply = document.querySelector('#apply_filter_button');
    var reset = document.querySelector('#reset_filter_button');
    var filterPanel = document.querySelector('.table_filter');




    resetTrianglesCondition();


    getAllEntries();


    buttonOn.addEventListener('click', function () { //При нажатии Включить фильтр.
        filterPanel.style.display = 'table-row';
        buttonOff.style.display = 'block';
        apply.style.display = 'block';
        buttonOn.style.display = 'none';
        reset.style.display = 'none';
    });

    buttonOff.addEventListener('click', function () { //При нажатии Вылючить фильтр.
        filterPanel.style.display = 'none';
        buttonOff.style.display = 'none';
        apply.style.display = 'none';
        buttonOn.style.display = 'block';
        reset.style.display = 'block';
    });
    apply.addEventListener('click', function () { //При нажатии Вылючить фильтр.
        getEntriesByFilter();
    });




    function composeJsonForQuery() {
        var regIdFilter = document.getElementById('regIdFilter');
        var loginFilter = document.getElementById('loginFilter');
        var fullNameFilter = document.getElementById('fullNameFilter');
        var whoGaveFilter = document.getElementById('whoGaveFilter');
        var whoReceivedFilter = document.getElementById('whoReceivedFilter');
        var startDateFilterFrom = document.getElementById('startDateFilterFrom');
        var startDateFilterTo = document.getElementById('startDateFilterTo');
        var endDateFilterFrom = document.getElementById('endDateFilterFrom');
        var endDateFilterTo = document.getElementById('endDateFilterTo');

        var dataToSend = {};
        dataToSend["filterSelect"] = {};
        if (regIdFilter.value !== "") {
            dataToSend["filterSelect"]["regIdFilter"] = regIdFilter.value;
        }
        if(loginFilter.value !== "") {
            dataToSend["filterSelect"]["loginFilter"] = loginFilter.value;
        }
        if(fullNameFilter.value !== "") {
            dataToSend["filterSelect"]["fullNameFilter"] = fullNameFilter.value;
        }
        if(whoGaveFilter.value !== "") {
            dataToSend["filterSelect"]["whoGaveFilter"] = whoGaveFilter.value;
        }
        if(whoReceivedFilter.value !== "") {
            dataToSend["filterSelect"]["whoReceivedFilter"] = whoReceivedFilter.value;
        }
        if(startDateFilterFrom.value !== "") {
            dataToSend["filterSelect"]["startDateFilterFrom"] = startDateFilterFrom.value;
        }
        if(startDateFilterTo.value !== "") {
            dataToSend["filterSelect"]["startDateFilterTo"] = startDateFilterTo.value;
        }
        if(endDateFilterFrom.value !== "") {
            dataToSend["filterSelect"]["endDateFilterFrom"] = endDateFilterFrom.value;
        }
        if(endDateFilterTo.value !== "") {
            dataToSend["filterSelect"]["endDateFilterTo"] = endDateFilterTo.value;
        }
        return JSON.stringify(dataToSend);
    }


    function getAllEntries() {
        sendAjaxRequest("./mainpage", "getAllRegs", showEntries)
    }

    function getEntriesByFilter() {
        var jsonToServer = composeJsonForQuery();
        sendAjaxRequest("./mainpage", jsonToServer, showEntries)
    }





};

var currentListOfEntries;

var twoTriangles = document.getElementsByClassName("two_triangles");
var triangleDown = document.getElementsByClassName("triangle_down");
var triangleUp = document.getElementsByClassName("triangle_up");

function sendAjaxRequest(url, dataToSend, callback) {
    $.ajax(url, {
        method:'post',
        data:dataToSend,
        contentType:'text/json; charset=utf-8',
        dataType:'json',
        success:function (data) {
            callback(data);
        }
    })
}

function showEntries(allRegs) {
    currentListOfEntries = allRegs;
    var tableBody = document.getElementById("table_body");
    var rows = "";
    for (var i = 0; i < allRegs.length; i++) {
        var adminGot = allRegs[i].adminGot != null ? allRegs[i].adminGot.fullName : '';
        var endDate = allRegs[i].endDate != null ? allRegs[i].endDate : '';
        rows += "<tr class=\"row\"><td class=\"cell\">" + allRegs[i].terminal.regId +
            "</td><td class=\"cell\">" + allRegs[i].user.userLogin +
            "</td><td class=\"cell\">" + allRegs[i].user.fullName +
            "</td><td class=\"cell\">" + allRegs[i].adminGaveOut.fullName +
            "</td><td class=\"cell\">" +  adminGot +
            "</td><td class=\"cell\">" + allRegs[i].startDate +
            "</td><td class=\"cell\">" + endDate + "</td>";
    }
    tableBody.innerHTML = rows;
}


function makeSortDown(element) {
    resetTrianglesCondition();
    var triangles_block = element.parentNode;
    var two_triangles = triangles_block.querySelector('.two_triangles');
    var triangle_down = triangles_block.querySelector('.triangle_down');
    var triangle_up = triangles_block.querySelector('.triangle_up');

    two_triangles.style.display = 'none';
    triangle_down.style.display = 'block';
    triangle_up.style.display = 'none';
    var sortWhat = triangles_block.id;
    var dataToSend = createJsonForSort(sortWhat, "down");
    sendAjaxRequest("./mainpage", dataToSend, showEntries)
}

function makeSortUp(element) {
    resetTrianglesCondition();
    var triangles_block = element.parentNode;
    var two_triangles = triangles_block.querySelector('.two_triangles');
    var triangle_up = triangles_block.querySelector('.triangle_up');
    var triangle_down = triangles_block.querySelector('.triangle_down');

    two_triangles.style.display = 'none';
    triangle_up.style.display = 'block';
    triangle_down.style.display = 'none';
    var sortWhat = triangles_block.id;
    var dataToSend = createJsonForSort(sortWhat, "up");
    sendAjaxRequest("./mainpage", dataToSend, showEntries)
}


function resetTrianglesCondition() {
    for (var i = 0; i < twoTriangles.length; i++) {
        var buttonId = triangleDown[i].parentNode.id;
        twoTriangles[i].style.display = 'block';
        triangleDown[i].style.display = 'none';
        triangleUp[i].style.display = 'none';

    }
}


function createJsonForSort(whatToSort, upOrDown) {

    var dataToSend = {};
    dataToSend["sortEntries"] = {};
    dataToSend["sortEntries"]["whatToSort"] = whatToSort;
    dataToSend["sortEntries"]["upOrDown"] = upOrDown;
    dataToSend["listOfEntries"] = {};
    for (var i = 0; i < currentListOfEntries.length; i++) {
        dataToSend["listOfEntries"]["entry" + i] = currentListOfEntries[i];
    }
    return JSON.stringify(dataToSend);
}











