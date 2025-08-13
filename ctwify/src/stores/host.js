import { defineStore } from "pinia";
import { ref } from "vue";
import { loginService, logoutService, findMe } from "@/service/host/hostService";

export const useHostStore = defineStore(
    'host', function () {
        const host = ref(null);

        async function fetchUser() {
            host.value = await findMe();
        }

        async function login(loginData) {
            await loginService(loginData);
            await fetchUser();
        }

        async function logout() {
            await logoutService();
            host.value = null;
        }

        return {
            host, login, logout, fetchUser
        }
    }, {
    persist: {
        key: 'host',
        paths: ['host'],
        storage: sessionStorage,
    }
}
)