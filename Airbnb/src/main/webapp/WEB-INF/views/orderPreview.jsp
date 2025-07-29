<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>訂單預覽</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
        integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .card.room-card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 4px 24px 0 rgba(0,0,0,0.08), 0 1.5px 4px 0 rgba(0,0,0,0.04);
            transition: transform 0.2s, box-shadow 0.2s;
            background: #fff;
        }
        .card.room-card:hover {
            transform: translateY(-6px) scale(1.03);
            box-shadow: 0 8px 32px 0 rgba(0,0,0,0.16), 0 3px 8px 0 rgba(0,0,0,0.08);
        }
        .room-info-list {
            list-style: none;
            padding: 0;
            margin: 0 0 1rem 0;
        }
        .room-info-list li {
            margin-bottom: 0.5rem;
            font-size: 1.08rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .room-info-list i {
            color: #6c757d;
            min-width: 22px;
        }
        .room-title {
            font-size: 1.4rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: #333;
        }
        .room-btns .btn {
            border-radius: 50px;
            font-weight: 500;
        }
        .room-btns .btn:hover {
            background: #495057;
            color: #fff;
        }
        body {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>
    <!-- 這邊是 Navbar -->
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
            <a class="navbar-brand" href="http://localhost:8080/Airbnb/index.html">
                <i class="fa-regular fa-plane-up"></i>
                Navbar
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item"> <a class="nav-link active" aria-current="page"
                            href="http://localhost:8080/Airbnb/index.html">首頁</a></li>
                    <li class="nav-item"> <a class="nav-link" href="#">敬請期待</a></li>
                    <li class="nav-item"> <a class="nav-link" href="#">敬請期待</a></li>
                    <li class="nav-item"> <a class="nav-link" href="#">敬請期待</a></li>
                </ul>
                <ul class="navbar-nav ms-auto" id="afterLogin">
                    <li class="nav-item">
                        <a class="nav-link" href="http://localhost:8080/Airbnb/html/customerInfo.html">NickChen</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="http://localhost:8080/Airbnb/getOrderList">我的訂單</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" id="logout">登出</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <!-- 主內容區塊 -->
    <div class="container" style="margin-bottom: 200px;">
        <div class="row mt-5">
            <div class="col-12">
                <h2 class="mb-4 text-center">請確認您的訂單內容</h2>
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card room-card mx-auto">
                    <div class="card-body">
                        <div class="room-title mb-3"><i class="fa-solid fa-file-invoice"></i> 訂單明細</div>
                        <ul class="room-info-list">
                            <li><i class="fa-solid fa-user"></i> 會員姓名：<span>${customer.username}</span></li>
                            <li><i class="fa-solid fa-house"></i> 房名：<span>${listing.house_name}</span></li>
                            <li><i class="fa-solid fa-location-dot"></i> 地址：<span>${listing.ads}</span></li>
                            <li><i class="fa-solid fa-phone"></i> 電話：<span>${listing.tel}</span></li>
                            <li><i class="fa-solid fa-bed"></i> 床型：<span>${listing.bed}</span></li>
                            <li><i class="fa-solid fa-calendar-days"></i> 入住日期：<span>${checkin_date}</span></li>
                            <li><i class="fa-solid fa-calendar-check"></i> 退房日期：<span>${checkout_date}</span></li>
                            <li><i class="fa-solid fa-users"></i> 入住人數：<span>${people}</span></li>
                            <li><i class="fa-solid fa-clock"></i> 入住天數：<span>${days}</span></li>
                            	<li><i class="fa-solid fa-dollar-sign"></i> 每晚價格： <span
								class="text-danger fw-bold fs-5">$${listing.price}</span></li>
                            <li><i class="fa-solid fa-money-bill-wave"></i> 總金額：<span class="text-danger fw-bold fs-5">$${total}</span></li>
                        </ul>
                       <div class="room-btns d-flex gap-2">
  					  <form action="finallOrderConfirm" method="post" class="flex-fill">
        					<input type="hidden" name="customer_id" value="${customer.customerId}">
        					<input type="hidden" name="username" value="${customer.username}">
   						    <input type="hidden" name="house_name" value="${listing.house_name}">
     					  	<input type="hidden" name="address" value="${listing.ads}">
     	   					<input type="hidden" name="tel" value="${listing.tel}">
        					<input type="hidden" name="bed" value="${listing.bed}">
        					<input type="hidden" name="checkin_date" value="${checkin_date}">
        					<input type="hidden" name="checkout_date" value="${checkout_date}">
        					<input type="hidden" name="people" value="${people}">
        					<input type="hidden" name="days" value="${days}">
        					<input type="hidden" name="price" value="${price}">
        					<input type="hidden" name="total" value="${total}">
        					<input type="hidden" name="lisid" value="${listing.lisid}">
        				<button type="submit" class="btn btn-outline-secondary w-100">
            <i class="fa-solid fa-credit-card"></i> 立即付款（送出訂單）
        </button>
    </form>
</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- 這邊是 footer -->
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
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
        crossorigin="anonymous"></script>
</body>
</html>