const BASE_URL = 'http://localhost:8080';
export async function getCurrentAdmin() {
    const response = await fetch(`${BASE_URL}/api/admins/current`, {
        method: 'GET',
        credentials: 'include',
    })
    return await response.json();
}