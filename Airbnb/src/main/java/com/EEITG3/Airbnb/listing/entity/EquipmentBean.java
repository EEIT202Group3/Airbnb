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
    private int equip_id; // 設備ID

    @Column(name = "equip_name", nullable = false)
    private String equip_name; // 設備名稱

    @Column(name = "equip_icon")
    private String equip_icon; // 設備圖示 

    @Column(name = "equip_category")
    private String equip_category; // 設備分類 

    public EquipmentBean() {
    }

    public EquipmentBean(String equip_name, String equip_icon, String equip_category) {
        this.equip_name = equip_name;
        this.equip_icon = equip_icon;
        this.equip_category = equip_category;
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

    public String getEquip_icon() {
        return equip_icon;
    }

    public void setEquip_icon(String equip_icon) {
        this.equip_icon = equip_icon;
    }

    public String getEquip_category() {
        return equip_category;
    }

    public void setEquip_category(String equip_category) {
        this.equip_category = equip_category;
    }

    @Override
    public String toString() {
        return "EquipmentBean [equip_id=" + equip_id +
                ", equip_name=" + equip_name +
                ", equip_icon=" + equip_icon +
                ", equip_category=" + equip_category + "]";
    }
}
