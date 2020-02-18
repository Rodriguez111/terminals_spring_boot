var inputTerminalField;
var inputUserField;
var okModalErrBtn;
var modalOverlay;
var infoField;

var userLogin;
var userName;
var userSurname;
var terminalRegId;
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
    let url = "./registrations/getStatistic";
sendAjaxData(url, "get", "", loadTerminalsStatistic);

}

function loadTerminalsStatistic(data) {
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
        if(typeof data.terminalNotExists !== 'undefined') {
            focusToTerminalInput = true;
            invokeErrorWindow(data.terminalNotExists);
        }
        if(typeof data.terminalNotActive !== 'undefined') {
            focusToTerminalInput = true;
            invokeErrorWindow(data.terminalNotActive);
        }
        if(typeof data.terminalIsReady !== 'undefined') { //Выдавать

            document.getElementById("operationTypeInfo").innerHTML = "\u0412\u044b\u0434\u0430\u0447\u0430";
            document.getElementById("operationTypeInfo").style.color = '#F30004';
            document.getElementById("terminalNoInfo").innerHTML =  data.terminalRegId;
            terminalRegId = data.terminalRegId;
            document.getElementById("terminalImageBlock").innerHTML = photoLink("terminalsphoto", data.terminalRegId);
            //terminalId = str.terminalId;
            isReceiving = false;
            userInputFocus();
        }
        if(typeof data.login !== 'undefined') { //Получать
            document.getElementById("operationTypeInfo").innerHTML = "\u041f\u0440\u0438\u0435\u043c";
            document.getElementById("operationTypeInfo").style.color =  '#009816'; //зеленый
            userLogin = data.login;
            userName = data.name;
            userSurname = data.surname;
            terminalRegId = data.terminalRegId;
            //terminalId = str.terminalId;
            document.getElementById("userNameInfo").innerHTML = data.surname + " " + data.name;
            document.getElementById("terminalNoInfo").innerHTML =  data.terminalRegId;
            document.getElementById("terminalImageBlock").innerHTML = photoLink("terminalsphoto", data.terminalRegId);
            isReceiving = true;
            userInputFocus();
        }
    }
    var dataToSend = {};
    dataToSend["validateTerminalInventoryId"] = inventoryId;
    dataToSend = JSON.stringify(dataToSend);
    sendAjaxData('./registrations/validateTerminalInventoryId', "post", dataToSend, callback);
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
    var dataToSend = JSON.stringify({"validateUserInputForGiving":"", userInputLogin:userInputLogin, terminalRegId:terminalRegId, adminGaveLogin:adminGaveLogin});


    function callback(data) {
        if(typeof data.userNotExists !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.userNotExists);
        }
        if (typeof data.userNotActive !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.userNotActive);
        }
        if (typeof data.userAlreadyHaveTerminal !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.userAlreadyHaveTerminal);
        }
        if (typeof data.departmentsNotMatch !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.departmentsNotMatch);
        }
        if (typeof data.terminalGivingSuccess !== 'undefined') {
            document.getElementById("userNameInfo").innerHTML = data.surname + " " + data.name;
            showTerminalsStatistic(data.totalAmountOfTerminals, data.amountOfInactiveTerminals, data.amountOfGivenTerminals, data.activeTerminalsRemain);
            document.getElementById("userImageBlock").innerHTML = photoLink("usersphoto", data.login);

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
    sendAjaxData('./registrations/validateUserInputForGiving', "post", dataToSend, callback);
    inputUserField.value = '';
}


function receiveTerminalFromUser() {
    var userInputLogin = inputUserField.value;
    var hiddenElementAdminLogin = document.getElementById("adminLogin");
    var adminReceivedLogin = hiddenElementAdminLogin.value;
    var dataToSend = JSON.stringify({"validateUserInputForReceiving":"", userInputLogin:userInputLogin, terminalRegId:terminalRegId, adminReceivedLogin:adminReceivedLogin});

    function callback(data) {
        if(typeof data.userNotExists !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.userNotExists);
        }
        if (typeof data.userNotMatch !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.userNotMatch);
        }
        if (typeof data.fatalErrorRecordNotFound !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.fatalErrorRecordNotFound);
        }
        if (typeof data.doNotHaveTerminal !== 'undefined') {
            focusToTerminalInput = false;
            invokeErrorWindow(data.doNotHaveTerminal);
        }

        if (typeof data.terminalReceivingSuccess !== 'undefined') {
            document.getElementById("userNameInfo").innerHTML = data.surname + " " + data.name;
            showTerminalsStatistic(data.totalAmountOfTerminals, data.amountOfInactiveTerminals, data.amountOfGivenTerminals, data.activeTerminalsRemain);
            document.getElementById("userImageBlock").innerHTML = photoLink("usersphoto", data.login);

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
    sendAjaxData('./registrations/validateUserInputForReceiving', "post", dataToSend, callback);
    inputUserField.value = '';
}


function showTerminalsStatistic(total, inactive, given, remain) {
    document.getElementById("terminalsTotalInfo").innerHTML = total;
    document.getElementById("terminalsInactiveInfo").innerHTML = inactive;
    document.getElementById("terminalsGivenInfo").innerHTML = given;
    document.getElementById("terminalsRemainInfo").innerHTML = remain;
    terminalsRemainColor();

}

function sendAjaxData(url, method, dataToSend, callback) {
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