
getActiveTerminals();

function getActiveTerminals() {
    sendAjaxRequest("getActiveTerminals", generateBarcodes);
}

function generateBarcodes(data) {
    var terminals = data.listOfTerminals;
    var table = document.getElementById("barcode_table");

    for (var count = 0; count < terminals.length; count++) {

        var getQuery = "./generatebarcode?barcodeText=" + terminals[count].inventoryId + "&displayText=" + terminals[count].regId;
        console.log(getQuery);
        console.log(getQuery);
        for (var i = 0; i < 5; i++) {
            var row = document.createElement("TR");
            row.setAttribute("class", "barcode_row");
            table.appendChild(row);
            console.log(table);
            var cell = document.createElement("TD");
            cell.setAttribute("class", "barcode_cell");
            row.appendChild(cell);
            var img = document.createElement("IMG");
            img.setAttribute("src", getQuery);
            img.setAttribute("width", 148);
            img.setAttribute("height", 51);
            cell.appendChild(img);
            cell.innerHTML += terminals[count].regId;
        }
    }
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