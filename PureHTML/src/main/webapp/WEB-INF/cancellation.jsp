<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Cancellazione</title>
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
            <div class="text-center top-margin">
                <h1 class="error-message">Tre tentativi di definire un gruppo con un numero di partecipanti errato, il gruppo non sarà creato</h1>
                <c:url value="GoToHome" var="homeUrl" />
                <a href="${homeUrl}" class="btn btn-outline-danger">HOME</a>
            </div>
        </div>
    </body>
</html>
