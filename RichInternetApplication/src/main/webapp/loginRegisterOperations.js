/**
 * JavaScript file handling the login and registration operations
 */
(function() {
    function clearErrorMessages() {
        setErrorMessage("error-message", "");
        setErrorMessage("error-message-email", "");
        setErrorMessage("error-message-password", "");
        setErrorMessage("reg-error-message", "");
    }

    // show registration
    document.getElementById("show-registration").addEventListener("click", (e) => {
        e.preventDefault();
        document.getElementById("form-title").textContent = "Per registrarti, compila i seguenti campi";
        hideElement("login-form");
        showElement("registration-form");
        clearErrorMessages();
        resetForm("login-form");
        resetForm("registration-form");
    });

    // show login
    document.getElementById("show-login").addEventListener("click", (e) => {
        e.preventDefault();
        document.getElementById("form-title").textContent = "Per accedere inserisci le tue credenziali";
        hideElement("registration-form");
        showElement("login-form");
        clearErrorMessages();
        resetForm("login-form");
        resetForm("registration-form");
    });

    // handle login
    document.getElementById("login-button").addEventListener("click", (e) => {
        e.preventDefault();
        clearErrorMessages();
        showElement("loader");
        const relatedForm = e.target.closest("form");
        if (relatedForm.checkValidity()) {
            makeCall("POST", "/RichInternetApplication_war/CheckLogin", relatedForm, (x) => {
                if (x.readyState === XMLHttpRequest.DONE) {
                    const response = x.responseText;
                    switch (x.status) {
                        case 200:
                            let responseJson = JSON.parse(response);
                            sessionStorage.setItem("userName", responseJson["name"]);
                            sessionStorage.setItem("userSurname", responseJson["surname"]);
                            sessionStorage.setItem("userUsername", responseJson["username"]);
                            sessionStorage.setItem("userEmail", responseJson["email"]);
                            window.location.href = "workGroups.html";
                            break;
                        case 400:
                        case 401:
                        case 502:
                            setErrorMessage("error-message", response);
                            hideElement("loader");
                            break;
                        default:
                            setErrorMessage("error-message", "Problems during login");
                            hideElement("loader");
                    }
                }
            }, false);
        } else {
            relatedForm.reportValidity();
            setErrorMessage("error-message", "Tutti i campi devono essere compilati correttamente");
            hideElement("loader");
        }
    });

    // handle registration
    document.getElementById("register-button").addEventListener("click", (e) => {
        e.preventDefault();
        clearErrorMessages();
        showElement("reg-loader");
        const relatedForm = e.target.closest("form");
        if (relatedForm.checkValidity()) {
            const email = relatedForm.querySelector("#email").value;
            const password = relatedForm.querySelector("#reg-password").value;
            const passwordCheck = relatedForm.querySelector("#passwordCheck").value;
            let isValid = true;
            // check email syntactic validity and password equality
            if (!isValidEmail(email)) {
                setErrorMessage("error-message-email", "Email non valida")
                isValid = false;
            }
            if (password !== passwordCheck) {
                setErrorMessage("error-message-password", "La password non corrisponde")
                isValid = false;
            }
            if (!isValid) {
                hideElement("reg-loader")
                return
            }
            makeCall("POST", "/RichInternetApplication_war/CheckRegistration", relatedForm, (x) => {
                if (x.readyState === XMLHttpRequest.DONE) {
                    const response = x.responseText;
                    switch (x.status) {
                        case 200:
                            let responseJson = JSON.parse(response);
                            sessionStorage.setItem("userName", responseJson["name"]);
                            sessionStorage.setItem("userSurname", responseJson["surname"]);
                            sessionStorage.setItem("userUsername", responseJson["username"]);
                            sessionStorage.setItem("userEmail", responseJson["email"]);
                            window.location.href = "workGroups.html";
                            break;
                        case 400:
                        case 409:
                        case 502:
                            setErrorMessage("reg-error-message", response);
                            hideElement("reg-loader");
                            break;
                        default:
                            setErrorMessage("reg-error-message", "Problems during registration");
                            hideElement("reg-loader");
                    }
                }
            }, false);
        } else {
            relatedForm.reportValidity();
            setErrorMessage("reg-error-message", "Tutti i campi devono essere compilati correttamente");
            hideElement("reg-loader");
        }
    });

})();