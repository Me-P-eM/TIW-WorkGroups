<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Seleziona invitati</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="css/global.css">
    </head>
    <body class="body-main">
        <div class="container-fluid">
            <div>
                <c:url value="Logout" var="logoutUrl" />
                <a href="${logoutUrl}" class="btn btn-outline-danger">LOGOUT</a>
            </div>
            <div class="text-center">
                <h1 class="title">
                    Creazione del gruppo:
                    <c:out value="${sessionScope.group.title}"/>
                </h1>
                <h2>
                    Giorni di attività:
                    <c:out value="${sessionScope.group.activity}"/>,
                    Partecipanti minimi:
                    <c:out value="${sessionScope.group.min}"/>,
                    Partecipanti massimi:
                    <c:out value="${sessionScope.group.max}"/>
                </h2>
            </div>
            <div class="text-center">
                <p class="text-decoration-underline">Ricorda che, oltre agli invitati, anche tu conti come partecipante</p>
            </div>
            <c:url value="/CheckInvitees" var="checkInviteesUrl" />
            <form method="post" action="${checkInviteesUrl}">
                <div class="text-center">
                    <h3>Anagrafica</h3>
                    <div class="container-fluid d-flex justify-content-center align-items-center">
                        <div class="w-100">
                            <table class="table table-bordered table-hover text-center">
                                <thead class="table-head">
                                <tr>
                                    <th class="p-10 align-middle-center">Seleziona</th>
                                    <th class="w-50">Cognome</th>
                                    <th class="w-50">Nome</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="user" items="${requestScope.users}">
                                    <tr>
                                        <td class="pd-0 text-center">
                                            <c:set var="isChecked" value="false"/>
                                            <c:forEach var="invitee" items="${requestScope.selectedUsers}">
                                                <c:if test="${user.username eq invitee}">
                                                    <c:set var="isChecked" value="true"/>
                                                </c:if>
                                            </c:forEach>
                                            <input type="checkbox" name="selectedUsers" value="${user.username}" ${isChecked eq 'true' ? 'checked' : ''}/>
                                        </td>
                                        <td><c:out value="${user.surname}" /></td>
                                        <td><c:out value="${user.name}" /></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="text-center row lateral-margin">
                    <div id="remaining-attempts">
                        <h4>
                            Tentativi rimasti:
                            <c:out value="${3-sessionScope.attempts}" />
                        </h4>
                    </div>
                </div>
                <c:if test="${not empty requestScope.errorMessage}">
                <div class="text-center row lateral-margin">
                    <div id="error-message" class="error-message bottom-margin">
                        <p><c:out value="${requestScope.errorMessage}" /></p>
                    </div>
                </div>
                </c:if>
                <div class="text-center row lateral-margin">
                    <div class="col-md-6">
                        <div>
                            <c:url value="GoToHome" var="homeUrl" />
                            <a href="${homeUrl}" class="btn btn-outline-danger">INDIETRO</a>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div>
                            <button type="submit" class="btn btn-outline-primary">INVITA</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
