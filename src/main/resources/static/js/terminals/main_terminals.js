window.onload = function () {
    initElements();
    addListenersToElements();
    showActiveTerminals();
    checkBox.checked = true;

};

var roleOfLoggedInUser;
var modal;
var modal_error;
var closeBtn;
var closeErrorBtn;
var okModalBtn;
var cancelModalBtn;
var checkBox;
var input_id;
var idToDelete;

function initElements() {
    roleOfLoggedInUser = document.getElementById("roleOfLoggedInUser");
    checkBox = document.getElementById("show_only_active");
    modal = document.querySelector('.modal');
    okModalBtn = document.querySelector('.ok');
    cancelModalBtn = document.querySelector('.cancel');
    closeBtn = document.querySelector('.closeButton');
    closeErrorBtn = document.querySelector('.closeButton-error');
    modal_error = document.querySelector('.modal-error');
    okErrorBtn = document.querySelector('.ok-error');
}

function addListenersToElements() {
    okModalBtn.addEventListener('click', function () {
        onOkModalClick();
    });

    cancelModalBtn.addEventListener('click', function () {
        onCancelModalClick();
    });

    okErrorBtn.addEventListener('click', function () {
        modal_error.style.display = 'none';
    });


    closeBtn.addEventListener('click', function () { //При нажатии Крестик убираем модальное окно.
        modal.style.display = 'none';
    });

    closeErrorBtn.addEventListener('click', function () { //При нажатии Крестик убираем модальное окно.
        modal_error.style.display = 'none';
    });

    checkBox.addEventListener('change', function () {
        checkCheckbox();
    });

}

function checkCheckbox() {
    if(checkBox.checked) {
        showActiveTerminals();
    } else {
        showAllTerminals();
    }
}





function showAllTerminals() {
    sendAjaxRequest("getAllTerminals", getTerminals);
}

function showActiveTerminals() {
    sendAjaxRequest("getActiveTerminals", getTerminals);
}

function getCountOfAllTerminals() {
    sendAjaxRequest("getCountOfAllTerminals", showCountOfAllUsers);
}

function getCountOfActiveTerminals() {
    sendAjaxRequest("getCountOfActiveTerminals", showCountOfActiveUsers);
}

function showCountOfAllUsers(data) {
    document.getElementById("countOfAllTerminals_container").innerHTML = "Всего: " + data.countOfAllTerminals;
}

function showCountOfActiveUsers(data) {
    document.getElementById("countOfActiveTerminals_container").innerHTML = "Активных: " + data.countOfActiveTerminals;
}



function getTerminals(data) {
    getCountOfAllTerminals();
    getCountOfActiveTerminals();

    var mainTable = document.getElementById("main_table");
    var table = "<thead><tr class=\"table_header\">\n" +
        "<th id='headerRegIdColumn'>Учетный номер</th>\n" +
        "<th id='headerModelColumn'>Модель</th>\n" +
        "<th>Серийный номер</th>\n" +
        "<th>Инв. номер</th>\n" +
        "<th id='headerCommentColumn'>Комментарий</th>\n" +
        "<th>Департамент</th>\n" +
        "<th id='headerUserLoginColumn'>Выдан пользователю</th>\n" +
        "<th id='headerActiveColumn'>Активен</th>\n" +
        "<th id='headerCreateColumn'>Дата создания</th>\n" +
        "<th id='headerUpdateColumn'>Дата изменения</th>\n";
    if(roleOfLoggedInUser.value === 'root') {
        table += "<th id=\"headerEditColumn\">Редактировать</th>\n" +
            "<th id=\"headerDeleteColumn\">Удалить</th>"
    }
    table += " </tr></thead>";
    var listOfTerminals = data.listOfTerminals;
    for(var i = 0; i < listOfTerminals.length; i++) {
        var active = listOfTerminals[i].terminalIsActive ? "Да" : "Нет";
        var department = listOfTerminals[i].department != null ? listOfTerminals[i].department.department : '';
        var lastUpdateDate = listOfTerminals[i].lastUpdateDate != null ? listOfTerminals[i].lastUpdateDate : '';
        var userReceived = listOfTerminals[i].user != null ? listOfTerminals[i].user.userLogin : '';

        table += "<tr class='row'><td id='bodyRegIdColumn' class='cell'>" + listOfTerminals[i].regId + "</td>"
            +"<td id='bodyModelColumn' class='cell'>" + listOfTerminals[i].terminalModel + "</td>"
            +"<td class='cell'>" + listOfTerminals[i].serialId + "</td>"
            +"<td class='cell'>" + listOfTerminals[i].inventoryId + "</td>"
            +"<td id='bodyCommentColumn' class='cell'>" + listOfTerminals[i].terminalComment + "</td>"
            +"<td class='cell'>" + department + "</td>"
            +"<td id='bodyUserLoginColumn' class='cell'>" + userReceived + "</td>"
            +"<td id='bodyActiveColumn' class='cell'>" + active + "</td>"
            +"<td id='bodyCreateColumn' class='cell'>" + listOfTerminals[i].createDate + "</td>"
            +"<td id='bodyUpdateColumn' class='cell'>" + lastUpdateDate + "</td>";



        if(roleOfLoggedInUser.value === 'root') {
            table += "<td id='bodyEditColumn' class='cell' style='text-align: center'>"
                + "<form class='forms' method='post' action='./update_terminal'>"

                + "<input type='hidden' name='id' value=" + listOfTerminals[i].id + ">"
                + "<input type='hidden' name='action' value='update_terminal'/>"
                + "<input class='editBtn' type='submit' value='Изменить'>"
                + "</form></td>"
                + "<td id='bodyDeleteColumn' class='cell' style='text-align: center'>"

                + "<input class='input_id_delete' type='hidden' name='id' value=" + listOfTerminals[i].id + ">"
                + "<input type='hidden' name='regIdToDelete' class='regIdToDelete' value=" + listOfTerminals[i].regId + ">"


                + "<button class='deleteBtn' onclick='modalWin(this)'>Удалить</button>"
                + "</td>";
        }
        table += "</tr>";
    }
    table += "<tfoot><tr>"
        + "<td id='table_footer' colspan='12'>"
        + "<div id='create_button'>"
        + "<form method='post' action='./add_terminal'>"
        + "<input type='hidden' name='action' value='add_terminal'/>"
        + "<input type='submit' value='Создать'>"
        + "</form></div>"
        + "<div id='generate_button'><form>"
        + "<c:set scope='session' var='listOfTerminals' value='${listOfTerminals}' />"
        + "<input type='button' value='Генерировать ШК' onClick='location.href=\"./displaybarcodes\"'>"
        + "</form></div>"
        + "</td></tr</tfoot>";
    mainTable.innerHTML = table;
    prepareForModal();
}


function sendAjaxRequest(dataToSend, callback) {
    $.ajax('./json', {
        method:'post',
        data:dataToSend,
        contentType:'text/json; charset=utf-8',
        dataType:'json',
        success:function (data) {
            callback(data);
        }
    })
}



function prepareForModal() {
    elements = document.querySelectorAll('.regIdToDelete');
    button = document.querySelectorAll('.deleteBtn'); //получаем массив классов deleteBtn (кнопки Удалить)
    input_id = document.querySelectorAll('.input_id_delete'); //получаем массив инпутов с id юзера для удаления (кнопки форма удаления)
}

function onOkModalClick() {
    deleteTerminal(idToDelete);
    modal.style.display = 'none';
}

function onCancelModalClick() {
    modal.style.display = 'none';
}

function deleteTerminal(terminalId) {
    var dataToSend = {};
    dataToSend["deleteTerminal"] = terminalId;
    dataToSend = JSON.stringify(dataToSend);
    sendAjaxRequest(dataToSend, whatToDoAfterDeleteTerminal);
}

function whatToDoAfterDeleteTerminal(data) {
    var message = data.terminalDeleteResult;
    if (message !== 'OK') {
        modal_error.style.display = 'flex';
        var errorInfoPanel = document.getElementById("modal-body-error") ;
        errorInfoPanel.innerHTML = message;
    } else {
        checkCheckbox();
    }
}


var elements;
var button;
var form;



function modalWin(buttonObj) {
    modal.style.display = 'flex';
    var count = 0;
    for (var i = 0; i < button.length; i++) { //Определяем, какая кнопка нажата
        if(button[i] == buttonObj) {
            count = i;
        }
    }
   var terminal = elements[count].getAttribute("value");
    var infoBlock = document.getElementById('modal-body');
    infoBlock.innerHTML = 'Вы действительно хотите удалить терминал <b>' + terminal + '</b>?';
    idToDelete = input_id[count].value;
}

