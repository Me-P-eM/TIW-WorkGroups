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
                    <c:out value="${requestScope.title}"/>
                </h1>
                <h2>
                    Giorni di attività:
                    <c:out value="${requestScope.activity}"/>,
                    Partecipanti minimi:
                    <c:out value="${requestScope.min}"/>,
                    Partecipanti massimi:
                    <c:out value="${requestScope.max}"/>
                </h2>
            </div>
            <div class="text-center">
                <c:url value="/CheckInvitees" var="checkInviteesUrl" />
                <form method="post" action="${checkInviteesUrl}">
                    <h3>Anagrafica</h3>
                    <div class="container-fluid d-flex justify-content-center align-items-center">
                        <div class="w-100">
                            <table class="table table-bordered table-hover text-center">
                                <thead class="table-head">
                                <tr>
                                    <th class="w-50">Cognome</th>
                                    <th class="w-50">Nome</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="user" items="${requestScope.users}">
                                    <tr>
                                        <td><c:out value="${user.surname}" /></td>
                                        <td><c:out value="${user.name}" /></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </form>
            </div>
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
        </div>
    </body>
</html>
