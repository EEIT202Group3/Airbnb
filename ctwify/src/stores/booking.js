// src/stores/booking.js
import { defineStore } from 'pinia'

export const useBookingStore = defineStore('booking', {
    state: () => ({
        // 房源的整個JSON
        selectedListing: null,

        // 訂房參數
        bookingParams: {
            listid: null,
            checkInDate: '',
            checkOutDate: '',
            guests: 1
        },

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
            reservationId: null, // 租車訂單ID
        },
    }),

    getters: {
        // 是否具備送單最低條件
        canSubmit(state) {
            const v = state.vehicleDraft
            return !!(v.vehicleId && v.pickupDateTime && v.returnDateTime)
        },

        // 是否有完整的訂房資料
        hasBookingData(state) {
            const p = state.bookingParams
            return !!(p.listid && p.checkInDate && p.checkOutDate && p.guests)
        },

        // 是否有租車資料
        hasCarData(state) {
            return !!(state.vehicleDraft.reservationId || state.vehicleDraft.vehicleId)
        }
    },

    actions: {
        // 第一次存入：房屋資訊
        setListing(listObj) {
            this.selectedListing = listObj || null
        },

        // 存入訂房參數
        setBookingParams({ listid, checkInDate, checkOutDate, guests }) {
            if (listid !== undefined) this.bookingParams.listid = listid
            if (checkInDate !== undefined) this.bookingParams.checkInDate = checkInDate
            if (checkOutDate !== undefined) this.bookingParams.checkOutDate = checkOutDate
            if (guests !== undefined) this.bookingParams.guests = guests

            // 同步日期到租車
            if (checkInDate !== undefined) this.vehicleDraft.pickupDateTime = checkInDate
            if (checkOutDate !== undefined) this.vehicleDraft.returnDateTime = checkOutDate
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

        // 設定租車訂單ID
        setReservationId(id) {
            this.vehicleDraft.reservationId = id ?? null
        },

        // 取消時清空pinia
        resetVehicleDraft() {
            this.vehicleDraft = {
                vehicleId: null,
                pickupDateTime: this.bookingParams.checkInDate || '',
                returnDateTime: this.bookingParams.checkOutDate || '',
                locationId: null,
                locationName: '',
                locationAddr: '',
                businessHours: '',
                totalAmount: 0,
                reservationId: null,
            }
        },

        clearAll() {
            this.selectedListing = null
            this.bookingParams = {
                listid: null,
                checkInDate: '',
                checkOutDate: '',
                guests: 1
            }
            this.resetVehicleDraft()
        },
    },
})