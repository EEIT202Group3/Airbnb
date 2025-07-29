
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
java.time.LocalDate today = java.time.LocalDate.now();
java.time.LocalDate max = today.plusMonths(2);
request.setAttribute("minDate", today.toString());
request.setAttribute("maxDate", max.toString());
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>訂單確認</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
	rel="stylesheet" crossorigin="anonymous">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<style>
.card.room-card {
	border: none;
	border-radius: 18px;
	box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
	background: #fff;
}

.card.room-card:hover {
	transform: translateY(-6px) scale(1.03);
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.16);
}

.room-info-list {
	list-style: none;
	padding: 0;
	margin: 0 0 1rem;
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
	<nav class="navbar navbar-expand-lg bg-body-tertiary">
		<div class="container-fluid">
			<a class="navbar-brand"
				href="http://localhost:8080/Airbnb/index.html"> <i
				class="fa-regular fa-plane-up"></i> Navbar
			</a>
		</div>
	</nav>

	<div class="container" style="margin-bottom: 200px;">
		<div class="row mt-5">
			<div class="col-12">
				<h2 class="mb-4 text-center">訂單確認</h2>
			</div>
		</div>
		<div class="row justify-content-center">
			<div class="col-md-8 col-lg-6">
				<div class="card room-card mx-auto">
					<div class="card-body">
						<div class="room-title mb-3">
							<i class="fa-solid fa-file-invoice"></i> 房源資訊
						</div>
						<ul class="room-info-list">
							<li><i class="fa-solid fa-user"></i> 會員姓名：<span>${customer.username}</span></li>
							<li><i class="fa-solid fa-house"></i> 房名：<span>${listing.house_name}</span></li>
							<li><i class="fa-solid fa-bed"></i> 床型：<span>${listing.bed}</span></li>
							<li><i class="fa-solid fa-location-dot"></i> 地址：<span>${listing.ads}</span></li>
							<li><i class="fa-solid fa-phone"></i> 電話：<span>${listing.tel}</span></li>
							<li><i class="fa-solid fa-dollar-sign"></i> 每晚價格： <span
								class="text-danger fw-bold fs-5">$${listing.price}</span></li>
						</ul>
						<form action="/Airbnb/orderConfirm2" method="post">
							<!-- Hidden inputs -->
							<input type="hidden" name="listId" value="${listing.lisid}">
							
							<input type="hidden" name="price" value="${listing.price}">
							<input type="hidden" name="houseName" value="${listing.house_name}">
							<input type="hidden" name="address" value="${listing.ads}">
							<input type="hidden" name="bed" value="${listing.bed}">
							<input type="hidden" name="tel" value="${listing.tel}">
							<input type="hidden" name="customerId" value="${customer.customerId}">

							<div class="mb-3">
								<label for="checkin" class="form-label"><i
									class="fa-solid fa-calendar-days"></i> 入住日</label> <input type="date"
									class="form-control" id="checkin" name="checkin_date"
									min="${minDate}" max="${maxDate}" required>
							</div>
							<div class="mb-3">
								<label for="checkout" class="form-label"><i
									class="fa-solid fa-calendar-check"></i> 退房日</label> <input type="date"
									class="form-control" id="checkout" name="checkout_date"
									min="${minDate}" max="${maxDate}" required>
							</div>
							<div class="mb-4">
								<label for="people" class="form-label"><i
									class="fa-solid fa-users"></i> 人數</label> <input type="number"
									class="form-control" id="people" name="people" min="1" required>
							</div>
							<div class="room-btns d-flex gap-2">
								<button type="submit" class="btn btn-outline-secondary w-100">
									<i class="fa-solid fa-circle-check"></i> 確認訂單
								</button>
								<a href="/Airbnb/CarRentJsp/homepage.jsp" class="btn btn-outline-secondary w-100"><i
									class="fa-solid fa-car"></i> 我要租車</a>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

	<footer class="border-top container-fluid position-relative top-100">
		<div class="row">
			<div class="col-4">
				<p class="mb-0 text-body-secondary">© 2025 Company, Inc</p>
			</div>
			<div class="col-8">
				<ul class="nav justify-content-end">
					<li class="nav-item"><a href="#"
						class="nav-link px-2 text-body-secondary">關於</a></li>
					<li class="nav-item"><a href="#"
						class="nav-link px-2 text-body-secondary">用戶支援</a></li>
					<li class="nav-item"><a href="#"
						class="nav-link px-2 text-body-secondary">其他服務</a></li>
					<li class="nav-item"><a href="#"
						class="nav-link px-2 text-body-secondary">聯繫我們</a></li>
				</ul>
			</div>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
		crossorigin="anonymous"></script>
</body>
</html>