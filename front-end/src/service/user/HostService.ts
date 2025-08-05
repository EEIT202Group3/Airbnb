const BASE_URL = 'http://localhost:8080';
export async function getAllHosts() {
    const response = await fetch(`${BASE_URL}/api/admins/hosts`, {
        method: 'GET',
        credentials: 'include',
    });
    if (response.status === 401 || response.status === 403) {
        alert('請先登入');
        return null;
    }
    return await response.json();
}