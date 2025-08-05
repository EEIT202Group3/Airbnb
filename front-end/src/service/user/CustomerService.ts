const BASE_URL = 'http://localhost:8080';
export async function getAllCustomers() {
    const response = await fetch(`${BASE_URL}/api/admins/customers`, {
        method: 'GET',
        credentials: 'include',
    });
    if (response.status === 401 || response.status === 403) {
        alert('請先登入');
        return null;
    }
    return await response.json();
}

export async function permission(status, customerEmail) {
    const data = {
        'status': status,
        'email': customerEmail
    }
    const response = await fetch(`${BASE_URL}/api/admins/customers/updatePermission`, {
        method: 'PATCH',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (response.status === 400) {
        alert('請求錯誤');
        return null;
    }
    return await response.json();
}