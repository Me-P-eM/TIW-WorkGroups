<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <title>HOME</title>
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
                <h1 class="title">GRUPPI DI LAVORO</h1>
                <h2>
                    Bentornato,
                    <c:out value="${sessionScope.user.username}"/>
                </h2>
            </div>
            <div class="text-center">
                <c:choose>
                    <c:when test="${not empty requestScope.createdGroups}">
                        <p class="text-decoration-underline">Ecco la lista dei gruppi ancora attivi che hai creato. Seleziona un gruppo per visualizzarne i dettagli.</p>
                        <div class="container-fluid">
                            <div class="row justify-content-md-center">
                                <div class="col col-lg">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-head">
                                            <tr>
                                                <th>Titolo</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="group" items="${requestScope.createdGroups}">
                                            <tr>
                                                <td>
                                                    <c:url value="/GetGroupDetails" var="groupDetailsUrl">
                                                        <c:param name="groupID" value="${group.groupID}"/>
                                                    </c:url>
                                                    <a href="${groupDetailsUrl}"><c:out value="${group.title}" /></a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="alert alert-info" role="alert">Non ci sono gruppi attivi che hai creato</p>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="text-center">
                <c:choose>
                    <c:when test="${not empty requestScope.invitedGroups}">
                        <p class="text-decoration-underline">Ecco la lista dei gruppi ancora attivi a cui sei stato invitato. Seleziona un gruppo per visualizzarne i dettagli.</p>
                        <div class="container-fluid">
                            <div class="row justify-content-md-center">
                                <div class="col col-lg">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-head">
                                        <tr>
                                            <th>Titolo</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="group" items="${requestScope.invitedGroups}">
                                            <tr>
                                                <td>
                                                    <c:url value="/GetGroupDetails" var="groupDetailsUrl">
                                                        <c:param name="groupID" value="${group.groupID}"/>
                                                    </c:url>
                                                    <a href="${groupDetailsUrl}"><c:out value="${group.title}" /></a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="alert alert-info" role="alert">Non ci sono gruppi attivi a cui sei stato invitato</p>
                    </c:otherwise>
                </c:choose>
            </div>
            <p class="text-center text-decoration-underline top-margin">Per creare un gruppo, compila i seguenti campi:</p>
            <c:url value="/CheckGroupParameters" var="checkGroupParametersUrl" />
            <form method="post" action="${checkGroupParametersUrl}">
                <div class="text-center row bottom-margin lateral-margin">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="title">Inserisci il titolo del gruppo</label>
                            <br>
                            <input id="title" type="text" name="title" placeholder="Titolo" required value="${requestScope.title}">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="activity">Inserisci la durata di attività del gruppo (in giorni)</label>
                            <br>
                            <input id="activity" type="number" name="activity" placeholder="Attività" min="1" step="1" required value="${requestScope.activity}">
                        </div>
                    </div>
                </div>
                <div class="text-center row lateral-margin">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="min">Inserisci il numero minimo di partecipanti</label>
                            <br>
                            <input id="min" type="number" name="min" placeholder="Minimo"  min="1" step="1" required value="${requestScope.min}">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="max">Inserisci il numero massimo di partecipanti</label>
                            <br>
                            <input id="max" type="number" name="max" placeholder="Massimo"  min="1" step="1" required value="${requestScope.max}">
                        </div>
                    </div>
                </div>
                <c:if test="${not empty requestScope.errorMessage}">
                <div id="error-message" class="text-center error-message">
                    <p><c:out value="${requestScope.errorMessage}" /></p>
                </div>
                </c:if>
                <div class="text-center top-margin">
                    <button type="submit" class="btn btn-outline-primary">INVIA</button>
                </div>
            </form>
        </div>
    </body>
</html>
