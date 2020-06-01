package com.glau.avasyu;

public class NGOModel {
    private String name, location, img_link, description, official_site, contact, est_date;



    public NGOModel(String name, String location, String img_link, String description, String official_site, String contact, String est_date) {
        this.name = name;
        this.location = location;
        this.img_link = img_link;
        this.description = description;
        this.official_site = official_site;
        this.contact = contact;
        this.est_date = est_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImg_link() {
        return img_link;
    }

    public void setImg_link(String img_link) {
        this.img_link = img_link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfficial_site() {
        return official_site;
    }

    public void setOfficial_site(String official_site) {
        this.official_site = official_site;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEst_date() {
        return est_date;
    }

    public void setEst_date(String est_date) {
        this.est_date = est_date;
    }

    public NGOModel() {
    }



}