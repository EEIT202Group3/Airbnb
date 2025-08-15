import { defineStore } from "pinia";
import { ref } from "vue";
import { loginService, logoutService, findMe } from "@/service/user/customerService";

export const useCustomerStore = defineStore(
    'customer', function () {
        const customer = ref(null);

        async function fetchUser() {
            customer.value = await findMe();
        }

        async function login(loginData) {
            await loginService(loginData);
            await fetchUser();
        }

        async function logout() {
            const response = await logoutService();
            customer.value = null;
        }

        return {
            customer, fetchUser, login, logout
        }
    }, {
    persist: {
        key: 'customer',
        paths: ['customer'],
        storage: sessionStorage,
    }
}
)