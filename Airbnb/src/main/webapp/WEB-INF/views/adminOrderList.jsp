<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>查詢所有訂單</title>
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
.order-table th, .order-table td {
	vertical-align: middle;
}

.order-table .badge {
	font-size: 1em;
}

.order-table .btn {
	border-radius: 50px;
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

	<!-- 主內容區塊 -->
	<div class="container" style="margin-bottom: 200px;">
		<div class="row mt-5">
			<div class="col-12">
				<h2 class="mb-4 text-center">
					<i class="fa-solid fa-list"></i> 會員訂單內容
				</h2>
			</div>
		</div>
		<div class="table-responsive">
			<table
				class="table table-bordered table-striped table-hover align-middle order-table">
				<thead class="table-secondary text-center">
					<tr>
						<th><i class="fa-solid fa-receipt"></i> 訂單編號</th>
						<th><i class="fa-solid fa-user"></i> 會員編號</th>
						<th><i class="fa-solid fa-house"></i> 房名</th>
						<th><i class="fa-solid fa-calendar-days"></i> 入住</th>
						<th><i class="fa-solid fa-calendar-check"></i> 退房</th>
						<th><i class="fa-solid fa-users"></i> 人數</th>
						<th><i class="fa-solid fa-dollar-sign"></i> 金額</th>
						<th><i class="fa-solid fa-money-check-dollar"></i> 付款狀態</th>
						<th><i class="fa-solid fa-money-check-dollar"></i> 訂單狀態</th>
						<th><i class="fa-solid fa-circle-info"></i> 訂單明細</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="order" items="${orders}">
						<tr class="text-center">
							<td>${order.booking_id}</td>
							<td>${order.customer_id}</td>
							<td>${order.houseName}</td>
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
							<td><c:choose>
									<c:when test="${order.booking_status == '待入住'}">
										<span class="badge bg-warning text-dark"><i
											class="fa-solid fa-hourglass-start"></i> 待入住</span>
									</c:when>
									<c:when test="${order.booking_status == '訂單已完成'}">
										<span class="badge bg-success"><i
											class="fa-solid fa-check"></i> 訂單已完成</span>
									</c:when>
									<c:when test="${order.booking_status == '訂單已取消'}">
										<span class="badge bg-secondary"> 訂單已取消</span>
									</c:when>
									<c:when test="${order.booking_status == '待入住'}">
										<span class="badge bg-secondary">待入住</span>
									</c:when>
								</c:choose></td>

							<td><a
								href="/Airbnb/adminOrderDetail?booking_id=${order.booking_id}"
								class="btn btn-outline-secondary"> <i
									class="fa-solid fa-eye"></i> 查看明細
							</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-ndDqU0Gzau9qJ1lfW4pNLlhNTkCfHzAVBReH9diLvGRem5+R9g2FzA8ZGN954O5Q"
		crossorigin="anonymous"></script>
</body>
</html>