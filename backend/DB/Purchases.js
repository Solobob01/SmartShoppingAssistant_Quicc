const mongoose = require('mongoose');

const purchases = mongoose.Schema({
    customer :{
        type: mongoose.ObjectId, 
        ref: "users"
    },
    itemsPurchased :
    [
        {
            type: String 
        } 
    ],
});

module.exports = Purchases = mongoose.model('purchases', purchases);
