package com.pointhub.db;

/**
 * Created by Venu on 03-05-2016.
 */
public class Points {

    String storeName;
    String points;
    String lastVisited;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    public Points() {


    }

    public Points(String storeName, String points, String lastVisited) {

        this.storeName = storeName;
        this.points = points;

        this.lastVisited = lastVisited;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(String lastVisited) {
        this.lastVisited = lastVisited;
    }
}
