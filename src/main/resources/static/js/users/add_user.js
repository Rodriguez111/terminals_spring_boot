function validate() {
    var infoBlock = document.getElementById('sys_info');
    infoBlock.innerHTML = '';

    var result = true;
    var login = document.getElementById("login").value;
    var password = document.getElementById("password").value;
    var rePassword = document.getElementById("rePassword").value;
    var name = document.getElementById("name").value;
    var surname = document.getElementById("surname").value;

    if (login == '') {
        result = false;
        infoBlock.innerHTML = 'Поле Логин не может быть пустым';
    } else if(!validateLength(login, 20)) {
        result = false;
        infoBlock.innerHTML = 'Логин должен быть от 3 до 20 символов и не содержать пробелы';
    }
    else if (password == '') {
        result = false;
        infoBlock.innerHTML = 'Поле Пароль не может быть пустым';
    } else if(!validateLength(password, 20)) {
        result = false;
        infoBlock.innerHTML = 'Пароль должен быть от 3 до 20 символов и не содержать пробелы';
    }
    else if (password != rePassword) {
        result = false;
        infoBlock.innerHTML = 'Пароли не совпадают';
    } else if (name == '') {
        result = false;
        infoBlock.innerHTML = 'Поле Имя не может быть пустым';
    } else if(!validateLength(name, 25)) {
        result = false;
        infoBlock.innerHTML = 'Имя должно быть от 3 до 25 символов и не содержать пробелы';
    } else if (surname == '') {
        result = false;
        infoBlock.innerHTML = 'Поле Фамилия не может быть пустым';
    } else if(!validateLength(surname, 25)) {
        result = false;
        infoBlock.innerHTML = 'Фамилия должна быть от 3 до 25 символов и не содержать пробелы';
    }
    if(result) {
        var roleInput = document.getElementById("rolesSelector");
        var role = roleInput.options[roleInput.selectedIndex].value;
        var departmentInput = document.getElementById("departmentsSelector").valueOf();
        var department = departmentInput.options[departmentInput.selectedIndex].value;
        var isActiveInput = document.getElementById("isActive_input");
        var isActive = isActiveInput.checked;

        var dataToSend = {};
        dataToSend["addUser"] = {};
        dataToSend["addUser"]["login"] = login;
        dataToSend["addUser"]["password"] = password;
        dataToSend["addUser"]["name"] = name;
        dataToSend["addUser"]["surname"] = surname;
        dataToSend["addUser"]["role"] = role;
        dataToSend["addUser"]["department"] = department;
        dataToSend["addUser"]["isActive"] = isActive;
        dataToSend = JSON.stringify(dataToSend);
        sendAddUserInfo(dataToSend);
    }
    return result;
}

function sendAddUserInfo(dataToSend) {
    sendAjaxRequest(dataToSend, addUser);
}

function addUser(data) {
    var result = data.userAddResult;
    if (result === "OK") {
        window.location.replace("./users");
    } else {
        var infoBlock = document.getElementById('sys_info');
        infoBlock.innerHTML = result;
    }
}



function validateLength(string, maxLength) {
    return string.length >= 3 && string.length <= maxLength && string.indexOf(' ') === - 1  && string.indexOf('\t') === -1;
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

function displayRolesSelector(data) {
    var listOfRoles = data.listOfRoles;
    var selector = document.getElementById("rolesSelector");
    for (var i = 0; i < listOfRoles.length; i++) {
        options = document.createElement("option");
        options.setAttribute("value", listOfRoles[i].role);
        options.innerHTML = listOfRoles[i].role;
        if (listOfRoles[i].role === "user") {
            options.selected = true;
        }
        selector.appendChild(options);
    }
}


function getAndDisplayRoles() {
    sendAjaxRequest("getListOfRoles", displayRolesSelector);
}
getAndDisplayDepartments();
getAndDisplayRoles();