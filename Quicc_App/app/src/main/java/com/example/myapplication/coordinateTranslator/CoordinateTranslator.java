package com.example.myapplication.coordinateTranslator;

import java.util.ArrayList;

/**
 * Constructor centers the object around a room and computes values that can then be used for coordinate translation.
 *  Use coordTranslateToPhone() to get the pixel coordinates where a point should be displayed.
 */
public class CoordinateTranslator {
    static final double RADIUS = 6371; // Radius of earth
    double avgLat; // Average latitude of the room, used for translating degrees
    double tiltAngle = 0; // 0 deg means the room is perfectly aligned with N-E-S-W and horizontal
    double roomWidth = -1, roomHeight = -1;
    double baseX, baseY;
    ArrayList<Coords> cornerCoordList = new ArrayList<>(); // BL, TL, BR, TR
//     Coords testCoords = new Coords(52.23929743942378, 6.856482218019106);
//     Coords testCoords2 = new Coords(52.23927649766113, 6.856535862199231);

    /**
     * Uses the GPS coordinates of 4 corners of a room to calculate the angle of the building and average latitude,
     * for the purpose of translating GPS coordinates using coordTranslate() or coordTranslateToPhone().
     * BL = Bottom Left  TL = Top Left  BR = Bottom Right  TR = Top Right
     */
    public CoordinateTranslator (double BLlat,double BLlng,double TLlat,double TLlng,double BRlat,double BRlng,double TRlat,double TRlng) {
        cornerCoordList.add(new Coords(BLlat, BLlng));
        cornerCoordList.add(new Coords(TLlat, TLlng));
        cornerCoordList.add(new Coords(BRlat, BRlng));
        cornerCoordList.add(new Coords(TRlat, TRlng));

        // Get average latitude
        avgLat = (cornerCoordList.get(0).y + cornerCoordList.get(1).y + cornerCoordList.get(2).y + cornerCoordList.get(3).y)/4;

        // Update coordinates from lat lng to x y
        for(Coords c : cornerCoordList){
            Coords tmp = latlngToXY(c.y,c.x);
            c.x = tmp.x;
            c.y = tmp.y;
        }

        //Set the "bottom left" corner as (0,0), update other points relative to it
        baseX = cornerCoordList.get(0).x;
        baseY = cornerCoordList.get(0).y;

        for(Coords c : cornerCoordList){
            c.x = (c.x - baseX);
            c.y = (c.y - baseY);
        }

        // Calculate the tilt angle of the shape
        double catOp = cornerCoordList.get(2).y, ip = cornerCoordList.get(0).distanceTo(cornerCoordList.get(2));
        double sin = catOp/ip;
        tiltAngle=Math.toDegrees(Math.asin(sin));

        // Rotate the shape so that it is aligned  with the X axis
        for (Coords c : cornerCoordList){
            c.translateByDegree(-tiltAngle);
            if(Math.abs(c.x) < 0.00001){
                c.x = 0;
            }
            if(Math.abs(c.y) < 0.00001){
                c.y = 0;
            }
        }
        double tempAvg = (cornerCoordList.get(1).y + cornerCoordList.get(3).y)/2;
        roomHeight = tempAvg;
        roomWidth = cornerCoordList.get(2).x;
        System.out.println(roomWidth);

        System.out.println("Bases:" + String.valueOf(baseX) + " " + String.valueOf(baseY));
        System.out.println("Angle: " + String.valueOf(tiltAngle));
    }

    Coords latlngToXY(double lat, double lng){
        double x = RADIUS * lng * Math.cos(Math.toRadians(avgLat));
        double y = RADIUS * lat;
        return new Coords(y,x);
    }


    /**
     * Receives GPS coordinates of a point together with the size of the phone map and computes the pixel coordinates
     * of the corresponding point.
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @param mapPxWidth horizontal length of map in pixels
     * @param mapPxHeight vertical length of map in pixels
     * @return double ArrayList of size 2: [X,Y]
     */
    public ArrayList<Double> coordTranslateToPhone(double latitude,double longitude, int mapPxWidth, int mapPxHeight){
        ArrayList<Double> xy = coordTranslate(latitude, longitude);
        double x = xy.get(0), y = xy.get(1);
        double xpercentage = x/roomWidth;
        double ypercentage = y/roomHeight;
        ArrayList<Double> res = new ArrayList<>();
        res.add(xpercentage * mapPxWidth);
        res.add((1-ypercentage) * mapPxHeight);
        return res;
    }

    /**
     * Takes GPS coordinates of a point and computes the corresponding coordinates on a 2D plane
     * (centered around the room given in the constructor)
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @return double ArrayList of size 2: [X,Y]
     */
public ArrayList<Double> coordTranslate(double latitude,double longitude){
    ArrayList<Double> res = new ArrayList<>();
    Coords c = latlngToXY(latitude,longitude);
    c.x -= baseX;
    c.y -= baseY;
    c.translateByDegree(-tiltAngle);

    res.add(c.x);
    res.add(c.y);
    return  res;
}

    public double getRoomWidth() {
        return roomWidth;
    }

    public double getRoomHeight() {
        return roomHeight;
    }
}
