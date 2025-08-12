import axios from "axios";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useCustomerStore = defineStore(
    'customer',function(){
        const customer = ref(null);
        
        async function fetchUser(){
            try {
                const response = await axios.get(`http://localhost:8080/api/customers/current`,{
                    withCredentials:true,
                })
                if(response.status===200){
                    customer.value = response.data
                }else{
                    customer.value = null
                }
            } catch (error) {
                customer.value = null;
                console.log(error)
            }
        }
        return {
            customer,fetchUser
        }
    }
)