/**
 * JavaScript file handling operations through the execution of functionalities
 */
{
    window.addEventListener("load", () => {
        let pageOrchestrator = new PageOrchestrator();

        if (sessionStorage.getItem("userUsername") == null) {
            window.location.href = "index.html";
        } else {
            pageOrchestrator.start(); // initialize the components
        } // display initial content
    }, false);

    // here every single view component is built

    //building logout
    function LogoutButton(_logoutButton) {
        this.button = _logoutButton;
        this.button.addEventListener("click", (e) => {
            makeCall("GET", "/RichInternetApplication_war/Logout", null,
                () => {
                    sessionStorage.clear();
                    window.location.href = "index.html";
                });
        });
    }

    // finally the page orchestrator is built
    function PageOrchestrator() {
        const logoutButton = document.getElementById("logout-button");
        this.logout = new LogoutButton(logoutButton);

        // starting the page orchestrator
        this.start = ()=>{
            console.log("PageOrchestrator started");
        };
    }
}