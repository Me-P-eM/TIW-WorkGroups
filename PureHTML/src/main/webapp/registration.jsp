<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Gruppi di lavoro - Registrazione</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="css/global.css">
    </head>
    <body class="body-login">
        <div class="container">
            <div class="text-center">
                <h1 class="title">GRUPPI DI LAVORO</h1>
                <h2 class="bottom-margin">Per registrarti, compila i seguenti campi</h2>
                <c:url value="/CheckRegistration" var="registrationUrl" />
                <form method="post" action="${registrationUrl}">
                    <div class="row bottom-margin lateral-margin">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="name">Inserisci il tuo nome</label>
                                <br>
                                <input id="name" type="text" name="name" placeholder="Nome" required value="${requestScope.name}">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="surname">Inserisci il tuo cognome</label>
                                <br>
                                <input id="surname" type="text" name="surname" placeholder="Cognome" required value="${requestScope.surname}">
                            </div>
                        </div>
                    </div>
                    <div class="row lateral-margin">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="username">Inserisci il tuo username</label>
                                <br>
                                <input id="username" type="text" name="username" placeholder="Username" required value="${requestScope.username}">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="email">Inserisci la tua email</label>
                                <br>
                                <input id="email" type="text" name="email" placeholder="Email" required value="${requestScope.email}">
                            </div>
                        </div>
                    </div>
                    <c:if test="${not empty requestScope.errorMessageUsername || not empty requestScope.errorMessageEmail}">
                    <div class="row lateral-margin">
                        <div class="col-md-6">
                            <c:if test="${not empty requestScope.errorMessageUsername}">
                                <div id="error-message-username" class="error-message">
                                    <p><c:out value="${requestScope.errorMessageUsername}" /></p>
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <c:if test="${not empty requestScope.errorMessageEmail}">
                                <div id="error-message-email" class="error-message">
                                    <p><c:out value="${requestScope.errorMessageEmail}" /></p>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    </c:if>
                    <div class="row top-margin lateral-margin">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="password">Inserisci la tua password</label>
                                <br>
                                <input id="password" type="password" placeholder="Password" name="password" required value="${requestScope.password}">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="passwordCheck">Ripeti la tua password</label>
                                <br>
                                <input id="passwordCheck" type="password" placeholder="Ripeti password" name="passwordCheck" required value="${requestScope.passwordCheck}">
                            </div>
                        </div>
                    </div>
                    <c:if test="${not empty requestScope.errorMessagePassword}">
                    <div class="row lateral-margin">
                        <div class="col-md-6">
                        </div>
                        <div class="col-md-6">
                            <div id="error-message-password" class="error-message">
                                <p><c:out value="${requestScope.errorMessagePassword}" /></p>
                            </div>
                        </div>
                    </div>
                    </c:if>
                    <div class="row top-margin lateral-margin2">
                        <div class="col-md-6">
                            <c:url value="/index.jsp" var="loginUrl" />
                            <a href="${loginUrl}" class="btn btn-outline-danger">INDIETRO</a>
                        </div>
                        <div class="col-md-6">
                            <button type="submit" class="btn btn-outline-primary">REGISTRATI</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
