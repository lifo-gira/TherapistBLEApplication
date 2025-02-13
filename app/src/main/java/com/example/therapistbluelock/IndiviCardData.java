package com.example.therapistbluelock;

public class IndiviCardData {
    String pain,minang,maxang,flexvel,extenvel,cyclecount;

    public String getPain() {
        return pain;
    }

    public void setPain(String pain) {
        this.pain = pain;
    }

    public String getMinang() {
        return minang;
    }

    public void setMinang(String minang) {
        this.minang = minang;
    }

    public String getMaxang() {
        return maxang;
    }

    public void setMaxang(String maxang) {
        this.maxang = maxang;
    }

    public String getFlexvel() {
        return flexvel;
    }

    public void setFlexvel(String flexvel) {
        this.flexvel = flexvel;
    }

    public String getExtenvel() {
        return extenvel;
    }

    public void setExtenvel(String extenvel) {
        this.extenvel = extenvel;
    }

    public String getCyclecount() {
        return cyclecount;
    }

    public void setCyclecount(String cyclecount) {
        this.cyclecount = cyclecount;
    }

    public IndiviCardData(String pain, String minang, String maxang, String flexvel, String extenvel, String cyclecount) {
        this.pain = pain;
        this.minang = minang;
        this.maxang = maxang;
        this.flexvel = flexvel;
        this.extenvel = extenvel;
        this.cyclecount=cyclecount;
    }
}
