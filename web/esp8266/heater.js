var whichPressed;
var startManualForm;
var manualOffForm;
var stopManualForm;
var counter = 0;

function load() {

    document.getElementById('heater').onsubmit = function (event) {
        event.preventDefault();
        sendPost(this, commandResponse);
    };
    startManualForm = document.getElementById('startManualForm');
    startManualForm.onsubmit = function () {
        event.preventDefault();
        sendPost(this, commandResponse);
    };
    manualOffForm = document.getElementById('manualOffForm');
    manualOffForm.onsubmit = function () {
        event.preventDefault();
        sendPost(this, commandResponse);
    };
    stopManualForm = document.getElementById('stopManualForm');
    stopManualForm.onsubmit = function () {
        event.preventDefault();
        sendPost(this, commandResponse);
    };
    startManualForm.style.display = 'none';
    manualOffForm.style.display = 'none';
    stopManualForm.style.display = 'none';

    getJson(heaterStatusPath, refresh);

    setInterval(function(){ getJson(heaterStatusPath, refresh); }, 20000);
}

function commandResponse(json) {
    /*whichPressed.style.visibility = "visible";*/
    document.getElementById('command').innerHTML += 'command result' + JSON.stringify(json);
    getJson(heaterStatusPath, refresh);
}

function refresh(json) {
    document.getElementById('summary').innerHTML = JSON.stringify(json);

    if (json.enabled)
        document.getElementById('heaterEnabled').checked = true;
    else
        document.getElementById('heaterEnabled').checked = false;

    var pinSelectControl = document.getElementById('pinSelect');
    for (var i, j = 0; i = pinSelectControl.options[j]; j++) {
        if (i.value == json.pin) {
            pinSelectControl.selectedIndex = j;
            break;
        }
    }
    if (json.status == 'manual' || json.status == 'manualoff') {
        startManualForm.style.display = 'none';
        manualOffForm.style.display = 'none';
        stopManualForm.style.display = 'block';

    } else {
        startManualForm.style.display = 'block';
        manualOffForm.style.display = 'block';
        stopManualForm.style.display = 'none';
    }
    whichPressed.style.visibility = "visible";
}


function sendPost(form, callback) {
    //whichPressed.style.visibility = "hidden";
    startManualForm.style.display = 'block';
    manualOffForm.style.display = 'block';
    stopManualForm.style.display = 'block';
    var data = formInputToJSON(form);

    sendCommand(data, callback);
}
