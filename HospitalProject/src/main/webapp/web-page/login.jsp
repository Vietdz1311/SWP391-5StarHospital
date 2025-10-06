<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card mt-5">
                    <div class="card-body">
                        <h5 class="card-title text-center">User Login</h5>
                        <c:if test="${param.error != null}">
                            <div class="alert alert-danger" role="alert">
                                ${param.error}
                            </div>
                        </c:if>
                        <c:if test="${param.success != null}">
                            <div class="alert alert-success" role="alert">
                                ${param.success}
                            </div>
                        </c:if>
                        <form action="auth?action=login" method="post">
                            <div class="form-group">
                                <label for="inputEmail">Email</label>
                                <input required type="email" class="form-control" id="inputEmail" name="email" placeholder="Enter email">
                            </div>
                            <div class="form-group">
                                <label for="inputPassword">Password</label>
                                <input required type="password" class="form-control" id="inputPassword" name="password" placeholder="Password">
                            </div>
                            <a href="auth?action=forgetPassword" class="text-right mb-3" style="display: block; color: #333">Forget password</a>
                            <button type="submit" class="btn btn-primary btn-block">Login</button>
                            <p class="text-center mt-3">Don't have an account? <a href="auth?action=register">Register</a></p>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>