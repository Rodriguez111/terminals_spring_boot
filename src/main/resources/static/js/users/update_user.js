window.onload = function () {
    loadPage();

};


function validate() {
    var result = true;
    var infoBlock = document.getElementById('sys_info');
    infoBlock.innerHTML = '';

    var login = document.getElementById("login_input").value;
    var name = document.getElementById("name_input").value;
    var surname = document.getElementById("surname_input").value;
    var password = document.getElementById("password_input").value;
    var rePassword = document.getElementById("repassword_input").value;

    if (!validateLength(login, 20)) {
        result = false;
        infoBlock.innerHTML = 'Логин должен быть от 3 до 20 символов и не содержать пробелы, или оставьте пустым';
    }
    if (password.length != 0) {
        if (!validateLength(password, 20)) {
            result = false;
            infoBlock.innerHTML = 'Пароль должен быть от 3 до 20 символов и не содержать пробелы, или оставьте пустым';
        } else if (password != rePassword) {
            result = false;
            infoBlock.innerHTML = 'Пароли не совпадают';
        }
    }
    if (!validateLength(name, 25)) {
        result = false;
        infoBlock.innerHTML = 'Имя должно быть от 3 до 25 символов и не содержать пробелы, или оставьте пустым';
    }
    if (!validateLength(surname, 25)) {
        result = false;
        infoBlock.innerHTML = 'Фамилия должна быть от 3 до 25 символов и не содержать пробелы, или оставьте пустым';
    }
    if (result) {
        var idInputField = document.getElementById("id");
        var id = idInputField.value;



        var roleInput = document.getElementById("rolesSelector");
        var role = roleInput.options[roleInput.selectedIndex].value;
        var departmentInput = document.getElementById("departmentsSelector").valueOf();
        var department = departmentInput.options[departmentInput.selectedIndex].value;
        var isActiveInput = document.getElementById("isActive_input");
        var isActive = isActiveInput.checked;

        var dataToSend = {};
        dataToSend["login"] = login;
        dataToSend["password"] = password;
        dataToSend["name"] = name;
        dataToSend["surname"] = surname;
        dataToSend["role"] = role;
        dataToSend["department"] = department;
        dataToSend["isActive"] = isActive;
        dataToSend = JSON.stringify(dataToSend);
        sendUpdateInfo(id, dataToSend);
    }
}

function validateLength(string, maxLength) {
    return string.length == 0 || (string.length >= 3 && string.length <= maxLength && string.indexOf(' ') === - 1  && string.indexOf('\t') === -1);
}


function sendAjaxRequest(url, method, dataToSend, callback) {
    $.ajax(url, {
        method:method,
        data:dataToSend,
        contentType:'application/json; charset=utf-8',
        dataType:'json',
        success:function (data) {
            callback(data);
        }
    })
}

function displayDepartmentsSelector(listOfDepartments) {
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
    getAndDisplayUserInfo();
}

function getAndDisplayDepartments() {
    sendAjaxRequest("./departments_controller", "get", "",  displayDepartmentsSelector);
}

function displayRolesSelector(listOfRoles) {
    var selector = document.getElementById("rolesSelector");
    for (var i = 0; i < listOfRoles.length; i++) {
        options = document.createElement("option");
        options.setAttribute("value", listOfRoles[i].role);
        options.innerHTML = listOfRoles[i].role;
        selector.appendChild(options);
    }
    getAndDisplayDepartments()
}

function getAndDisplayRoles() {
    sendAjaxRequest("./roles_controller", "get", "",  displayRolesSelector);
}

function displayUserInfo(user) {
    var userDepartment = user.department == null ? "" : user.department.department;
    var login = document.getElementById("login_input").valueOf();
    login.setAttribute("placeholder", user.userLogin);
    if(user.userLogin === "root") {
        login.setAttribute("disabled", true);
    }
    var name = document.getElementById("name_input").valueOf();
    name.setAttribute("placeholder", user.userName);
    var surname = document.getElementById("surname_input").valueOf();
    surname.setAttribute("placeholder", user.userSurname);
    var isActive = document.getElementById("isActive_input");
    isActive.checked  = user.active;
    if (user.userLogin === 'root') {
        isActive.disabled  = true;
    }

    var selectorsOfDepartmentSelector = document.getElementById("departmentsSelector")
        .getElementsByTagName("option");
    for (var i = 0; i < selectorsOfDepartmentSelector.length; i++) {
        if(selectorsOfDepartmentSelector[i].value === userDepartment) {

            selectorsOfDepartmentSelector[i].selected = true;
        }
    }

    var selectorsOfRoleSelector = document.getElementById("rolesSelector")
        .getElementsByTagName("option");

    if (user.userLogin === 'root') {
        var roleSelector = document.getElementById("rolesSelector");
        roleSelector.disabled = true;
        var option = document.createElement("option");
        option.setAttribute("value", "root");
        option.setAttribute("selected", true);
        option.innerHTML = "root";
        roleSelector.appendChild(option);
    } else {

        for (var i = 0; i < selectorsOfRoleSelector.length; i++) {
            if(selectorsOfRoleSelector[i].value === user.role.role) {
                selectorsOfRoleSelector[i].selected = true;
            }
        }
    }
}

function loadPage() {
    getAndDisplayRoles(); 
}

function getAndDisplayUserInfo() {
    var idInputField = document.getElementById("id");
    var id = idInputField.value;
    let url = "./users_controller/userinfo/" + id;
    sendAjaxRequest(url, "get", "", displayUserInfo);
}


function sendUpdateInfo(id, dataToSend) {
    let url = "./users_controller/" + id;
    sendAjaxRequest(url, "put", dataToSend, updateUser);
}

function updateUser(data) {
    let userUpdateResult = data.userUpdateResult;
    if (userUpdateResult === "OK") {
        window.location.replace("./users");
    } else {
        var infoBlock = document.getElementById('sys_info');
        infoBlock.innerHTML = userUpdateResult;
    }
}

