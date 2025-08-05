import { defineStore } from "pinia";
import { ref } from "vue";

export const useAdminStore = defineStore(
    'admin',function(){
        const admin = ref(null);

        return {
            admin
        }
    }
)