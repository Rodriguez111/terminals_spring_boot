window.onload = function () {
    okCreateButton  = document.getElementById("okCreateButton");
    cancelCreateButton  = document.getElementById("cancelCreateButton");
    okRenameButton = document.getElementById("okRenameButton");
    cancelRenameButton = document.getElementById("cancelRenameButton");

    modal = document.querySelector('.modal');
    okModalBtn = document.querySelector('.ok');
    cancelModalBtn = document.querySelector('.cancel');
    closeBtn = document.querySelector('.closeButton');
    addListeners();

};

var departToRename;
var cellWithDepartToUpdate;
var departNewName;
var okCreateButton;
var cancelCreateButton;
var okRenameButton;
var cancelRenameButton;


var modal;
var closeBtn;
var okModalBtn;
var cancelModalBtn;


showAllDepartments();

function showAllDepartments() {
    sendAjaxRequest("./departments_controller",'get', '',  getDepartments);
}

function getDepartments(departments) {
    var mainTable = document.getElementById("main_table_body");
    mainTable.innerHTML = "";
    for (i = 0; i < departments.length; i++) {
        var row = document.createElement("TR");
        row.setAttribute("class", "row");
        mainTable.appendChild(row);
        var cellWithDepartmentName = document.createElement("TD");
        cellWithDepartmentName.setAttribute("class", "department_name_cell");
        cellWithDepartmentName.innerHTML = departments[i].department;
        row.appendChild(cellWithDepartmentName);

        var cellWithRenameButton = document.createElement("TD");
        cellWithRenameButton.setAttribute("class", "cell");
        cellWithRenameButton.setAttribute("id", "bodyUpdateColumn");
        var inputFieldForDepartmentName = document.createElement("INPUT");
        inputFieldForDepartmentName.setAttribute("type", "hidden");
        inputFieldForDepartmentName.setAttribute("name", "department");
        inputFieldForDepartmentName.setAttribute("value", departments[i].department);
        cellWithRenameButton.appendChild(inputFieldForDepartmentName);
        var renameButton = document.createElement("INPUT");
        renameButton.setAttribute("type", "button");
        renameButton.setAttribute("class", "updateBtn");
        renameButton.setAttribute("value", "Переименовать");
        renameButton.setAttribute('onclick', 'update(this)');
        cellWithRenameButton.appendChild(renameButton);
        row.appendChild(cellWithRenameButton);

        var cellWithDeleteButton = document.createElement("TD");
        cellWithDeleteButton.setAttribute("class", "cell");
        cellWithDeleteButton.setAttribute("id", "bodyDeleteColumn");
        cellWithDeleteButton.appendChild(inputFieldForDepartmentName);
        var deleteButton = document.createElement("INPUT");
        deleteButton.setAttribute("type", "button");
        deleteButton.setAttribute("class", "deleteBtn");
        deleteButton.setAttribute("value", "Удалить");
        deleteButton.setAttribute('onclick', 'modalWin(this)');
        cellWithDeleteButton.appendChild(deleteButton);
        row.appendChild(cellWithDeleteButton);
    }
}

function create() {
    clearSelection();
    clearCreateContainer();
    clearRenameContainer();
    showAndFocusCreateField()


}

function update(element) {
    clearSelection();
    clearCreateContainer();
    clearRenameContainer();
    showRenameDialog(element);
    departToRename = getDepartNameForRenaming(element);
}

function showRenameDialog(element) {
    var row = element.parentElement.parentElement;
    row.style.backgroundColor = "#abb5c0";
    showAndFocusRenameField();
}

function showAndFocusRenameField() {
    var renameContainer = document.getElementById("update_container");
    var fieldSignature = document.getElementById("rename_field_signature");
    fieldSignature.innerText = "Переименовать в: ";
    renameContainer.style.display = "block";
    var inputField = document.getElementById("renameInput");
    inputField.focus();
}

function showAndFocusCreateField() {
    var createContainer = document.getElementById("create_container");
    var fieldSignature = document.getElementById("create_field_signature");
    fieldSignature.innerText = "Новый департамент: ";
    createContainer.style.display = "block";
    var inputField = document.getElementById("createInput");
    inputField.focus();
}



function clearSelection() {
    var allRows = document.getElementsByClassName("row");
    for (var i = 0; i < allRows.length; i++) {
        allRows[i].style.backgroundColor = "";
    }
}

function clearCreateContainer() {
    var inputField = document.getElementById("createInput");
    inputField.value = "";
    var inputContainer = document.getElementById("create_container");
    inputContainer.style.display = "none";
    clearInfoContainer();
}

function clearRenameContainer() {
    var inputField = document.getElementById("renameInput");
    inputField.value = "";
    var inputContainer = document.getElementById("update_container");
    inputContainer.style.display = "none";
    clearInfoContainer();
}

function clearInfoContainer() {
    var infoContainer = document.getElementById("info_container");
    infoContainer.innerHTML = "";
}

function getDepartNameForRenaming(element) {
    var arrayOfUpdateButtons = document.getElementsByClassName("updateBtn");
    var counterOfButtons = 0;
    for (var i = 0; i < arrayOfUpdateButtons.length; i++) {
        if (element === arrayOfUpdateButtons[i]) {
            counterOfButtons = i;
            break;
        }
    }
    var arrayOfDepartmentNameCells = document.getElementsByClassName("department_name_cell");
    cellWithDepartToUpdate = arrayOfDepartmentNameCells[counterOfButtons];
    return arrayOfDepartmentNameCells[counterOfButtons].innerHTML;
}

function getDepartNameForDelete(element) {
    var arrayOfUpdateButtons = document.getElementsByClassName("deleteBtn");
    var counterOfButtons = 0;
    for (var i = 0; i < arrayOfUpdateButtons.length; i++) {
        if (element === arrayOfUpdateButtons[i]) {
            counterOfButtons = i;
            break;
        }
    }
    var arrayOfDepartmentNameCells = document.getElementsByClassName("department_name_cell");
    cellWithDepartToUpdate = arrayOfDepartmentNameCells[counterOfButtons];
    return arrayOfDepartmentNameCells[counterOfButtons].innerHTML;
}

function modalWin(buttonObj) {
    clearInfoContainer();
    departToRename = getDepartNameForDelete(buttonObj);
    modal.style.display = 'flex';
    var infoBlock = document.getElementById('modal-body');
    infoBlock.innerHTML = 'Вы действительно хотите удалить департамент <b>' + departToRename + '</b>?';
}

function validate(inputString) {
    var infoContainer = document.getElementById("info_container");
    var result = true;
    if (inputString === '') {
        result = false;
        infoContainer.innerHTML = 'Название не может быть пустым';
    } else if (!validateLength(inputString, 3, 30)) {
        result = false;
        infoContainer.innerHTML = 'Название должно быть от 3 до 30 символов и не содержать пробелы';
    } else if (inputString === departToRename) {
        result = false;
        infoContainer.innerHTML = 'Это то же самое название';
    }
    return result;
}

function validateLength(string, minLength, maxLength) {
    return string.length >= minLength && string.length <= maxLength && string.indexOf(' ') === - 1  && string.indexOf('\t') === -1;
}

function createDepartment() {
    sendAjaxRequest("./departments_controller", "post", departNewName, displayDepartmentsAfterCreate);
}

function renameDepartment() {
    var url = "./departments_controller/" + departToRename;
    sendAjaxRequest(url, "put", departNewName, displayDepartmentsAfterRename);
}

function displayDepartmentsAfterCreate(data) {
    var message = data.departmentAddResult;
    var infoContainer = document.getElementById("info_container");
    if(message !== "OK") {
        infoContainer.innerHTML = message;
        clearSelection();
    }  else {
        showAllDepartments();
        clearCreateContainer();
        clearSelection();
    }
}

function displayDepartmentsAfterRename(data) {
    var message = data.departmentRenameResult;
    var infoContainer = document.getElementById("info_container");
    if(message !== "OK") {
        infoContainer.innerHTML = message;
        clearSelection();
    }  else {
        showAllDepartments();
        clearRenameContainer();
        clearSelection();
    }
}

function deleteDepartment(departmentName) {
    let url = "./departments_controller/" + departmentName;
    sendAjaxRequest(url, "delete", "", whatToDoAfterDelete);
}

function whatToDoAfterDelete(data) {
    var message = data.departmentDeleteResult;
    var infoContainer = document.getElementById("info_container");
    if(message !== "OK") {
        infoContainer.innerHTML = message;
    }  else {
        showAllDepartments();
    }
}

function sendAjaxRequest(url, method, dataToSend, callback) {
    $.ajax(url, {
        method: method,
        data: dataToSend,
        contentType: 'text/json; charset=utf-8',
        dataType: 'json',
        success: function (data) {
            callback(data);
        }
    })
}


function addListeners() {
    okCreateButton.addEventListener("click", function () {
        var newDepName = document.getElementById("createInput").value;
        if(validate(newDepName)) {
            departNewName = newDepName;
            createDepartment();
            clearCreateContainer();
        }
    });

    cancelCreateButton.addEventListener("click", function () {
        clearCreateContainer();
    });

    okRenameButton.addEventListener("click", function () {
        var newDepName = document.getElementById("renameInput").value;
        if(validate(newDepName)) {
            departNewName = newDepName;
            renameDepartment();
            clearRenameContainer();
        }
    });

    cancelRenameButton.addEventListener("click", function () {
        clearSelection();
        clearRenameContainer();
    });

    okModalBtn.addEventListener('click', function () { //При нажатии Да убираем модальное окно и отправляем форму.
        modal.style.display = 'none';
        deleteDepartment(departToRename);
    });

    cancelModalBtn.addEventListener('click', function () { //При нажатии Отмена убираем модальное окно.
        modal.style.display = 'none';
    });
}