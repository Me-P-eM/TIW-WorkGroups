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
        <a class="btn btn-outline-danger" href="Logout">Logout</a>
    </div>
    <div class="text-center">
        <h1 class="title">GRUPPI DI LAVORO</h1>
        <h2>
            Bentornato,
            <c:out value="${sessionScope.user.username}"/>
        </h2>
    </div>
    <div class="text-center">
        <c:if test="${not empty groups}">
            <p>Ecco la lista dei gruppi che hai creato. Seleziona un gruppo per vedere i dettagli.</p>
            <div class="container-fluid">
                <div class="row justify-content-md-center">
                    <div class="col col-lg">
                        <table class="table table-bordered table-hover">
                            <thead class="thead-dark">
                            <tr>
                                <th>Titolo</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>
