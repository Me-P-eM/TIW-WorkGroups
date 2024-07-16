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

        this.button.addEventListener("click", () => {
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

        this.show = function(type, text) {
            this.hide();
            setMessage(this.container, text);
            this.container.classList.add("alert-" + type);
            showElement(this.container);
        }

        this.hide = function() {
            hideElement(this.container);
            clearMessage(this.container);
            this.container.classList.remove("alert-success", "alert-info", "alert-warning", "alert-danger");
        };
    }

    // building the view related to homepage
    function HomePage(_alertBox, _title, _outsideContainer, _subtitle, _details1, _createdGroupsContainer, _createdGroups,
                      _details2, _invitedGroupsContainer, _invitedGroups, _groupCreation, _groupTitle, _activity, _min, _max,
                      _homeErrorMessage, _loader, _create, _pageOrchestrator) {
        this.alertBox = _alertBox;
        this.title = _title;
        this.outsideContainer = _outsideContainer;
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
            this.sendGroupParameters(e);
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
                                this.updateElement(this.details1, "alert alert-info", "alert", "Non ci sono gruppi attivi che hai creato");
                                hideElement(this.createdGroupsContainer);
                            } else {
                                this.updateElement(this.details1, "text-decoration-underline", "", "Ecco la lista dei gruppi ancora attivi che hai creato. Seleziona un gruppo per visualizzarne i dettagli.");
                                this.updateGroups(this.createdGroups, createdGroups);
                                showElement(this.createdGroupsContainer);
                            }
                            if (invitedGroups.length === 0) {
                                this.updateElement(this.details2, "alert alert-info", "alert", "Non ci sono gruppi attivi a cui sei stato invitato");
                                hideElement(this.invitedGroupsContainer);
                            } else {
                                this.updateElement(this.details2, "text-decoration-underline", "", "Ecco la lista dei gruppi ancora attivi a cui sei stato invitato. Seleziona un gruppo per visualizzarne i dettagli.");
                                this.updateGroups(this.invitedGroups, invitedGroups);
                                showElement(this.invitedGroupsContainer);
                            }
                            showElement(this.outsideContainer);
                            break;
                        case 400:
                        case 500:
                        case 502:
                            this.alertBox.show("warning", x.responseText);
                            break;
                        case 401:
                        case 403:
                            this.hide();
                            this.alertBox.show("danger", "Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("danger", "Something went wrong");
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

        this.sendGroupParameters = function (e) {
            clearMessage(this.homeErrorMessage);
            hideElement(this.create)
            showElement(this.loader);
            const relatedForm = e.target.closest("form");
            if (relatedForm.checkValidity()) {
                if (parseInt(this.min.value) > parseInt(this.max.value)  && this.min.value !== '' && this.max.value !== '') {
                    setMessage(this.homeErrorMessage, "Il numero minimo non può essere più grande del numero massimo!");
                    hideElement(this.loader);
                    showElement(this.create);
                    return;
                }
                makeCall("POST", "/RichInternetApplication_war/CheckGroupParameters", relatedForm, (x) => {
                    if (x.readyState === XMLHttpRequest.DONE) {
                        const response = x.responseText;
                        switch (x.status) {
                            case 200:
                                let responseJson = JSON.parse(response);
                                sessionStorage.setItem("groupCreator", responseJson["group"]["creator"]);
                                sessionStorage.setItem("groupTitle", responseJson["group"]["title"]);
                                sessionStorage.setItem("groupActivity", responseJson["group"]["activity"]);
                                sessionStorage.setItem("groupMin", responseJson["group"]["min"]);
                                sessionStorage.setItem("groupMax", responseJson["group"]["max"]);
                                sessionStorage.setItem("attempts", "0");
                                hideElement(this.loader);
                                resetForm(this.groupCreation);
                                showElement(this.create);
                                this.pageOrchestrator.transitionToRegistry();
                                break;
                            case 400:
                            case 500:
                            case 502:
                                setMessage(this.homeErrorMessage, response);
                                hideElement(this.loader);
                                showElement(this.create);
                                break;
                            case 401:
                            case 403:
                                this.hide();
                                this.alertBox.show("danger", "Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                                break;
                            default:
                                setMessage(this.homeErrorMessage, "Something went wrong");
                                hideElement(this.loader);
                                showElement(this.create);
                        }
                    }
                });
            } else {
                relatedForm.reportValidity();
                setMessage(this.homeErrorMessage, "Tutti i campi devono essere compilati correttamente");
                hideElement(this.loader);
                showElement(this.create);
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
            hideElement(this.outsideContainer);
        }
    }

    // building the view related to group details
    function GroupDetails(_alertBox, _title, _outsideContainer, _group, _details, _participants, _showHome, _trashBin, _pageOrchestrator) {
        this.alertBox = _alertBox;
        this.title = _title;
        this.outsideContainer = _outsideContainer;
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
                this.alertBox.show("warning", "Non puoi effettuare quest'operazione perché il numero " +
                    "minimo di partecipanti dev'essere rispettato");
            }
        });
        this.showHome.addEventListener("click", () => {
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
                            setMessage(this.title, "Dettagli del gruppo: " + resultGroup["title"]);
                            this.updateView(creator)
                            if (sessionStorage.getItem("userUsername") === creator["username"]) {
                                showElement(this.details);
                                showElement(this.trashBin);
                            } else {
                                hideElement(this.trashBin);
                                hideElement(this.details);
                            }
                            showElement(this.outsideContainer);
                            break;
                        case 400:
                        case 500:
                        case 502:
                            this.alertBox.show("warning", x.responseText);
                            break;
                        case 401:
                        case 403:
                            this.hide();
                            this.alertBox.show("danger", "Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("danger", "Something went wrong");
                    }
                });
        }

        this.updateView = function(creator) {
            this.group.innerHTML = "";
            let row = document.createElement("tr");
            const groupDetails = [
                resultGroup["title"],
                creator["surname"] + " " + creator["name"],
                resultGroup["creation"],
                resultGroup["activity"],
                resultGroup["min"],
                resultGroup["max"]
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
            resultInvitees.forEach(addParticipantRow);
        }

        this.removeParticipant = (groupID, participantID) => {
            const encodedUsername = encodeURIComponent(participantID);
            makeCall("POST", "/RichInternetApplication_war/RemoveParticipant?groupID=" + groupID + "&username=" + encodedUsername,
                null,
                (x) => {
                    switch (x.status) {
                        case 200:
                            this.hide();
                            this.show(groupID);
                            this.alertBox.show("success", "Partecipante rimosso con successo");
                            break;
                        case 400:
                        case 403:
                        case 500:
                        case 502:
                            this.alertBox.show("warning", x.responseText);
                            break;
                        case 401:
                            this.hide();
                            this.alertBox.show("danger", "Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("danger", "Something went wrong");
                    }
                });
        };

        this.hide = function() {
            clearMessage(this.title)
            this.group.innerHTML = "";
            this.participants.innerHTML = "";
            hideElement(this.outsideContainer);
        }
    }

    // building the view related to registry
    function Registry(_alertBox, _title, _outsideContainer, _closeButton, _groupModalDescription1, _groupModalDescription2,
                      _userList, _attempts, _errorMessageModal, _clearSelection, _submitSelection, _pageOrchestrator) {
        this.alertBox = _alertBox;
        this.title = _title;
        this.outsideContainer = _outsideContainer;
        this.closeButton = _closeButton;
        this.groupModalDescription1 = _groupModalDescription1;
        this.groupModalDescription2 = _groupModalDescription2;
        this.userList = _userList;
        this.attempts = _attempts;
        this.errorMessageModal = _errorMessageModal;
        this.clearSelection = _clearSelection;
        this.submitSelection = _submitSelection;
        this.pageOrchestrator = _pageOrchestrator;

        this.closeButton.addEventListener("click", () => {
            this.pageOrchestrator.transitionToHome();
        });
        this.clearSelection.addEventListener("click", (e) => {
            e.preventDefault();
            const checkboxes = this.userList.querySelectorAll("input[type='checkbox']");
            checkboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
        })
        this.submitSelection.addEventListener("click", (e) => {
            e.preventDefault();
            this.submit();
        })

        this.show = () => {
            makeCall("GET", "/RichInternetApplication_war/GoToRegistry", null,
                (x) => {
                    switch(x.status) {
                        case 200:
                            setMessage(this.groupModalDescription1, "Creazione del gruppo: " + sessionStorage.getItem("groupTitle"));
                            setMessage(this.groupModalDescription2, "Giorni di attività: " + sessionStorage.getItem("groupActivity") +
                                                                             ", Partecipanti minimi: " + sessionStorage.getItem("groupMin") +
                                                                             ", Partecipanti massimi: " + sessionStorage.getItem("groupMax"));
                            const responseAsJson = JSON.parse(x.responseText);
                            const users = responseAsJson["users"];
                            this.updateUsers(users);
                            setMessage(this.attempts, "Tentativi rimasti: " + (3 - sessionStorage.getItem("attempts")));
                            showElement(this.outsideContainer);
                            break;
                        case 400:
                        case 500:
                        case 502:
                            this.alertBox.show("warning", x.responseText);
                            break;
                        case 401:
                        case 403:
                            this.hide();
                            this.alertBox.show("danger", "Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                            break;
                        default:
                            this.alertBox.show("danger", "Something went wrong");
                    }
                });
        }

        this.updateUsers = (users) => {
            this.userList.innerHTML = "";
            users.forEach((user) => {
                let row = document.createElement("tr");
                let selectCell = document.createElement("td");
                let selectCheckbox = document.createElement("input");
                selectCheckbox.type = "checkbox";
                selectCheckbox.className = "form-check-input";
                selectCheckbox.value = user["username"];
                selectCell.appendChild(selectCheckbox);
                row.appendChild(selectCell);
                let surnameCell = document.createElement("td");
                surnameCell.appendChild(document.createTextNode(user["surname"]));
                row.appendChild(surnameCell);
                let nameCell = document.createElement("td");
                nameCell.appendChild(document.createTextNode(user["name"]));
                row.appendChild(nameCell);
                this.userList.appendChild(row);
            });
        };

        this.submit = function() {
            const selectedUsers = [];
            const checkboxes = this.userList.querySelectorAll("input[type='checkbox']:checked");
            checkboxes.forEach(checkbox => {
                selectedUsers.push(checkbox.value);
            });
            if (sessionStorage.getItem("attempts") === null || sessionStorage.getItem("groupTitle") === null) {
                this.pageOrchestrator.transitionToHome();
                this.alertBox.show("warning", "Badly formatted request parameters");
            }
            let validSelection = true;
            if (selectedUsers.length+1 < sessionStorage.getItem("groupMin")) {
                validSelection = false;
                setMessage(this.errorMessageModal, "Troppo pochi utenti selezionati, aggiungerne almeno " +
                                                            (sessionStorage.getItem("groupMin") - (selectedUsers.length+1)))
            } else if (selectedUsers.length+1 > sessionStorage.getItem("groupMax")) {
                validSelection = false;
                setMessage(this.errorMessageModal, "Troppi utenti selezionati, eliminarne almeno " +
                                                            ((selectedUsers.length+1) - sessionStorage.getItem("groupMax")))
            }
            let attempts = parseInt(sessionStorage.getItem("attempts"));
            if (!validSelection) {
                attempts++;
                sessionStorage.setItem("attempts", attempts.toString());
                setMessage(this.attempts, "Tentativi rimasti: " + (3 - attempts));
            }
            if (attempts < 3) {
                makeJsonCall("POST", "/RichInternetApplication_war/CheckInvitees", selectedUsers,
                    (x) => {
                        switch (x.status) {
                            case 200:
                                sessionStorage.removeItem("groupCreator");
                                sessionStorage.removeItem("groupTitle");
                                sessionStorage.removeItem("groupActivity",);
                                sessionStorage.removeItem("groupMin");
                                sessionStorage.removeItem("groupMax");
                                sessionStorage.removeItem("attempts");
                                this.pageOrchestrator.transitionToHome();
                                this.alertBox.show("success", x.responseText);
                                break;
                            case 400:
                            case 500:
                            case 502:
                                setMessage(this.errorMessageModal, x.responseText);
                                break;
                            case 401:
                            case 403:
                                this.hide();
                                this.alertBox.show("danger", "Non sei autorizzato a vedere questa pagina. Premi sul bottone di logout");
                                break;
                            default:
                                setMessage(this.errorMessageModal, "Something went wrong");
                        }
                    });
            } else {
                sessionStorage.removeItem("groupCreator");
                sessionStorage.removeItem("groupTitle");
                sessionStorage.removeItem("groupActivity",);
                sessionStorage.removeItem("groupMin");
                sessionStorage.removeItem("groupMax");
                sessionStorage.removeItem("attempts");
                this.pageOrchestrator.transitionToCancellation();
            }
        }

        this.hide = function() {
            clearMessage(this.groupModalDescription1);
            clearMessage(this.groupModalDescription2);
            this.userList.innerHTML = "";
            clearMessage(this.attempts);
            clearMessage(this.errorMessageModal);
            hideElement(this.outsideContainer)
        }
    }

    // building the view related to cancellation
    function Cancellation(_outsideContainer, _homeButton, _pageOrchestrator) {
        this.outsideContainer = _outsideContainer;
        this.homeButton = _homeButton;
        this.pageOrchestrator = _pageOrchestrator;

        this.homeButton.addEventListener("click", () => {
            this.pageOrchestrator.transitionToHome();
        });

        this.show = () => {
            showElement(this.outsideContainer);
        }

        this.hide = function () {
            hideElement(this.outsideContainer);
        }
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
        const homeOutsideContainer = document.getElementById("home");
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
        this.homePage = new HomePage(this.alertBox, title, homeOutsideContainer, subtitle, details1, createdGroupsContainer,
                                     createdGroups, details2, invitedGroupsContainer, invitedGroups, groupCreation,
                                     groupTitle, activity, min, max, homeErrorMessage, loader, create, this);

        // accessing elements related to groupDetails
        const groupDetailsOutsideContainer = document.getElementById("groupDetails");
        const group = document.getElementById("group");
        const details = document.getElementById("details");
        const participants = document.getElementById("participants");
        const showHome = document.getElementById("show-home");
        const trashBin = document.getElementById("trash-bin");
        this.groupDetails = new GroupDetails(this.alertBox, title, groupDetailsOutsideContainer, group, details, participants,
                                             showHome, trashBin, this);

        // accessing elements related to registry
        const registryOutsideContainer = document.getElementById("registry");
        const closeButton = document.getElementById("closeButton");
        const groupModalDescription1 = document.getElementById("groupModalDescription1");
        const groupModalDescription2 = document.getElementById("groupModalDescription2");
        const userList = document.getElementById("userList");
        const attempts = document.getElementById("attempts");
        const errorMessageModal = document.getElementById("errorMessageModal");
        const clearSelection = document.getElementById("clearSelection");
        const submitSelection = document.getElementById("submitSelection");
        this.registry = new Registry(this.alertBox, title, registryOutsideContainer, closeButton, groupModalDescription1,
                                     groupModalDescription2, userList, attempts, errorMessageModal,
                                     clearSelection, submitSelection, this);

        // accessing elements related to cancellation
        const cancellationOutsideContainer = document.getElementById("cancellation");
        const homeButton = document.getElementById("return-home");
        this.cancellation = new Cancellation(cancellationOutsideContainer, homeButton, this);

        // starting the page orchestrator
        this.start = () => {
            // instantiate and show home
            console.log("PageOrchestrator started");
            this.transitionToHome();
        };

        this.hideAll = () => {
            this.homePage.hide();
            this.groupDetails.hide();
            this.registry.hide();
            this.cancellation.hide();
            this.alertBox.hide();
        }

        this.transitionToHome = () => {
            this.hideAll();
            this.homePage.show();
        }

        this.transitionToGroupDetails = (groupID) => {
            this.hideAll();
            this.groupDetails.show(groupID);
        }

        this.transitionToRegistry = () => {
            this.registry.show();
        }

        this.transitionToCancellation = () => {
            this.hideAll();
            this.cancellation.show();
        }
    }
}