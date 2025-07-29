<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>訂單明細</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-LN+7fdVzj6u52u30Kp6M/trliBMCMKTyK833zpbD+pXdCLuTusPj697FH4R/5mcr"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css"
	integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<style>
.card.room-card {
	border: none;
	border-radius: 18px;
	box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.08), 0 1.5px 4px 0
		rgba(0, 0, 0, 0.04);
	transition: transform 0.2s, box-shadow 0.2s;
	background: #fff;
}

.card.room-card:hover {
	transform: translateY(-6px) scale(1.03);
	box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.16), 0 3px 8px 0
		rgba(0, 0, 0, 0.08);
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

	<!-- 主內容區塊 -->
	<div class="container" style="margin-bottom: 200px;">
		<div class="row mt-5">
			<div class="col-12">
				<h2 class="mb-4 text-center">
					<i class="fa-solid fa-file-invoice"></i> 訂單明細
				</h2>
			</div>
		</div>
		<div class="row justify-content-center">
			<div class="col-md-8 col-lg-6">
				<div class="card room-card mx-auto">
					<div class="card-body">
						<ul class="room-info-list">
							<li><i class="fa-solid fa-receipt"></i> 訂單編號：<span>${order.booking_id}</span></li>
							<li><i class="fa-solid fa-user"></i> 會員編號：<span>${order.customer_id}</span></li>
							<li><i class="fa-solid fa-house"></i> 房名：<span>${order.houseName}</span></li>
							<li><i class="fa-solid fa-location-dot"></i> 地址：<span>${order.address}</span>
								<a href="https://www.google.com/maps"
								class="icon-link icon-link-hover ms-2"
								style="--bs-icon-link-transform: translate3d(0, -.125rem, 0);">
									<i class="fa-solid fa-map-location-dot"></i> Google Map
							</a></li>
							<li><i class="fa-solid fa-phone"></i> 電話：<span>${order.tel}</span></li>
							<li><i class="fa-solid fa-bed"></i> 房型：<span>${order.bed}</span></li>
							<li><i class="fa-solid fa-calendar-days"></i> 入住日期：<span>${order.checkin_date}</span></li>
							<li><i class="fa-solid fa-calendar-check"></i> 退房日期：<span>${order.checkout_date}</span></li>
							<li><i class="fa-solid fa-users"></i> 人數：<span>${order.people}</span></li>
							<li><i class="fa-solid fa-calendar-plus"></i> 訂單產生日：<span>${order.created_time}</span></li>
							<li><i class="fa-solid fa-info-circle"></i> 訂單狀態： <c:choose>
									<c:when test="${order.booking_status == '訂單已完成'}">
										<span class="badge bg-success"><i
											class="fa-solid fa-check"></i> 訂單已完成</span>
									</c:when>
									<c:when test="${order.booking_status == '訂單已取消'}">
										<span class="badge bg-danger"><i
											class="fa-solid fa-xmark"></i> 訂單已取消</span>
									</c:when>
									<c:when test="${order.booking_status == '待入住'}">
										<span class="badge bg-danger"><i
											class="fa-solid fa-xmark"></i> 待入住</span>
									</c:when>
								</c:choose></li>

							<li><i class="fa-solid fa-money-check-dollar"></i> 付款編號：<span>${order.payment_id}</span></li>
							<li><i class="fa-solid fa-dollar-sign"></i> 總金額：<span
								class="text-danger fw-bold fs-5">$${order.total}</span></li>
							<li><i class="fa-solid fa-clock"></i> 付款時間：<span>${order.paid_time}</span></li>
							<li><i class="fa-solid fa-credit-card"></i> 付款方式：<span>${order.booking_method}</span></li>
							<li><i class="fa-solid fa-money-bill-wave"></i> 付款狀態： <c:choose>
									<c:when test="${order.ment_status == '已付款'}">
										<span class="badge bg-success"><i
											class="fa-solid fa-check"></i> 已付款</span>
									</c:when>
									<c:when test="${order.ment_status == '未付款'}">
										<span class="badge bg-danger"><i
											class="fa-solid fa-xmark"></i> 未付款</span>
									</c:when>
									<c:when test="${order.ment_status == '退款中'}">
										<span class="badge bg-danger"><i
											class="fa-solid fa-xmark"></i> 退款中</span>
									</c:when>
									<c:when test="${order.ment_status == '已退款'}">
										<span class="badge bg-danger"><i
											class="fa-solid fa-xmark"></i> 已退款</span>
									</c:when>
								</c:choose></li>
						</ul>
						<form action="${pageContext.request.contextPath}/adminOrderDetail"
							method="post">
							<input type="hidden" name="booking_id"
								value="${order.booking_id}"> <select
								name="booking_status" required>
								<option value="">--選擇付款狀態--</option>
								<option value="訂單已取消"
									${order.booking_status == '訂單已取消' ? 'selected' : ''}>訂單已取消</option>
								<option value="訂單已完成"
									${order.booking_status == '訂單已完成' ? 'selected' : ''}>訂單已完成</option>
								<option value="待入住"
									${order.booking_status == '待入住' ? 'selected' : ''}>待入住</option>
							</select>
							<button type="submit">更新狀態</button>
						</form>
						<form action="${pageContext.request.contextPath}/adminOrderDetail"
							method="post">
							<input type="hidden" name="booking_id"
								value="${order.booking_id}"> <select name="ment_status"
								required>
								<option value="">--選擇付款狀態--</option>
								<option value="未付款"
									${order.ment_status == '未付款' ? 'selected' : ''}>未付款</option>
								<option value="已付款"
									${order.ment_status == '已付款' ? 'selected' : ''}>已付款</option>
								<option value="退款中"
									${order.ment_status == '退款中' ? 'selected' : ''}>退款中</option>
								<option value="已退款"
									${order.ment_status == '退款中' ? 'selected' : ''}>已款中</option>
							</select>
							<button type="submit">更新狀態</button>
						</form>
						<div class="room-btns d-flex gap-2">
							<a href="http://localhost:8080/Airbnb/adminOrderList"
								class="btn btn-outline-secondary w-100"><i
								class="fa-solid fa-arrow-left"></i> 返回房源</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
		crossorigin="anonymous"></script>
</body>
</html>