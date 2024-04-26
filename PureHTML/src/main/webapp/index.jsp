<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
  <title>Gruppi di lavoro</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="css/global.css" />
</head>
<body class="login-body">
<div class="container">
  <div class="text-center">
    <h1 class="title">GRUPPI DI LAVORO</h1>
    <h2 class="bottom-margin">Per accedere inserisci le tue credenziali</h2>
    <c:url value="/CheckLogin" var="loginUrl" />
    <form method="post" action="${loginUrl}">
      <div class="form-group bottom-margin">
        <label for="username">Inserisci il tuo username</label>
        <input id="username" type="text" name="username" placeholder="Username" required value="${requestScope.username}">
      </div>
      <div class="form-group bottom-margin">
        <label for="password">Inserisci la tua password</label>
        <input id="password" type="password" placeholder="Password" name="password" required value="${requestScope.password}">
      </div>
      <c:if test="${not empty requestScope.errorMessage}">
        <div id="error-message" class="error-message bottom-margin">
          <p><c:out value="${requestScope.errorMessage}" /></p>
        </div>
      </c:if>
      <div class="bottom-margin">
        <button type="submit" class="btn btn-outline-primary">Accedi</button>
      </div>
      <div class="member-name-link">
        <p>Se non hai ancora un account, puoi registrarti
          <c:url value="/registration.jsp" var="registrationUrl" />
          <a href="${registrationUrl}">qui</a>
        </p>
      </div>
    </form>
  </div>
</div>
</body>
</html>
