/**
 * JavaScript file handling the login operation that occurs by pressing
 * the "login" button in "index.html"
 */
(function() {
    document.getElementById("error-message").textContent = "";
    document.getElementById("loader").style.display = "none";
    document.getElementById("login-button").addEventListener("click",
        (e) => {
            document.getElementById("error-message").textContent = "";
            document.getElementById("loader").style.display = "block";
            e.preventDefault();
            var relatedForm = e.target.closest("form");
            if(relatedForm.checkValidity()) {
                console.log("making call");
                makeCall("POST", "/RichInternetApplication_war/CheckLogin", e.target.closest("form"),
                    (x) => {
                        if (x.readyState === XMLHttpRequest.DONE) {
                            var response = x.responseText;
                            switch(x.status) {
                                case 200:
                                    let responseJson = JSON.parse(response);
                                    sessionStorage.setItem("userName", responseJson["name"]);
                                    sessionStorage.setItem("userSurname", responseJson["surname"]);
                                    sessionStorage.setItem("userUsername", responseJson["username"]);
                                    sessionStorage.setItem("userEmail", responseJson["email"]);
                                    console.log(responseJson);
                                    window.location.href = "workGroups.html"
                                    break;
                                case 400:
                                case 401:
                                case 502:
                                    console.log(response);
                                    document.getElementById("error-message").textContent = response;
                                    document.getElementById("loader").style.display = "none";
                                    break;
                                default:
                                    document.getElementById("error-message").textContent = "Problems during login operations";
                                    document.getElementById("loader").style.display = "none";
                                    console.log("got " + x.status);
                            }
                        }
                    }, false);
            } else {
                relatedForm.reportValidity();
                document.getElementById("error-message").textContent = "Tutti i campi devono essere compilati correttamente";
                document.getElementById("loader").style.display = "none";
            }
        });
})();