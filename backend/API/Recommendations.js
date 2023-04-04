const express = require('express');
const mongoose = require('mongoose');
const Recommendations = require('../DB/Recommendations');
const route = express.Router();

route.get('/recommendations', async (req, res) => {
    try {
        console.log("GET ALL RECOMMENDATIONS PER ITEM");

        const allRecommendations = await Recommendations.find()
        console.log("all recommendations: ", allRecommendations);

        res.send({
            status: 200,
            recommendations: allRecommendations,
        })
    } catch (err) {
        console.log('error');
        res.send({
            status: 400,
            message: err
        })
    }
});


module.exports = route;