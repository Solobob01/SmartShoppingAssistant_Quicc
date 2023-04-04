package com.example.myapplication.bluetoothBeacon;

import com.example.myapplication.constants.Constants;

import java.util.ArrayList;
import java.util.Scanner;

public class BeaconBlueTooth {
    private int id;
    private String deviceName;
    private String macAdd;
    private double longitude;
    private double latitude;
    private int floor;

    public BeaconBlueTooth(int id, String deviceName, String macAdd, double longitude, double latitude, int floor) {
        this.id = id;
        this.deviceName = deviceName;
        this.macAdd = macAdd;
        this.longitude = longitude;
        this.latitude = latitude;
        this.floor = floor;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAdd() {
        return macAdd;
    }

    public void setMacAdd(String macAdd) {
        this.macAdd = macAdd;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    static public ArrayList<BeaconBlueTooth> readBeaconsFromDataBase(){
        ArrayList<BeaconBlueTooth> output = new ArrayList<>();
        Scanner myReader = new Scanner(Constants.dataBase);
        while(myReader.hasNextLine()){
            String data = myReader.nextLine();
            String[] lineRead = data.split(" ");
            output.add(new BeaconBlueTooth(Integer.parseInt(lineRead[0]), lineRead[1], lineRead[2],
                    Double.parseDouble(lineRead[3]), Double.parseDouble(lineRead[4]), Integer.parseInt(lineRead[5])));

        }
        return output;
    }

    @Override
    public String toString() {
        return "BeaconBT{" +
                "id=" + id +
                ", deviceName='" + deviceName + '\'' +
                ", macAdd='" + macAdd + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", floor=" + floor +
                '}';
    }
}
