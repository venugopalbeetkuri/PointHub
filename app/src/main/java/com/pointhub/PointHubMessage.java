package com.pointhub;

/**
 * Created by Provigil on 05-06-2016.
 */
public class PointHubMessage {

    String type;
    String billAmount;
    String userName;
    String points;


    PointHubMessage(String type, String billAmount, String userName, String points){
        this.type = type;
        this.billAmount = billAmount;
        this.userName = userName;
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
