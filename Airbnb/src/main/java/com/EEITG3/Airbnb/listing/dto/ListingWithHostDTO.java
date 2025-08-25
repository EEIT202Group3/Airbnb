package com.EEITG3.Airbnb.listing.dto;

import java.util.List;

public class ListingWithHostDTO {

    // 房源資訊
    private Integer listId;
    private String houseName;
    private String ads;
    private String room;
    private String bed;
    private Integer ppl;
    private Integer price;
    private String describe;
    private String tel;
    private Double reviewCount;

    // 房源圖片
    private String photo1;
    private String photo2;
    private String photo3;
    private String photo4;
    private String photo5;
    private String photo6;
    private String photo7;
    private String photo8;
    private String photo9;
    private String photo10;

    // 房東資訊
    private String hostName;
    private String hostAvatarURL;

    // 設備清單
    private List<EquipmentDTO> equipments;

    // getters & setters
    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }

    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }

    public String getAds() { return ads; }
    public void setAds(String ads) { this.ads = ads; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getBed() { return bed; }
    public void setBed(String bed) { this.bed = bed; }

    public Integer getPpl() { return ppl; }
    public void setPpl(Integer ppl) { this.ppl = ppl; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getDescribe() { return describe; }
    public void setDescribe(String describe) { this.describe = describe; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public Double getReviewCount() { return reviewCount; }
    public void setReviewCount(Double reviewCount) { this.reviewCount = reviewCount; }

    public String getPhoto1() { return photo1; }
    public void setPhoto1(String photo1) { this.photo1 = photo1; }

    public String getPhoto2() { return photo2; }
    public void setPhoto2(String photo2) { this.photo2 = photo2; }

    public String getPhoto3() { return photo3; }
    public void setPhoto3(String photo3) { this.photo3 = photo3; }

    public String getPhoto4() { return photo4; }
    public void setPhoto4(String photo4) { this.photo4 = photo4; }

    public String getPhoto5() { return photo5; }
    public void setPhoto5(String photo5) { this.photo5 = photo5; }

    public String getPhoto6() { return photo6; }
    public void setPhoto6(String photo6) { this.photo6 = photo6; }

    public String getPhoto7() { return photo7; }
    public void setPhoto7(String photo7) { this.photo7 = photo7; }

    public String getPhoto8() { return photo8; }
    public void setPhoto8(String photo8) { this.photo8 = photo8; }

    public String getPhoto9() { return photo9; }
    public void setPhoto9(String photo9) { this.photo9 = photo9; }

    public String getPhoto10() { return photo10; }
    public void setPhoto10(String photo10) { this.photo10 = photo10; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getHostAvatarURL() { return hostAvatarURL; }
    public void setHostAvatarURL(String hostAvatarURL) { this.hostAvatarURL = hostAvatarURL; }

    public List<EquipmentDTO> getEquipments() { return equipments; }
    public void setEquipments(List<EquipmentDTO> equipments) { this.equipments = equipments; }

    // 內部設備 DTO
    public static class EquipmentDTO {
        private Integer equip_id;
        private String equip_name;
        private String equip_icon;
        private String equip_category;

        public Integer getEquip_id() { return equip_id; }
        public void setEquip_id(Integer equip_id) { this.equip_id = equip_id; }

        public String getEquip_name() { return equip_name; }
        public void setEquip_name(String equip_name) { this.equip_name = equip_name; }

        public String getEquip_icon() { return equip_icon; }
        public void setEquip_icon(String equip_icon) { this.equip_icon = equip_icon; }

        public String getEquip_category() { return equip_category; }
        public void setEquip_category(String equip_category) { this.equip_category = equip_category; }
    }
}
