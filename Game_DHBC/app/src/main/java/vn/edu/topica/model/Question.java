package vn.edu.topica.model;

import android.graphics.Bitmap;

public class Question {
    private int id;
    private String link;
    private Bitmap bitmap;
    private String dapAnTam;
    private String dapAn;

    public Question() {
    }

    public Question(int id, String link, Bitmap bitmap, String dapAnTam, String dapAn) {
        this.id = id;
        this.link = link;
        this.bitmap = bitmap;
        this.dapAnTam = dapAnTam;
        this.dapAn = dapAn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDapAnTam() {
        return dapAnTam;
    }

    public void setDapAnTam(String dapAnTam) {
        this.dapAnTam = dapAnTam;
    }

    public String getDapAn() {
        return dapAn;
    }

    public void setDapAn(String dapAn) {
        this.dapAn = dapAn;
    }
}
