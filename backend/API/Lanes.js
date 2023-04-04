const express = require('express');
const mongoose = require('mongoose');
const Lanes = require('../DB/Lanes');
const route = express.Router();





route.get('/lanes', async (req, res) => {
    try {
        console.log("GET ALL LANES");

        const allLanes = await Lanes.find()
        console.log("all lanes: ", allLanes);

        res.send({
            status: 200,
            lanes: allLanes,
        })
    } catch (err) {
        console.log('error trying to add thougth');
        res.send({
            status: 400,
            message: err
        })
    }
})

route.post('/lane', async (req, res) => {
    try {
        const {cameraID, count} = req.body;

        console.log(cameraID);
        console.log(count);

        const result = await Lanes.updateOne(
            { laneNumber: cameraID },
            { $set: { numberOfPeople: count } },
            { upsert: true } // Make this update into an upsert
          );          

        res.send({
            status: 200,
        })
    } catch (err) {
        console.log('error trying to update / insert a lane');
        res.send({
            status: 400,
            message: err
        })
    }
})

route.get('/fastestLane', async (req, res) => {
    try {
        console.log("GET Fastest lane");

        const allLanes = await Lanes.find();
        console.log("all lanes: ", allLanes);

        var lowest = Number.POSITIVE_INFINITY;
        
        var tmp;
        let fastestLane = allLanes[0];

        for (var i = allLanes.length-1; i>=0; i--) {
            tmp = allLanes[i].numberOfPeople;
            if (tmp < lowest) {
                lowest = tmp;
                fastestLane = allLanes[i];
            }
        }

        var min = Math.min(...allLanes.map(item => item.numberOfPeople));
        console.log("min: " + min);

        res.send({
            status: 200,
            fastestLane: fastestLane.laneNumber
        })
    } catch (err) {
        console.log('error trying to add thougth');
        res.send({
            status: 400,
            message: err
        })
    }
})



module.exports = route;