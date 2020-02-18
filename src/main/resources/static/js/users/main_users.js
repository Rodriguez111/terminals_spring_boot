window.onload = function () {
    initElements();
    addListenersToElements();
    showActiveUsers();
    checkBox.checked = true;
};

function initElements() {
    roleOfLoggedInUser = document.getElementById("roleOfLoggedInUser");
    checkBox = document.getElementById("show_only_active");
    modal = document.querySelector('.modal');
    okModalBtn = document.querySelector('.ok');
    cancelModalBtn = document.querySelector('.cancel');
    closeBtn = document.querySelector('.closeButton');
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

    checkBox.addEventListener('change', function () {
        checkCheckbox();
    });

}

var roleOfLoggedInUser;
var checkBox;
var elements;
var button;
var input_id;
var idToDelete;

var modal;
var closeBtn;
var okModalBtn;
var cancelModalBtn;
var modal_error;
var okErrorBtn;

function prepareForModal() {
    elements = document.querySelectorAll('.userToDelete');
    button = document.querySelectorAll('.deleteBtn'); //получаем массив классов deleteBtn (кнопки Удалить)
    input_id = document.querySelectorAll('.input_id_delete'); //получаем массив инпутов с id юзера для удаления (кнопки форма удаления)
}

function onOkModalClick() {
    deleteUser(idToDelete);
    modal.style.display = 'none';
}

function onCancelModalClick() {
    modal.style.display = 'none';
}


function modalWin(buttonObj) {
    modal.style.display = 'flex';
    var count = 0;
    for (var i = 0; i < button.length; i++) { //Определяем, какая кнопка нажата
        if (button[i] == buttonObj) {
            count = i;
        }
    }
    var user = elements[count].getAttribute("value");
    var infoBlock = document.getElementById('modal-body');
    infoBlock.innerHTML = 'Вы действительно хотите удалить пользователя <b>' + user + '</b>?';

    idToDelete = input_id[count].value;

}


function checkCheckbox() {
    if (checkBox.checked) {
        showActiveUsers();
    } else {
        showAllUsers();
    }
}


function showAllUsers() {
    let url = "./users_controller/all";
    sendAjaxRequest(url, "get", "", getUsers);
}

function showActiveUsers() {
    let url = "./users_controller/active";
    sendAjaxRequest(url, "get", "", getUsers);

}

function getCountOfAllUsers() {
    let url = "./users_controller/countall";
    sendAjaxRequest(url, "get", "", showCountOfAllUsers);

}

function getCountOfActiveUsers() {
    let url = "./users_controller/countactive";
    sendAjaxRequest(url, "get", "", showCountOfActiveUsers);
}

function showCountOfAllUsers(data) {
    document.getElementById("countOfAllUsers_container").innerHTML = "Всего: " + data;
}

function showCountOfActiveUsers(data) {
    document.getElementById("countOfActiveUsers_container").innerHTML = "Активных: " + data;
}


function getUsers(listOfUsers) {
    getCountOfAllUsers();
    getCountOfActiveUsers();
    var mainTable = document.getElementById("main_table");
    var table = "<thead><tr class=\"table_header\">\n" +
        "<th id=\"headerLoginColumn\">Логин</th>\n" +
        "<th>Фамилия и имя</th>\n" +
        "<th id='headerRoleColumn'>Роль</th>\n" +
        "<th>Департамент</th>\n" +
        "<th id='headerTermRegIdColumn'>Выдан терминал</th>\n" +
        "<th id='headerActiveColumn'>Активен</th>\n" +
        "<th id='headerCreateColumn'>Дата создания</th>\n" +
        "<th id='headerUpdateColumn'>Дата изменения</th>\n" +
        "<th id=\"headerEditColumn\">Редактировать</th>\n" +
        "<th id=\"headerDeleteColumn\">Удалить</th>";

    table += " </tr></thead>";

    for (var i = 0; i < listOfUsers.length; i++) {
        var active = listOfUsers[i].active ? "Да" : "Нет";
        var department = listOfUsers[i].department != null ? listOfUsers[i].department.department : '';
        var lastUpdateDate = listOfUsers[i].lastUpdateDate != null ? listOfUsers[i].lastUpdateDate : '';
        var terminalGiven = listOfUsers[i].terminal != null ? listOfUsers[i].terminal.regId : '';

        table += "<tr class='row'><td id='bodyLoginColumn' class='cell'>" + listOfUsers[i].userLogin + "</td>"
            + "<td class='cell'>" + listOfUsers[i].userSurname + " " + listOfUsers[i].userName + "</td>"
            + "<td id='bodyRoleColumn' class='cell'>" + listOfUsers[i].role.role + "</td>"
            + "<td class='cell'>" + department + "</td>"
            + "<td id='bodyTermRegIdColumn' class='cell'>" + terminalGiven + "</td>"
            + "<td id='bodyActiveColumn' class='cell'>" + active + "</td>"
            + "<td id='bodyCreateColumn' class='cell'>" + listOfUsers[i].createDate + "</td>"
            + "<td id='bodyUpdateColumn' class='cell'>" + lastUpdateDate + "</td>";

        table += "<td id='bodyEditColumn' class='cell' style=\"text-align: center\">";
        if (listOfUsers[i].role.role !== 'root' || roleOfLoggedInUser.value === 'root') {

            table += "<form class='forms' method='post' action='./update_user'>"
                + "<input type='hidden' name='id'  value='" + listOfUsers[i].id + "'/>"
                + "<input class='editBtn' type='submit' value='Изменить'>"
                + "</form>";
        }

        table += "</td>";

        table += "<td id='bodyDeleteColumn' class='cell' style='text-align: center'>";
        if (listOfUsers[i].role.role !== 'root') {
            table += "<input class='input_id_delete' type='hidden' name='id' value='" + listOfUsers[i].id + "'/>"
                + "<input type='hidden' name='loginToDelete' class='userToDelete' value='" + listOfUsers[i].userLogin + "'/>"
                + "<input type='hidden' name='action' value='delete_user'/>"
                + "<button class='deleteBtn' onclick='modalWin(this)'>Удалить</button>";

        }
        table += "</td>";

        table += "</tr>";
    }
    table += "<tfoot><tr>"

        + "<td id='table_footer'  colspan='12'>"
        + "<div id='create_button'>"
        + "<form method='post' action='./add_user'>"
        + "<input type='hidden' name='action' value='add_user'/>"
        + "<input type='submit' value='Создать'>"
        + "</form></div>"
        + "</td></tr></tfoot>";
    mainTable.innerHTML = table;
    prepareForModal();
}


function deleteUser(userId) {
    let url = "./users_controller/" + userId;
    sendAjaxRequest(url, "delete", "", whatToDoAfterDeleteUser);
}

function whatToDoAfterDeleteUser(data) {
    var message = data.userDeleteResult;
    if (message !== 'OK') {
        modal_error.style.display = 'flex';
        var errorInfoPanel = document.getElementById("modal-body-error");
        errorInfoPanel.innerHTML = message;
    } else {
        checkCheckbox();
    }
}

function sendAjaxRequest(url, method, dataToSend, callback) {
    $.ajax(url, {
        method: method,
        data: dataToSend,
        contentType: "text/json",
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            callback(data);
        }
    })
}

