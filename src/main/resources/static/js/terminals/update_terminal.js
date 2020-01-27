window.onload = function () {
    loadPage();
};



function validate() {
    var result = true;
    var infoBlock = document.getElementById('sys_info');
    infoBlock.innerHTML = '';

    var regId = document.getElementById("regId_input").value;
    var model = document.getElementById("model_input").value;
    var serialId = document.getElementById("serialId_input").value;
    var inventoryId = document.getElementById("inventoryId_input").value;
    var comment = document.getElementById("comment_input").value;


    if (!validateLength(regId, 3, 10)) {
        result = false;
        infoBlock.innerHTML = '"Учетный номер" должен быть от 3 до 10 символов и не содержать пробелы, или оставьте пустым';
    } else if (regId !== '' && !validateRegId(regId)) {
        result = false;
        infoBlock.innerHTML = 'Поле "Учетный номер" должно иметь числовое значение всех символов, кроме первых двух. Например AB12345';
    } else if (!validateLength(model, 3, 20)) {
        result = false;
        infoBlock.innerHTML = '"Модель" должна быть от 3 до 20 символов и не содержать пробелы, или оставьте пустым';
    } else if (!validateLength(serialId, 3, 30)) {
        result = false;
        infoBlock.innerHTML = '"Серийный номер" должен быть от 3 до 30 символов и не содержать пробелы, или оставьте пустым';
    } else if (!validateLength(inventoryId, 3, 20)) {
        result = false;
        infoBlock.innerHTML = '"Инв. номер" должен быть от 3 до 20 символов и не содержать пробелы, или оставьте пустым';
    } else if (!validateComment(comment, 500)) {
        result = false;
        infoBlock.innerHTML = 'Поле "Комментарий" не должно содержать более 500 символов ' +
            'и не должно содержать двойных пробелов, или оставьте пустым';
    }
    if (result) {
        var id = document.getElementById("id").value;
        var dataToSend = {};
        dataToSend["updateTerminal"] = {};
        dataToSend["updateTerminal"]["id"] = id;
        var departmentInput = document.getElementById("departmentsSelector").valueOf();
        var department = departmentInput.options[departmentInput.selectedIndex].value;
        var isActiveInput = document.getElementById("isActive_input");
        var isActive = isActiveInput.checked;
        dataToSend["updateTerminal"]["regId"] = regId;
        dataToSend["updateTerminal"]["model"] = model;
        dataToSend["updateTerminal"]["serialId"] = serialId;
        dataToSend["updateTerminal"]["inventoryId"] = inventoryId;
        dataToSend["updateTerminal"]["comment"] = comment;
        dataToSend["updateTerminal"]["department"] = department;
        dataToSend["updateTerminal"]["isActive"] = isActive;
        dataToSend = JSON.stringify(dataToSend);
        sendUpdateInfo(dataToSend);

    }
}

function sendUpdateInfo(dataToSend) {
    sendAjaxRequest(dataToSend, updateTerminal);
}

function updateTerminal(data) {
    var result = data.terminalUpdateResult;
    if (result === "OK") {
        window.location.replace("./terminals");
    } else {
        var infoBlock = document.getElementById('sys_info');
        infoBlock.innerHTML = result;
    }
}

function validateLength(string, minLength, maxLength) {
    return string.length == 0 || (string.length >= minLength && string.length <= maxLength && string.indexOf(' ') === -1 && string.indexOf('\t') === -1);
}

function validateComment(string, maxLength) {
    return string.length == 0 || (string.length <= maxLength && string.indexOf('  ') === -1);
}

function validateRegId(string) {
    var regex = new RegExp("^[A-zА-я]{2}[0-9]{1,8}$");
    return regex.test(string);
}


function sendAjaxRequest(dataToSend, callback) {
    $.ajax('./json', {
        method: 'post',
        data: dataToSend,
        contentType: 'text/json; charset=utf-8',
        dataType: 'json',
        success: function (data) {
            callback(data);
        }
    })
}


function displayDepartmentsSelector(data) {
    var listOfDepartments = data.listOfDeparts;
    var selector = document.getElementById("departmentsSelector");
    var options = document.createElement("option");
    options.selected = true;
    options.setAttribute("value", "");
    options.innerHTML = "Не выбран";
    selector.appendChild(options);

    for (var i = 0; i < listOfDepartments.length; i++) {
        options = document.createElement("option");
        options.setAttribute("value", listOfDepartments[i].department);
        options.innerHTML = listOfDepartments[i].department;
        selector.appendChild(options);
    }
    getAndDisplayTerminalInfo();
}


function displayTerminalInfo(data) {
    var terminal = data;
    var terminalDepartment = terminal.department == null ? "" : terminal.department.department;



    var regId = document.getElementById("regId_input").valueOf();
    regId.setAttribute("placeholder", terminal.regId);

    var model = document.getElementById("model_input").valueOf();
    model.setAttribute("placeholder", terminal.terminalModel);

    var serialId = document.getElementById("serialId_input").valueOf();
    serialId.setAttribute("placeholder", terminal.serialId);

    var inventoryId = document.getElementById("inventoryId_input").valueOf();
    inventoryId.setAttribute("placeholder", terminal.inventoryId);

    var comment = document.getElementById("comment_input").valueOf();
    comment.setAttribute("placeholder", terminal.terminalComment);

    var isActive = document.getElementById("isActive_input").valueOf();
    isActive.checked = terminal.terminalIsActive;

    var selectorsOfDepartmentSelector = document.getElementById("departmentsSelector")
        .getElementsByTagName("option");
    for (var i = 0; i < selectorsOfDepartmentSelector.length; i++) {
        if(selectorsOfDepartmentSelector[i].value === terminalDepartment) {
            console.log("terminalDepartment " + terminalDepartment);
            selectorsOfDepartmentSelector[i].selected = true;
        }
    }
}

function loadPage() {
    getAndDisplayDepartments();
}

function getAndDisplayDepartments() {
    sendAjaxRequest("getAllDepartments", displayDepartmentsSelector);
}

function getAndDisplayTerminalInfo() {
    var idInputField = document.getElementById("id");
    var id = idInputField.value;
    var jsonObj = JSON.stringify({"getTerminalInfo":id});
    sendAjaxRequest(jsonObj, displayTerminalInfo);
}

