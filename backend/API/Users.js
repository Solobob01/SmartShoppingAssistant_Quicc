const express = require('express');
const mongoose = require('mongoose');
const Users = require('../DB/Users');
const route = express.Router();

route.get('/users', async (req, res) => {
    try {
        console.log("GET ALL PURCHASES");

        const allUsers = await Users.find()
        console.log("all users: ", allUsers);

        res.send({
            status: 200,
            users: allUsers,
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