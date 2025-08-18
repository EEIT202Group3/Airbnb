// src/stores/booking.js
import { defineStore } from 'pinia'

export const useBookingStore = defineStore('booking', {
    state: () => ({
        // 房源的整個JSON
        selectedListing: null,

        // 車輛資訊
        vehicleDraft: {
            vehicleId: null,
            pickupDateTime: '',
            returnDateTime: '',
            locationId: null,
            locationName: '',
            locationAddr: '',
            businessHours: '',
            totalAmount: 0,
        },
    }),

    getters: {
        // 是否具備送單最低條件
        canSubmit(state) {
            const v = state.vehicleDraft
            return !!(v.vehicleId && v.pickupDateTime && v.returnDateTime)
        },
    },

    actions: {
        // 第一次存入：房屋資訊
        setListing(listObj) {
            this.selectedListing = listObj || null
        },


        // 第二次存入：車輛ID
        setVehicleId(id) {
            this.vehicleDraft.vehicleId = id ?? null
        },
        // 車輛日期
        setDates({ pickupDateTime, returnDateTime }) {
            if (pickupDateTime !== undefined) this.vehicleDraft.pickupDateTime = pickupDateTime
            if (returnDateTime !== undefined) this.vehicleDraft.returnDateTime = returnDateTime
        },
        // 車輛地點
        setLocation({ locationId = null, locationName = '', locationAddr = '', businessHours = '' }) {
            if (locationId !== undefined) this.vehicleDraft.locationId = locationId
            if (locationName !== undefined) this.vehicleDraft.locationName = locationName
            if (locationAddr !== undefined) this.vehicleDraft.locationAddr = locationAddr
            if (businessHours !== undefined) this.vehicleDraft.businessHours = businessHours
        },
        // 車輛總金額
        setTotalAmount(amount) {
            this.vehicleDraft.totalAmount = Number(amount || 0)
        },


        // 取消時清空pinia
        resetVehicleDraft() {
            this.vehicleDraft = {
                vehicleId: null,
                pickupDateTime: '',
                returnDateTime: '',
                locationId: null,
                locationName: '',
                locationAddr: '',
                businessHours: '',
                totalAmount: 0,
            }
        },
        clearAll() {
            this.selectedListing = null
            this.resetVehicleDraft()
        },
    },
})
