package com.EEITG3.Airbnb.listing.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipment")  
public class EquipmentBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equip_id")
    private int equip_id;             //設備ID

    @Column(name = "equip_name")      //設備名稱
    private String equip_name;

    public EquipmentBean() {
    }

    public EquipmentBean(String equip_name) {
        this.equip_name = equip_name;
    }

    public int getEquip_id() {
        return equip_id;
    }

    public void setEquip_id(int equip_id) {
        this.equip_id = equip_id;
    }

    public String getEquip_name() {
        return equip_name;
    }

    public void setEquip_name(String equip_name) {
        this.equip_name = equip_name;
    }

    @Override
    public String toString() {
        return "EquipmentBean [equip_id=" + equip_id + ", equip_name=" + equip_name + "]";
    }

}

