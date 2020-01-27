var inputTerminalField;
var inputUserField;
var okModalErrBtn;
var modalOverlay;
var infoField;

var userLogin;
var userName;
var userSurname;
var terminalRegId;
//var terminalId;
var isReceiving = false;
var focusToTerminalInput = true;

function onWindowLoad(){
    inputTerminalField = document.getElementById("terminalInput");
    inputUserField = document.getElementById("userInput");
    okModalErrBtn = document.querySelector('.ok-error');
    modalOverlay = document.querySelector('.modal-overlay');
    infoField = document.getElementById('info-field');

    terminalInputFocus();
    terminalsRemainColor();



    document.onkeydown = function keyPress (e) {
        if(e.key === "Escape") {
            terminalInputFocus();
        }
        if (e.shiftKey && e.key === "F2") {
            location.href = "./main";
        }
    };

    inputTerminalField.addEventListener('keypress', function (evt) {
        if (validateTerminalInputEvent(evt)) {
            sendTerminalInventoryId();
        }

    });

    inputUserField.addEventListener('keypress', function (evt) {
        if (validateUserInputEvent(evt)) {
            if(isReceiving) {
                receiveTerminalFromUser();
            } else {
                giveTerminalToUser();
            }
        }
    });


    okModalErrBtn.addEventListener('click', function () { //При нажатии Ok убираем модальное окно.
        modalOverlay.style.display = 'none';
        if (focusToTerminalInput) {
            terminalInputFocus();
        } else {
            userInputFocus();
        }

    });
    getAllStatistic();
}

function getAllStatistic() {
sendAjaxData("./registrations", "getStatisticForRegistrationsPage", loadTerminalsStatistic);

}

function loadTerminalsStatistic(data) {
    data = JSON.parse(data);
    var total = data.totalAmountOfTerminals;
    var inactive = data.amountOfInactiveTerminals;
    var given = data.amountOfGivenTerminals;
    var active = data.activeTerminalsRemain;
    showTerminalsStatistic(total, inactive, given, active);
}




function validateTerminalInputEvent(event) {
    var result = true;
    if (inputTerminalField.value.indexOf(' ') !== - 1) {
        inputTerminalField.value = '';
        result = false;
    } else {
        result = event.key === "Enter" && inputTerminalField.value !== '';
    }
    return result;
}

function validateUserInputEvent(event) {
    var result = true;
    if (inputUserField.value.indexOf(' ') !== - 1) {
        inputUserField.value = '';
        result = false;
    } else {
        result = event.key === "Enter" && inputUserField.value !== '';
    }
    return result;
}


function invokeErrorWindow(message) {
    infoField.innerHTML = message;
    modalOverlay.style.display = 'flex';
    modalOverlay.setAttribute('tabindex', '0'); //убираем фокус на modalOverlay
    modalOverlay.focus(); //убираем фокус на modalOverlay
}


function sendTerminalInventoryId() {
    var inventoryId = inputTerminalField.value;
    function callback(data) {
        var str = JSON.parse(data);

        if(typeof str.terminalNotExists !== 'undefined') {
            focusToTerminalInput = true;
            invokeErrorWindow(str.terminalNotExists);
        }
        if(typeof str.terminalNotActive !== 'undefined') {
            focusToTerminalInput = true;
            invokeErrorWindow(str.terminalNotActive);
        }
        if(typeof str.terminalIsReady !== 'undefined') { //Выдавать

            document.getElementById("operationTypeInfo").innerHTML = "\u0412\u044b\u0434\u0430\u0447\u0430";
            document.getElementById("operationTypeInfo").style.color = '#F30004';
            document.getElementById("terminalNoInfo").innerHTML =  str.terminalRegId;
            terminalRegId = str.terminalRegId;
            document.getElementById("terminalImageBlock").innerHTML = photoLink("terminalsphoto", str.terminalRegId);
            //terminalId = str.terminalId;
            isReceiving = false;
            userInputFocus();
        }
        if(typeof str.login !== 'undefined') { //Получать
            document.getElementById("operationTypeInfo").innerHTML = "\u041f\u0440\u0438\u0435\u043c";
            document.getElementById("operationTypeInfo").style.color =  '#009816'; //зеленый
            userLogin = str.login;
            userName = str.name;
            userSurname = str.surname;
            terminalRegId = str.terminalRegId;
            //terminalId = str.terminalId;
            document.getElementById("userNameInfo").innerHTML = str.surname + " " + str.name;
            document.getElementById("terminalNoInfo").innerHTML =  str.terminalRegId;
            document.getElementById("terminalImageBlock").innerHTML = photoLink("terminalsphoto", str.terminalRegId);
            isReceiving = true;
            userInputFocus();
        }
    }
    var dataToSend = {};
    dataToSend["validateTerminalInventoryId"] = inventoryId;
    dataToSend = JSON.stringify(dataToSend);
    sendAjaxData('./registrations', dataToSend, callback);
    inputTerminalField.value = '';
}

function terminalInputFocus() {
    document.getElementById("operationTypeInfo").innerHTML = "";//сбрасываем поля
    document.getElementById("terminalNoInfo").innerHTML =  "";
    document.getElementById("userNameInfo").innerHTML =  "";
    document.getElementById("terminalImageBlock").innerHTML ="";
    inputUserField.value = '';
    inputTerminalField.value = '';
    inputUserField.style.backgroundColor = '#FFFFFF'; //белый
    inputTerminalField.focus();
    inputTerminalField.style.backgroundColor = '#99FFBA'; //зеленый
}

function userInputFocus() {
    inputTerminalField.style.backgroundColor = '#FFFFFF'; //белый
    inputUserField.focus();
    inputUserField.style.backgroundColor = '#99FFBA'; //зеленый
}

function giveTerminalToUser() {
    var userInputLogin = inputUserField.value;
    var hiddenElementAdminLogin = document.getElementById("adminLogin");
    var adminGaveLogin = hiddenElementAdminLogin.value;
    console.log("terminalRegId " + terminalRegId)
    var dataToSend = JSON.stringify({"validateUserInputForGiving":"", userInputLogin:userInputLogin, terminalRegId:terminalRegId, adminGaveLogin:adminGaveLogin});


    function callback(data) {
        var str = JSON.parse(data);
        console.log(str);
        if(typeof str.userNotExists !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.userNotExists);

        }
        if (typeof str.userNotActive !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.userNotActive);
        }
        if (typeof str.userAlreadyHaveTerminal !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.userAlreadyHaveTerminal);
        }
        if (typeof str.departmentsNotMatch !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.departmentsNotMatch);
        }
        if (typeof str.terminalGivingSuccess !== 'undefined') {
            document.getElementById("userNameInfo").innerHTML = str.surname + " " + str.name;
            showTerminalsStatistic(str.totalAmountOfTerminals, str.amountOfInactiveTerminals, str.amountOfGivenTerminals, str.activeTerminalsRemain);
            document.getElementById("userImageBlock").innerHTML = photoLink("usersphoto", str.login);

            $('#userInput').attr('disabled', 'disabled'); //Disable input
            setTimeout(function(){
                document.getElementById("operationTypeInfo").innerHTML = ""; //сбрасываем поля
                document.getElementById("terminalNoInfo").innerHTML =  "";
                document.getElementById("userNameInfo").innerHTML =  "";
                document.getElementById("userImageBlock").innerHTML = "";
                terminalInputFocus();
                $('#userInput').removeAttr('disabled'); //Enable input
            }, 1000);//wait 1 second

        }
    }
    sendAjaxData('./registrations', dataToSend, callback);
    inputUserField.value = '';
}


function receiveTerminalFromUser() {
    var userInputLogin = inputUserField.value;
    var hiddenElementAdminLogin = document.getElementById("adminLogin");
    var adminReceivedLogin = hiddenElementAdminLogin.value;
    var dataToSend = JSON.stringify({"validateUserInputForReceiving":"", userInputLogin:userInputLogin, terminalRegId:terminalRegId, adminReceivedLogin:adminReceivedLogin});

    function callback(data) {
        var str = JSON.parse(data);

        if(typeof str.userNotExists !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.userNotExists);
        }
        if (typeof str.userNotMatch !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.userNotMatch);
        }
        if (typeof str.fatalErrorRecordNotFound !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.fatalErrorRecordNotFound);
        }
        if (typeof str.doNotHaveTerminal !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(str.doNotHaveTerminal);
        }

        if (typeof str.terminalReceivingSuccess !== 'undefined') {
            document.getElementById("userNameInfo").innerHTML = str.surname + " " + str.name;
            showTerminalsStatistic(str.totalAmountOfTerminals, str.amountOfInactiveTerminals, str.amountOfGivenTerminals, str.activeTerminalsRemain);
            document.getElementById("userImageBlock").innerHTML = photoLink("usersphoto", str.login);

            $('#userInput').attr('disabled', 'disabled'); //Disable input
            setTimeout(function(){
                document.getElementById("operationTypeInfo").innerHTML = ""; //сбрасываем поля
                document.getElementById("terminalNoInfo").innerHTML =  "";
                document.getElementById("userNameInfo").innerHTML =  "";
                document.getElementById("userImageBlock").innerHTML = "";
                terminalInputFocus();
                $('#userInput').removeAttr('disabled'); //Enable input
            }, 1000);//wait 1 second

        }
    }
    sendAjaxData('./registrations', dataToSend, callback);
    inputUserField.value = '';
}


function showTerminalsStatistic(total, inactive, given, remain) {
    document.getElementById("terminalsTotalInfo").innerHTML = total;
    document.getElementById("terminalsInactiveInfo").innerHTML = inactive;
    document.getElementById("terminalsGivenInfo").innerHTML = given;
    document.getElementById("terminalsRemainInfo").innerHTML = remain;
    terminalsRemainColor();

}


function sendAjaxData(url, data, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url);
    xhr.setRequestHeader('Content-Type', 'application/json;');

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            callback(xhr.responseText);
        }
    };
    xhr.send(data);
}

function photoLink(folder, fileName) {
    return "<img id='img' src=\"./generatephoto?folder="
        + folder + "&fileName=" + fileName + ".jpg\" width=\"304px\" height=\"304px\" alt=''>";
}

function terminalsRemainColor() {
    var terminalsRemainInfo = document.getElementById("terminalsRemainInfo");
    var terminalsRemain = terminalsRemainInfo.innerHTML;
    if(terminalsRemain == 0) {
        terminalsRemainInfo.style.color = "#c2181a";
    } else if(terminalsRemain > 0 && terminalsRemain <= 3) {
        terminalsRemainInfo.style.color = "#ffad1c";
        terminalsRemainInfo.style.textShadow = "2px 2px 1px #c2181a, 0 2px 1px #c2181a, -2px 0 1px #c2181a, 0 -2px 1px #c2181a";
    } else {
        terminalsRemainInfo.style.color = "";
        terminalsRemainInfo.style.textShadow = "";
    }

}