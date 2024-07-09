/**
 * Call management (AJAX)
 */
function makeCall(method, url, formElement, cback) {
    const req = new XMLHttpRequest();
    req.onreadystatechange = function() {
        if (req.readyState === 4) {
            cback(req);
        }
    }; // closure
    req.open(method, url);
    if (formElement == null) {
        req.send();
    } else {
        req.send(new FormData(formElement));
    }
}

function makeJsonCall(method, url, jsonBody, cback) {
    const req = new XMLHttpRequest();
    req.onreadystatechange = function() {
        if (req.readyState === 4) {
            cback(req);
        }
    }; // closure
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify(jsonBody));
}


function showElement(element) {
    element.removeAttribute("style");
}

function hideElement(element) {
    element.style.display = "none";
}

function setMessage(element, message) {
    element.textContent = message;
}

function clearMessage(element) {
    setMessage(element, "");
}

function resetForm(form) {
    form.reset();
}

function isValidEmail(email) {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
}