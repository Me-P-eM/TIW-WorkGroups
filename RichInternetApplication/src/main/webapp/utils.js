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