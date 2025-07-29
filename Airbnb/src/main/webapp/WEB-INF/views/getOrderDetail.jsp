<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>訂單明細</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .card.room-card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08), 0 1.5px 4px rgba(0,0,0,0.04);
            background: #fff;
        }
        body {
            background-color: #f8f9fa;
        }
        .order-header {
            font-size: 1.3rem;
            font-weight: 600;
            color: #333;
        }
        .order-badge {
            font-size: 1em;
        }
    </style>
</head>
<body>
<!-- Navbar -->
<nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
        <a class="navbar-brand" href="http://localhost:8080/Airbnb/index.html">
            <i class="fa-regular fa-plane-up"></i> Navbar
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item"><a class="nav-link active" href="http://localhost:8080/Airbnb/index.html">首頁</a></li>
                <li class="nav-item"><a class="nav-link" href="#">敬請期待</a></li>
                <li class="nav-item"><a class="nav-link" href="#">敬請期待</a></li>
            </ul>
            <ul class="navbar-nav ms-auto" id="afterLogin">
                <li class="nav-item"><a class="nav-link" href="#">NickChen</a></li>
                <li class="nav-item"><a class="nav-link" href="http://localhost:8080/Airbnb/getOrderList">我的訂單</a></li>
                <li class="nav-item"><a class="nav-link" href="#" id="logout">登出</a></li>
            </ul>
        </div>
    </div>
</nav>

<!-- 主內容 -->
<div class="container" style="margin-bottom: 200px;">
    <div class="row mt-5">
        <div class="col-12 text-center">
            <h2 class="mb-3"><i class="fa-solid fa-file-invoice"></i> 訂單明細</h2>
            <div class="order-header text-primary">
                <i class="fa-solid fa-receipt"></i> 訂單編號：${order.booking_id}
            </div>
        </div>
    </div>
    <div class="row justify-content-center mt-4">
        <div class="col-md-8 col-lg-6">
            <div class="card room-card">
                <div class="card-body">
                    <table class="table table-bordered align-middle mb-0">
                        <tbody>
                        <tr>
                            <th style="width:35%;"><i class="fa-solid fa-user"></i> 會員姓名</th>
                            <td>${order.username}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-house"></i> 房名</th>
                            <td>${order.house_name}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-location-dot"></i> 地址</th>
                            <td>
                                ${order.address}
                                <a href="https://www.google.com/maps" class="ms-2">
                                    <i class="fa-solid fa-map-location-dot"></i> Google Map
                                </a>
                            </td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-phone"></i> 電話</th>
                            <td>${order.tel}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-bed"></i> 房型</th>
                            <td>${order.bed}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-calendar-days"></i> 入住日期</th>
                            <td>${order.checkin_date}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-calendar-check"></i> 退房日期</th>
                            <td>${order.checkout_date}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-users"></i> 人數</th>
                            <td>${order.people}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-calendar-plus"></i> 訂單產生日</th>
                            <td>${order.created_time}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-info-circle"></i> 訂單狀態</th>
                            <td>${order.booking_status}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-money-check-dollar"></i> 付款編號</th>
                            <td>${order.payment_id}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-dollar-sign"></i> 總金額</th>
                            <td class="text-danger fw-bold fs-5">$${order.total}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-clock"></i> 付款時間</th>
                            <td>${order.paid_time}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-credit-card"></i> 付款方式</th>
                            <td>${order.booking_method}</td>
                        </tr>
                        <tr>
                            <th><i class="fa-solid fa-money-bill-wave"></i> 付款狀態</th>
                            <td>
                                <c:choose>
                                    <c:when test="${order.ment_status == '未付款'}">
                                        <span class="badge bg-warning text-dark order-badge"><i class="fa-solid fa-hourglass-start"></i> 未付款</span>
                                    </c:when>
                                    <c:when test="${order.ment_status == '已付款'}">
                                        <span class="badge bg-success order-badge"><i class="fa-solid fa-check"></i> 已付款</span>
                                    </c:when>
                                    <c:when test="${order.ment_status == '退款中'}">
                                        <span class="badge bg-danger order-badge"><i class="fa-solid fa-rotate"></i> 退款中</span>
                                    </c:when>
                                    <c:when test="${order.ment_status == '已退款'}">
                                        <span class="badge bg-success order-badge"><i class="fa-solid fa-check"></i> 已退款</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary order-badge">${order.ment_status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="text-center mt-3">
                        <a href="http://localhost:8080/Airbnb/index.html" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-arrow-left"></i> 返回房源
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<div class="container-fluid position-relative top-100">
    <footer class="border-top">
        <div class="row">
            <div class="col-4">
                <p class="mb-0 text-body-secondary">© 2025 Company, Inc</p>
            </div>
            <div class="col-8">
                <ul class="nav justify-content-end">
                    <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">關於</a></li>
                    <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">用戶支援</a></li>
                    <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">其他服務</a></li>
                    <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">聯繫我們</a></li>
                </ul>
            </div>
        </div>
    </footer>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
</body>
</html>