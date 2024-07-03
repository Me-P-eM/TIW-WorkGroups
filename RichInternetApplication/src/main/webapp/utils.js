/**
 * Call management (AJAX)
 */
function makeCall(method, url, formElement, cback, reset = true) {
    let req = new XMLHttpRequest();
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
    if (formElement !== null && reset === true) {
        formElement.reset();
    }
}

function showElement(elementId) {
    document.getElementById(elementId).style.display = "block";
}

function hideElement(elementId) {
    document.getElementById(elementId).style.display = "none";
}

function setErrorMessage(elementId, message) {
    document.getElementById(elementId).textContent = message;
}

function resetForm(formId) {
    document.getElementById(formId).reset();
}

function isValidEmail(email) {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
}