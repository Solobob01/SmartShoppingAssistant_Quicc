const express = require('express');
const mongoose = require('mongoose');
const Purchases = require('../DB/Purchases');
const route = express.Router();

route.get('/purchases', async (req, res) => {
    try {
        console.log("GET ALL PURCHASES");

        const allPurchases = await Purchases.find()
        console.log("all purchases: ", allPurchases);

        res.send({
            status: 200,
            purchases: allPurchases,
        })
    } catch (err) {
        console.log('error');
        res.send({
            status: 400,
            message: err
        })
    }
})

route.post('/purchase', async (req, res) => {
    try {
        let {purchasedItems} = req.body;

        console.log("THE USER COMMPLETED A NEW GROCERIES SHOPPING, OF: ", purchasedItems);
        
        let newPurchase = {}
        newPurchase.itemsPurchased = [];

        for (var i=0; i<purchasedItems.length; i++) {
            newPurchase.itemsPurchased.push(purchasedItems[i]);
        }

        let purchaseModel = new Purchases(newPurchase);
        await purchaseModel.save();

        res.send({
            status: 200,
        })
    } catch (err) {
        console.log('error');
        res.send({
            status: 400,
            message: err
        })
    }
})


module.exports = route;