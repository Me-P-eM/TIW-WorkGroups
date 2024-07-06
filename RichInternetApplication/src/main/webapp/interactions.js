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

    // building logout
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

    // building alert box
    function AlertBox(_container) {
        this.container = _container;
        this.show = function(text) {
            showElement(this.container);
            setMessage(this.container, text);
        }
        this.hide = function() {
            hideElement(this.container);
            clearMessage(this.container);
        };
    }

    // building the view related to homepage
    function HomePage(_alertBox, _title, _container, _subtitle, _details1, _createdGroupsContainer, _createdGroups,
                      _details2, _invitedGroupsContainer, _invitedGroups, _groupCreation, _groupTitle, _activity, _min, _max,
                      _homeErrorMessage, _loader, _create, _pageOrchestrator) {
        this.alertBox = _alertBox;
        this.title = _title;
        this.outSideContainer = _container;
        this.subtitle = _subtitle;
        this.details1 = _details1;
        this.createdGroupsContainer = _createdGroupsContainer;
        this.createdGroups = _createdGroups;
        this.details2 = _details2;
        this.invitedGroupsContainer = _invitedGroupsContainer;
        this.invitedGroups = _invitedGroups;
        this.groupCreation = _groupCreation;
        this.groupTitle = _groupTitle;
        this.activity = _activity;
        this.min = _min;
        this.max = _max;
        this.homeErrorMessage = _homeErrorMessage;
        this.loader = _loader;
        this.create = _create;
        this.pageOrchestrator = _pageOrchestrator;

        this.create.addEventListener("click", (e) => {
            e.preventDefault();
            this.sendGroupParameters(e, this.create, this.groupCreation);
        });

        this.show = () => {
            makeCall("GET", "/RichInternetApplication_war/GoToHome", null,
                (x) => {
                    switch(x.status) {
                        case 200:
                            setMessage(this.title, "GRUPPI DI LAVORO");
                            setMessage(this.subtitle, "Bentornato, " + sessionStorage.getItem("userUsername"));
                            const responseAsJson = JSON.parse(x.responseText);
                            const createdGroups = responseAsJson["createdGroups"];
                            const invitedGroups = responseAsJson["invitedGroups"];
                            if (createdGroups.length === 0) {
                                this.updateElement(this.details1, "alert alert-warning", "alert", "Non ci sono gruppi attivi che hai creato");
                                hideElement(this.createdGroupsContainer);
                            } else {
                                this.updateElement(this.details1, "text-decoration-underline", "", "Ecco la lista dei gruppi ancora attivi che hai creato. Seleziona un gruppo per visualizzarne i dettagli.");
                                this.updateGroups(this.createdGroups, createdGroups);
                                showElement(this.createdGroupsContainer);
                            }
                            if (invitedGroups.length === 0) {
                                this.updateElement(this.details2, "alert alert-warning", "alert", "Non ci sono gruppi attivi a cui sei stato invitato");
                                hideElement(this.invitedGroupsContainer);
                            } else {
                                this.updateElement(this.details2, "text-decoration-underline", "", "Ecco la lista dei gruppi ancora attivi a cui sei stato invitato. Seleziona un gruppo per visualizzarne i dettagli.");
                                this.updateGroups(this.invitedGroups, invitedGroups);
                                showElement(this.invitedGroupsContainer);
                            }
                            showElement(this.outSideContainer);
                            break;
                        case 400:
                        case 500:
                        case 502:
                            this.alertBox.show(x.responseText);
                            break;
                        case 401:
                        case 403:
                            this.hide();
                            this.alertBox.show("Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("Something went wrong");
                    }
                });
        };

        this.updateGroups = function (table, groups) {
            let row, titleCell, titleAnchor, titleLink;
            table.innerHTML = "";
            groups.forEach(
                (group) => {
                    row = document.createElement("tr");
                    titleCell = document.createElement("td");
                    titleAnchor = document.createElement("a");
                    titleLink = document.createTextNode(group["title"]);
                    titleAnchor.appendChild(titleLink);
                    titleAnchor.href = "";
                    titleAnchor.addEventListener("click", (e) => {
                        e.preventDefault();
                        this.pageOrchestrator.transitionToGroupDetails(group["id"]);
                    });
                    titleCell.appendChild(titleAnchor);
                    row.appendChild(titleCell);
                    table.appendChild(row);
                }
            );
        };

        this.updateElement = function (element, className, role, message) {
            element.className = className;
            if (role) {
                element.setAttribute("role", role);
            } else {
                element.removeAttribute("role");
            }
            setMessage(element, message);
        };

        this.sendGroupParameters = function (e, button, form) {
            e.preventDefault();
            clearMessage(this.homeErrorMessage);
            hideElement(button)
            showElement(this.loader);
            const relatedForm = e.target.closest("form");
            if (relatedForm.checkValidity()) {
                if (this.min.value > this.max.value && this.min.value !== '' && this.max.value !== '') {
                    setMessage(this.homeErrorMessage, "Il numero minimo non può essere più grande del numero massimo!");
                    hideElement(this.loader);
                    showElement(button);
                    return;
                }
                makeCall("POST", "/RichInternetApplication_war/CheckGroupParameters", relatedForm, (x) => {
                    if (x.readyState === XMLHttpRequest.DONE) {
                        const response = x.responseText;
                        switch (x.status) {
                            case 200:
                                let responseJson = JSON.parse(response);
                                sessionStorage.setItem("groupCreator", responseJson["creator"]);
                                sessionStorage.setItem("groupTitle", responseJson["title"]);
                                sessionStorage.setItem("groupActivity", responseJson["activity"]);
                                sessionStorage.setItem("groupMin", responseJson["min"]);
                                sessionStorage.setItem("groupMax", responseJson["max"]);
                                sessionStorage.setItem("attempts", "0");
                                hideElement(this.loader);
                                resetForm(form);
                                showElement(button);
                                this.pageOrchestrator.transitionToRegistry();
                                break;
                            case 400:
                            case 500:
                            case 502:
                                setMessage(this.homeErrorMessage, response);
                                hideElement(this.loader);
                                showElement(button);
                                break;
                            case 401:
                            case 403:
                                this.hide();
                                this.alertBox.show("Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                                break;
                            default:
                                setMessage(this.homeErrorMessage, "Something went wrong");
                                hideElement(this.loader);
                                showElement(button);
                        }
                    }
                }, false);
            } else {
                relatedForm.reportValidity();
                setMessage(this.homeErrorMessage, "Tutti i campi devono essere compilati correttamente");
                hideElement(this.loader);
                showElement(button);
            }
        }

        this.hide = function() {
            clearMessage(this.title);
            clearMessage(this.subtitle);
            clearMessage(this.details1);
            this.createdGroups.innerHTML = "";
            clearMessage(this.details2);
            this.invitedGroups.innerHTML = "";
            clearMessage(this.homeErrorMessage);
            hideElement(this.outSideContainer);
        }
    }

    // building the view related to group details
    function GroupDetails(_alertBox, _title, _outSideContainer, _group, _details, _participants, _showHome, _trashBin, _pageOrchestrator) {
        this.alertBox = _alertBox;
        this.title = _title;
        this.outSideContainer = _outSideContainer;
        this.group = _group;
        this.details = _details;
        this.participants = _participants;
        this.showHome = _showHome;
        this.trashBin = _trashBin
        this.pageOrchestrator = _pageOrchestrator;
        let resultGroup, resultInvitees;

        this.trashBin.addEventListener("dragover", (e) => {
            e.preventDefault();
        });
        this.trashBin.addEventListener("drop", (e) => {
            e.preventDefault();
            if (resultInvitees.length >= resultGroup["min"]) {
                const participantID = e.dataTransfer.getData("text/plain");
                this.removeParticipant(resultGroup["groupID"], participantID);
            } else {
                this.alertBox.show("Non puoi effettuare quest'operazione perché il numero " +
                    "minimo di partecipanti dev'essere rispettato");
            }
        });
        this.showHome.addEventListener("click", (e) => {
            e.preventDefault();
            this.pageOrchestrator.transitionToHome();
        });

        this.show = (groupID) => {
            makeCall("GET", "/RichInternetApplication_war/GetGroupDetails?groupID=" + groupID, null,
                (x) => {
                    switch(x.status) {
                        case 200:
                            const responseAsJson = JSON.parse(x.responseText);
                            const group = responseAsJson["group"];
                            resultGroup = group;
                            const creator = responseAsJson["creator"];
                            const invitees = responseAsJson["invitees"];
                            resultInvitees = invitees;
                            this.updateView(group, creator, invitees)
                            if (sessionStorage.getItem("userUsername") === creator["username"]) {
                                showElement(this.details);
                                showElement(this.trashBin);
                            } else {
                                hideElement(this.trashBin);
                                hideElement(this.details);
                            }
                            showElement(this.outSideContainer);
                            break;
                        case 400:
                        case 500:
                        case 502:
                            this.alertBox.show(x.responseText);
                            break;
                        case 401:
                        case 403:
                            this.hide();
                            this.alertBox.show("Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("Something went wrong");
                    }
                });
        }

        this.updateView = function(group, creator, invitees) {
            setMessage(this.title, "Dettagli del gruppo: " + group["title"]);
            this.group.innerHTML = "";
            let row = document.createElement("tr");
            const groupDetails = [
                group["title"],
                creator["surname"] + " " + creator["name"],
                group["creation"],
                group["activity"],
                group["min"],
                group["max"]
            ];
            groupDetails.forEach(detail => {
                let cell = document.createElement("td");
                cell.appendChild(document.createTextNode(detail));
                row.appendChild(cell);
            });
            this.group.appendChild(row);
            this.participants.innerHTML = "";
            const addParticipantRow = (participant) => {
                let row = document.createElement("tr");
                if (sessionStorage.getItem("userUsername") === creator["username"] && participant["username"] !== creator["username"]) {
                    row.draggable = true;
                    row.dataset.participantID = participant["username"];
                    row.addEventListener("dragstart", (e) => {
                        e.dataTransfer.setData("text/plain", e.target.dataset.participantID);
                    });
                }
                let surnameCell = document.createElement("td");
                surnameCell.appendChild(document.createTextNode(participant["surname"]));
                row.appendChild(surnameCell);
                let nameCell = document.createElement("td");
                nameCell.appendChild(document.createTextNode(participant["name"]));
                row.appendChild(nameCell);
                this.participants.appendChild(row);
            };
            addParticipantRow(creator);
            invitees.forEach(addParticipantRow);
        }

        this.removeParticipant = (groupID, participantID) => {
            makeCall("POST", "/RichInternetApplication_war/RemoveParticipant?groupID=" + groupID + "&username=" + participantID,
                null,
                (x) => {
                    switch (x.status) {
                        case 200:
                            clearMessage(this.title);
                            hideElement(this.outSideContainer);
                            this.show(groupID);
                            this.alertBox.show("Partecipante rimosso con successo");
                            break;
                        case 400:
                        case 403:
                        case 500:
                        case 502:
                            this.alertBox.show(x.responseText);
                            break;
                        case 401:
                            this.hide();
                            this.alertBox.show("Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("Something went wrong");
                    }
                });
        };

        this.hide = function() {
            clearMessage(this.title)
            this.group.innerHTML = "";
            this.participants.innerHTML = "";
            hideElement(this.outSideContainer);
        }
    }

    // building the view related to registry
    function Registry() {

    }

    // building the view related to cancellation
    function Cancellation() {

    }

    // finally the page orchestrator is built
    function PageOrchestrator() {
        // accessing logout button
        const logoutButton = document.getElementById("logout-button");
        this.logout = new LogoutButton(logoutButton);

        // accessing alert box
        const alertContainer = document.getElementById("alertLabel");
        this.alertBox = new AlertBox(alertContainer);

        // accessing title
        const title = document.getElementById("title");

        // accessing elements related to home
        const homeOutSideCourseContainer = document.getElementById("home");
        const subtitle = document.getElementById("subtitle");
        const details1 = document.getElementById("details1");
        const createdGroupsContainer = document.getElementById("createdGroupsContainer");
        const createdGroups = document.getElementById("createdGroups");
        const details2 = document.getElementById("details2");
        const invitedGroupsContainer = document.getElementById("invitedGroupsContainer");
        const invitedGroups = document.getElementById("invitedGroups");
        const groupCreation = document.getElementById("groupCreation");
        const groupTitle = document.getElementById("groupTitle");
        const activity = document.getElementById("activity");
        const min = document.getElementById("min");
        const max = document.getElementById("max");
        const homeErrorMessage = document.getElementById("home-error-message");
        const loader = document.getElementById("loader");
        const create = document.getElementById("create");
        this.homePage = new HomePage(this.alertBox, title, homeOutSideCourseContainer, subtitle, details1, createdGroupsContainer,
                                     createdGroups, details2, invitedGroupsContainer, invitedGroups, groupCreation,
                                     groupTitle, activity, min, max, homeErrorMessage, loader, create, this);

        // accessing elements related to groupDetails
        const detailsOutSideCourseContainer = document.getElementById("groupDetails");
        const group = document.getElementById("group");
        const details = document.getElementById("details");
        const participants = document.getElementById("participants");
        const showHome = document.getElementById("show-home");
        const trashBin = document.getElementById("trash-bin");
        this.groupDetails = new GroupDetails(this.alertBox, title, detailsOutSideCourseContainer, group, details, participants, showHome, trashBin, this);

        // accessing elements related to registry


        // accessing elements related to cancellation


        // starting the page orchestrator
        this.start = () => {
            // instantiate and hide everything
            console.log("PageOrchestrator started");
            this.transitionToHome();
        };

        this.transitionToHome = ()=> {
            this.alertBox.hide();
            this.groupDetails.hide();
            this.homePage.show();
        }

        this.transitionToGroupDetails = (groupID)=> {
            this.alertBox.hide();
            this.homePage.hide();
            this.groupDetails.show(groupID);
        }

        this.transitionToRegistry = ()=> {
            this.alertBox.hide();
            this.homePage.hide();
            //--------
        }

        this.transitionToCancellation = () => {
            //---------------
        }
    }
}