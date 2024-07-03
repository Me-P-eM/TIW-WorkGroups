<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Dettagli del gruppo</title>
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
                    Dettagli del gruppo:
                    <c:out value="${requestScope.group.title}"/>
                </h1>
                <div class="text-center">
                    <table class="table table-bordered table-hover">
                        <thead class="table-head">
                        <tr>
                            <th>Titolo</th>
                            <th>Creatore</th>
                            <th>Data di creazione</th>
                            <th>Giorni di attività</th>
                            <th>Minimo di partecipanti</th>
                            <th>Massimo di partecipanti</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>${requestScope.group.title}</td>
                            <td>${requestScope.creator.surname} ${requestScope.creator.name}</td>
                            <td>${requestScope.group.creation}</td>
                            <td>${requestScope.group.activity}</td>
                            <td>${requestScope.group.min}</td>
                            <td>${requestScope.group.max}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="text-center">
                <h2>Partecipanti</h2>
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
                                <tr>
                                    <td><c:out value="${requestScope.creator.surname}" /></td>
                                    <td><c:out value="${requestScope.creator.name}" /></td>
                                </tr>
                            <c:forEach var="invitee" items="${requestScope.invitees}">
                                <tr>
                                    <td><c:out value="${invitee.surname}" /></td>
                                    <td><c:out value="${invitee.name}" /></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="text-center">
                <c:url value="GoToHome" var="homeUrl" />
                <a href="${homeUrl}" class="btn btn-outline-danger">INDIETRO</a>
            </div>
        </div>
    </body>
</html>
