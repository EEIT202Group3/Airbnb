<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>查詢訂單</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
        integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .order-table th, .order-table td {
             vertical-align: middle;
  			 white-space: nowrap;          /* 不換行 */
    		 overflow: hidden;             /* 超出寬度 */
   			 text-overflow: ellipsis;      
   			 max-width: 180px;             /* 最大寬度 */
        }
        .order-table .badge {
            font-size: 1em;
        }
        .order-table .btn {
            border-radius: 100px;
            font-weight: 500;
            transition: background 0.2s, color 0.2s;
        }
        .order-table .btn:hover {
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
				<ul class="navbar-nav ms-auto">
					<li class="nav-item">
						<a class="nav-link" href="http://localhost:8080/Airbnb/html/customerInfo.html"
							id="username"></a>
					</li>
					<li class="nav-item">
						<a class="nav-link" href="#">我的訂單</a>
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
                <h2 class="mb-4 text-center"><i class="fa-solid fa-list"></i> 您的訂單內容</h2>
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-striped table-hover align-middle order-table">
                <thead class="table-secondary text-center">
                    <tr>
                        <th><i class="fa-solid fa-receipt"></i> 訂單編號</th>
                        <th><i class="fa-solid fa-user"></i> 會員姓名</th>
                        <th><i class="fa-solid fa-house"></i> 房名</th>
                        <th><i class="fa-solid fa-calendar-days"></i> 入住</th>
                        <th><i class="fa-solid fa-calendar-check"></i> 退房</th>
                        <th><i class="fa-solid fa-users"></i> 人數</th>
                        <th><i class="fa-solid fa-dollar-sign"></i> 金額</th>
                        <th><i class="fa-solid fa-money-check-dollar"></i> 付款狀態</th>
                        <th><i class="fa-solid fa-circle-info"></i> 訂單明細</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr class="text-center">
                            <td>${order.booking_id}</td>
                            <td>${order.username}</td>
                            <td>${order.house_name}</td>
                            <td>${order.checkin_date}</td>
                            <td>${order.checkout_date}</td>
                            <td>${order.people}</td>
                            <td>$${order.total}</td>
                           <td><c:choose>
									<c:when test="${order.ment_status == '未付款'}">
										<span class="badge bg-warning text-dark"><i
											class="fa-solid fa-hourglass-start"></i> 未付款</span>
									</c:when>
									<c:when test="${order.ment_status == '已付款'}">
										<span class="badge bg-success"><i
											class="fa-solid fa-check"></i> 已付款</span>
									</c:when>
									<c:when test="${order.ment_status == '退款中'}">
										<span class="badge bg-danger"><i
											class="fa-solid fa-check"></i>退款中</span>
									</c:when>
										<c:when test="${order.ment_status == '已退款'}">
										<span class="badge bg-success"><i
											class="fa-solid fa-check"></i>已退款</span>
									</c:when>
									<c:otherwise>
										<span class="badge bg-secondary">
											${order.ment_status}</span>
									</c:otherwise>
								</c:choose></td>
                            <td>
                                <a href="/Airbnb/getOrderDetail?booking_id=${order.booking_id}" class="btn btn-outline-secondary">
                                    <i class="fa-solid fa-eye"></i> 查看明細
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="room-btns text-center mt-4">
            <a href="http://localhost:8080/Airbnb/index.html" class="btn btn-outline-secondary"><i class="fa-solid fa-arrow-left"></i> 返回房源</a>
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