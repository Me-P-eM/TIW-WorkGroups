/**
 * JavaScript file handling the login and registration operations
 */
(function() {
    const errorMessage = document.getElementById("error-message");
    const errorMessageEmail = document.getElementById("error-message-email");
    const errorMessagePassword = document.getElementById("error-message-password");
    const regErrorMessage = document.getElementById("reg-error-message");
    const loader = document.getElementById("loader");
    const regLoader = document.getElementById("reg-loader");
    const formTitle = document.getElementById("form-title");
    const loginForm = document.getElementById("login-form");
    const registrationForm = document.getElementById("registration-form");
    const showLogin = document.getElementById("show-login");
    const showRegistration = document.getElementById("show-registration");
    const loginButton = document.getElementById("login-button");
    const registerButton = document.getElementById("register-button");

    clearErrorMessages();
    hideElement(loader);
    hideElement(regLoader);

    function clearErrorMessages() {
        clearMessage(errorMessage);
        clearMessage(errorMessageEmail);
        clearMessage(errorMessagePassword);
        clearMessage(regErrorMessage);
    }

    // show registration
    showRegistration.addEventListener("click", (e) => {
        e.preventDefault();
        setMessage(formTitle, "Per registrarti, compila i seguenti campi");
        hideElement(loginForm);
        showElement(registrationForm);
        clearErrorMessages();
        resetForm(loginForm);
        resetForm(registrationForm);
    });

    // show login
    showLogin.addEventListener("click", (e) => {
        e.preventDefault();
        setMessage(formTitle, "Per accedere inserisci le tue credenziali");
        hideElement(registrationForm);
        showElement(loginForm);
        clearErrorMessages();
        resetForm(loginForm);
        resetForm(registrationForm);
    });

    // handle login
    loginButton.addEventListener("click", (e) => {
        e.preventDefault();
        clearErrorMessages();
        hideElement(loginButton);
        showElement(loader);
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
                            hideElement(loader);
                            resetForm(loginForm);
                            showElement(loginButton);
                            window.location.href = "workGroups.html";
                            break;
                        case 400:
                        case 401:
                        case 502:
                            setMessage(errorMessage, response);
                            hideElement(loader);
                            showElement(loginButton);
                            break;
                        default:
                            setMessage(errorMessage, "Something went wrong");
                            hideElement(loader);
                            showElement(loginButton);
                    }
                }
            }, false);
        } else {
            relatedForm.reportValidity();
            setMessage(errorMessage, "Tutti i campi devono essere compilati correttamente");
            hideElement(loader);
            showElement(loginButton);
        }
    });

    // handle registration
    registerButton.addEventListener("click", (e) => {
        e.preventDefault();
        clearErrorMessages();
        hideElement(registerButton);
        hideElement(showLogin);
        showElement(regLoader);
        const relatedForm = e.target.closest("form");
        if (relatedForm.checkValidity()) {
            const email = relatedForm.querySelector("#email").value;
            const password = relatedForm.querySelector("#reg-password").value;
            const passwordCheck = relatedForm.querySelector("#passwordCheck").value;
            let isValid = true;
            // check email syntactic validity and password equality
            if (!isValidEmail(email)) {
                setMessage(errorMessageEmail, "Email non valida");
                isValid = false;
            }
            if (password !== passwordCheck) {
                setMessage(errorMessagePassword, "La password non corrisponde");
                isValid = false;
            }
            if (!isValid) {
                hideElement(regLoader);
                showElement(showLogin);
                showElement(registerButton);
                return;
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
                            hideElement(regLoader);
                            resetForm(registrationForm);
                            showElement(showLogin);
                            showElement(registerButton);
                            window.location.href = "workGroups.html";
                            break;
                        case 400:
                        case 409:
                        case 502:
                            setMessage(regErrorMessage, response);
                            hideElement(regLoader);
                            showElement(showLogin);
                            showElement(registerButton);
                            break;
                        default:
                            setMessage(regErrorMessage, "Something went wrong");
                            hideElement(regLoader);
                            showElement(showLogin);
                            showElement(registerButton);
                    }
                }
            }, false);
        } else {
            relatedForm.reportValidity();
            setMessage(regErrorMessage, "Tutti i campi devono essere compilati correttamente");
            hideElement(regLoader);
            showElement(showLogin);
            showElement(registerButton);
        }
    });
})();