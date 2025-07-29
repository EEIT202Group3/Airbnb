<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
<title>訂單預覽</title>
</head>
<body>
<!-- 這邊是 Navbar -->
	<nav class="navbar navbar-expand-lg bg-body-tertiary">
		<div class="container-fluid">
			<a class="navbar-brand" href="http://localhost:8080/Project2/index.html">
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
							href="http://localhost:8080/Project2/index.html">首頁</a></li>
					<li class="nav-item"> <a class="nav-link" href="#">敬請期待</a></li>
					<li class="nav-item"> <a class="nav-link" href="#">敬請期待</a></li>
					<li class="nav-item"> <a class="nav-link" href="#">敬請期待</a></li>
				</ul>
				
				<ul class="navbar-nav ms-auto" id="afterLogin" >
					<li class="nav-item">
						<a class="nav-link" href="http://localhost:8080/Project2/html/customerInfo.html"
							>NickChen</a>
					</li>
					<li class="nav-item">
						<a class="nav-link" href="http://localhost:8080/Project2/JSP/beforOrderConfirm">我的訂單</a>
					</li>
					<li class="nav-item">
						<a class="nav-link" href="#" id="logout">登出</a>
					</li>
				</ul>
			</div>
		</div>
	</nav>
	<h2>請確認您的訂單內容</h2>
	<table border="1">
		<tr><th>訂購人</th><td>${customer_id}</td></tr>
		<tr><th>房名</th>	<td>${houseName}</td></tr>
		<tr><th>地址</th>	<td>${address}</td></tr>
		<tr><th>地址</th>	<td>${tel}</td></tr>
		<tr><th>房型</th>	<td>${bed}</td></tr>
		<tr><th>入住日期</th><td>${checkin_date}</td></tr>
		<tr><th>退房日期</th><td>${checkout_date}</td>	</tr>
		<tr><th>入住人數</th><td>${people}</td></tr>
		<tr><th>入住天數</th><td>${days}</td></tr>
		<tr><th>單價</th>	<td>$${price}</td></tr>
		<tr><th>總金額</th><td><strong>$${total}</strong></td>
		</tr>
	</table>

	<form action="orderPreview" method="post">
		<input type="hidden" name="customer_id" value="${customer_id}">
		<input type="hidden" name="houseName" value="${houseName}">
		<input type="hidden" name="address" value="${address}">
		<input type="hidden" name="tel" value="${tel}">
		<input type="hidden" name="bed" value="${bed}">
		<input type="hidden" name="checkin_date" value="${checkin_date}">
		<input type="hidden" name="checkout_date" value="${checkout_date}">
		<input type="hidden" name="people" value="${people}">
		<input type="hidden" name="price" value="${price}">
		<input type="hidden" name="total" value="${total}">
		<button type="submit">立即付款（送出訂單）</button>
	</form>
<!-- 這邊是 footer -->
	<div class="container-fluid position-relative top-100">
		<footer class="border-top">
			<div class="row">
				<div class="col-4">
					<p class=" mb-0 text-body-secondary">© 2025 Company, Inc</p>
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
</body>
</html>