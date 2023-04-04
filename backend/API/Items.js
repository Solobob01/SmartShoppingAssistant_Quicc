const express = require('express');
const mongoose = require('mongoose');
const Items = require('../DB/Items');
const route = express.Router();

route.get('/items', async (req, res) => {
    try {
        console.log("GET ALL ITEMS IN THE STORE");

        const allItems = await Items.find()
        //console.log("all items: ", allItems);

        res.send({
            status: 200,
            items: allItems,
        })
    } catch (err) {
        console.log('error');
        res.send({
            status: 400,
            message: err
        })
    }
})

route.post('/item', async (req, res) => {
    try {
        const {name, location} = req.body;
        
        let item = {};
        item.name = name;
        item.location = location;

        let itemModel = Items(item);
        await itemModel.save();

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

route.get('/getPostById/:id', async (req, res) => {
    try {
        const findId = req.params.id
        console.log(findId)
        const thought = await Thoughts.findById(findId)
        res.json(thought);

    } catch (err) {
        res.send({
            status: 400,
            message: 'error trying to retrieve data'
        })
    }
})

module.exports = route;