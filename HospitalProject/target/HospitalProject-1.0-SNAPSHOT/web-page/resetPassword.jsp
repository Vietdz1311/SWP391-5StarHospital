<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card mt-5">
                    <div class="card-body">
                        <h5 class="card-title text-center">Reset Password</h5>
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
                        <form action="auth?action=resetPassword" method="post">
                            <div class="form-group">
                                <label for="password">New Password</label>
                                <input required type="password" class="form-control" id="password" name="password" placeholder="Enter new password">
                            </div>
                            <div class="form-group">
                                <label for="confirmPassword">Confirm Password</label>
                                <input required type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="Confirm new password">
                            </div>
                            <button type="submit" class="btn btn-primary btn-block">Reset Password</button>
                            <p class="text-center mt-3">Back to <a href="UserController">Login</a></p>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>