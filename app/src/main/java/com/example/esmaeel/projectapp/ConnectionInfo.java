package com.example.esmaeel.projectapp;

/**
 * Created by esmaeel on 1/28/2017.
 */

public class ConnectionInfo {

    private int id;
    private String port;
    private String ip;

    public ConnectionInfo(){}

    public ConnectionInfo(String port, String ip) {
        super();
        this.port = port;
        this.ip = ip;
    }

    //getters & setters


    public int getId() {
        return id;
    }

    public String getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public String toString() {
        return  id + " - "+"\n PORT: " + port + "\n IP ADDRESS: " + ip;
    }

}