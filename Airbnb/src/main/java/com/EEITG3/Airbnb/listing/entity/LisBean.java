package com.EEITG3.Airbnb.listing.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "listings")
public class LisBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Integer listId; // 房源ID

    @Column(name = "host_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID host_Id; // 房東會員ID

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0; // 評價數

    @Column(name = "house_name")
    private String house_Name; // 房子名稱

    @Column(name = "ads")
    private String ads; // 地址

    @Column(name = "room")
    private String room; // 房型

    @Column(name = "bed")
    private String bed; // 床位

    @Column(name = "describe")
    private String describe; // 描述

    @Column(name = "tel")
    private String tel; // 電話

    @Column(name = "ppl")
    private int ppl; // 人數

    @Column(name = "price")
    private int price; // 價格

    @Column(name = "photo1")
    private String photo1;
    @Column(name = "photo2")
    private String photo2;
    @Column(name = "photo3")
    private String photo3;
    @Column(name = "photo4")
    private String photo4;
    @Column(name = "photo5")
    private String photo5;
    @Column(name = "photo6")
    private String photo6;
    @Column(name = "photo7")
    private String photo7;
    @Column(name = "photo8")
    private String photo8;
    @Column(name = "photo9")
    private String photo9;
    @Column(name = "photo10")
    private String photo10;

    @Column(name = "approved", nullable = true)  
    private Boolean approved; // true=通過, false=拒絕, null=待審核

    @Column(name = "published", nullable = false)
    private Boolean published = true; // true=上架, false=下架

    // 多對多關聯：房源設備
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "Listings_Equipment",
        joinColumns = @JoinColumn(name = "list_id"),
        inverseJoinColumns = @JoinColumn(name = "equip_id")
    )
    private List<EquipmentBean> equipments = new ArrayList<>();

    public LisBean() {}

    public LisBean(UUID hostId, String houseName, String ads, String room, String bed,
                   String describe, String tel, int ppl, int price) {
        this.host_Id = hostId;
        this.house_Name = houseName;
        this.ads = ads;
        this.room = room;
        this.bed = bed;
        this.describe = describe;
        this.tel = tel;
        this.ppl = ppl;
        this.price = price;
        this.reviewCount = 0;
        this.approved = null; // 新增時預設待審核
        this.published = true; // 預設上架
    }

   

    public Integer getListId() { return listId; }
    public void setListId(Integer listId) { this.listId = listId; }

    public UUID getHostId() { return host_Id; }
    public void setHostId(UUID hostId) { this.host_Id = hostId; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public String getHouseName() { return house_Name; }
    public void setHouseName(String houseName) { this.house_Name = houseName; }

    public String getAds() { return ads; }
    public void setAds(String ads) { this.ads = ads; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getBed() { return bed; }
    public void setBed(String bed) { this.bed = bed; }

    public String getDescribe() { return describe; }
    public void setDescribe(String describe) { this.describe = describe; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public int getPpl() { return ppl; }
    public void setPpl(int ppl) { this.ppl = ppl; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

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

    public List<EquipmentBean> getEquipments() { return equipments; }
    public void setEquipments(List<EquipmentBean> equipments) { this.equipments = equipments; }

    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
}
