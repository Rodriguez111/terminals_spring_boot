
getActiveTerminals();

function getActiveTerminals() {
    let url = "./terminals_controller/active";
    sendAjaxRequest(url, "get", "", generateBarcodes);
}

function generateBarcodes(terminals) {
    var table = document.getElementById("barcode_table");

    for (var count = 0; count < terminals.length; count++) {

        var getQuery = "./generatebarcode?barcodeText=" + terminals[count].inventoryId + "&displayText=" + terminals[count].regId;
        for (var i = 0; i < 5; i++) {
            var row = document.createElement("TR");
            row.setAttribute("class", "barcode_row");
            table.appendChild(row);
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

function sendAjaxRequest(url, method, dataToSend, callback) {
    $.ajax(url, {
        method:method,
        data:dataToSend,
        contentType:'text/json; charset=utf-8',
        dataType:'json',
        success:function (data) {
            callback(data);
        }
    })
}