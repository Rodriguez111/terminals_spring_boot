function validate() {
    var infoBlock = document.getElementById('sys_info');
    infoBlock.innerHTML = '';

    var result = true;

    var regId = document.getElementById("regId").value;
    var model = document.getElementById("model").value;
    var serialId = document.getElementById("serialId").value;
    var inventoryId = document.getElementById("inventoryId").value;
    var comment = document.getElementById("comment").value;


    if (regId == '') {
        result = false;
        infoBlock.innerHTML = 'Поле "Учетный номер" не может быть пустым';
    } else if (!validateLength(regId, 3, 10)) {
        result = false;
        infoBlock.innerHTML = '"Учетный номер" должен быть от 3 до 10 символов и не содержать пробелы';
    } else if (!validateRegId(regId)) {
        result = false;
        infoBlock.innerHTML = 'Поле "Учетный номер" должно иметь числовое значение всех символов, кроме первых двух. Например AB12345';
    } else if (model == '') {
        result = false;
        infoBlock.innerHTML = 'Поле "Модель" не может быть пустым';
    } else if (!validateLength(model, 3, 20)) {
        result = false;
        infoBlock.innerHTML = 'Поле "Модель" должно быть от 3 до 20 символов и не содержать пробелы';
    } else if (serialId == '') {
        result = false;
        infoBlock.innerHTML = 'Поле "Серийный номер" не может быть пустым';
    } else if (!validateLength(serialId, 3, 30)) {
        result = false;
        infoBlock.innerHTML = 'Поле "Серийный номер" должно быть от 3 до 30 символов и не содержать пробелы';
    }  else if (inventoryId == '') {
        result = false;
        infoBlock.innerHTML = 'Поле "Инв. номер" не может быть пустым';
    } else if (!validateLength(inventoryId, 3, 20)) {
        result = false;
        infoBlock.innerHTML = 'Поле "Инв. номер" должно быть от 3 до 20 символов и не содержать пробелы';
    } else if (comment != '') {
        if (!validateComment(comment, 500)) {
            result = false;
            infoBlock.innerHTML = 'Поле "Комментарий" не должно содержать более 500 символов ' +
                'и не должно содержать двойных пробелов';
        }
    }
    if (result) {

        var departmentInput = document.getElementById("departmentsSelector").valueOf();
        var department = departmentInput.options[departmentInput.selectedIndex].value;
        var isActiveInput = document.getElementById("isActive_input");
        var isActive = isActiveInput.checked;

        var dataToSend = {};
        dataToSend["addTerminal"] = {};
        dataToSend["addTerminal"]["regId"] = regId;
        dataToSend["addTerminal"]["model"] = model;
        dataToSend["addTerminal"]["serialId"] = serialId;
        dataToSend["addTerminal"]["inventoryId"] = inventoryId;
        dataToSend["addTerminal"]["comment"] = comment;
        dataToSend["addTerminal"]["department"] = department;
        dataToSend["addTerminal"]["isActive"] = isActive;
        dataToSend = JSON.stringify(dataToSend);
        sendAddTerminalInfo(dataToSend);
    }
    return result;
}

function sendAddTerminalInfo(dataToSend) {
    sendAjaxRequest(dataToSend, addTerminal);
}

function addTerminal(data) {
    var result = data.terminalAddResult;
    if (result === "OK") {
        window.location.replace("./terminals");
    } else {
        var infoBlock = document.getElementById('sys_info');
        infoBlock.innerHTML = result;
    }
}

function validateLength(string, minLength, maxLength) {
    return string.length >= minLength && string.length <= maxLength && string.indexOf(' ') === - 1  && string.indexOf('\t') === -1;
}

function validateComment(string, maxLength) {
    return string.length <= maxLength && string.indexOf('  ') === - 1  && string.indexOf('\t') === -1;
}

function validateRegId(string) {
    var regex = new RegExp("^[A-zА-я]{2}[0-9]{1,8}$");
    return regex.test(string);
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
}

function getAndDisplayDepartments() {
    sendAjaxRequest("getAllDepartments", displayDepartmentsSelector);
}
getAndDisplayDepartments();