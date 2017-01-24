var serverPath = '..';
var heaterStatusPath = serverPath + '/heaterstatus';
var commandPath = serverPath + '/command';

function getJson(path, callback) {
    var request = new XMLHttpRequest();
    request.open('GET', path, true);
    request.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    request.onload = function () {
        if (this.status >= 200 && this.status < 400) {
            var json = JSON.parse(this.response);
            callback(json);
        }
    };
    request.send();
}

function formInputToJSON(form) {
    var data = {};
    for (var i = 0, ii = form.length; i < ii; ++i) {
        var input = form[i];
        if (input.name) {

            if (input.type == 'number') {
                data[input.name] = Number(input.value);
            }
            else if (input.type == 'radio' && input.checked) {
                if (input.value == '0')
                    data[input.name] = false;
                else
                    data[input.name] = true;
            }
            else if (input.type == 'checkbox') {
                if (input.checked)
                    data[input.name] = true;
                else
                    data[input.name] = false;
            }
            else {
                data[input.name] = input.value;
            }
        }
    }
    return data;
}

function sendCommand(data, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', commandPath, true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    xhr.send(JSON.stringify(data));
    document.getElementById('command').innerHTML = 'command sent' + JSON.stringify(data);
    xhr.onloadend = function () {
        result = xhr.responseText;
        var json = JSON.parse(result);
        callback(json);
    };
}
