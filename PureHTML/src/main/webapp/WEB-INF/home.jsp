<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Homepage</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="css/global.css">
</head>
<body class="home-body">
<div class="container-fluid">
    <div>
        <c:url value="Logout" var="logoutUrl" />
        <a href="${logoutUrl}" class="btn btn-outline-danger">Logout</a>
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
            <c:when test="${not empty requestScope.titlesCreatedGroups}">
                <p class="text-decoration-underline">Ecco la lista dei gruppi che hai creato. Seleziona un gruppo per vederne i dettagli.</p>
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
                                <c:forEach var="groupTitle" items="${requestScope.titlesCreatedGroups}">
                                    <tr>
                                        <td><c:out value="${groupTitle}" /></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <p class="alert alert-warning" role="alert">Non hai ancora creato alcun gruppo</p>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="text-center">
        <c:choose>
            <c:when test="${not empty requestScope.titlesParticipatingGroups}">
                <p class="text-decoration-underline">Ecco la lista dei gruppi a cui partecipi. Seleziona un gruppo per vederne i dettagli.</p>
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
                                <c:forEach var="groupTitle" items="${requestScope.titlesParticipatingGroups}">
                                    <tr>
                                        <td><c:out value="${groupTitle}" /></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <p class="alert alert-warning" role="alert">Non partecipi ancora a nessun gruppo</p>
            </c:otherwise>
        </c:choose>
    </div>
    <c:url value="/CreateGroup" var="createUrl" />
    <form method="post" action="${createUrl}">
        <div class="text-center">
            <div class="bottom-margin">
                <button type="submit" class="btn btn-outline-primary">CREA</button>
            </div>
        </div>
    </form>
</div>
</body>
</html>
