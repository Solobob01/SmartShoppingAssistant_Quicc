const mongoose = require('mongoose');

const items = mongoose.Schema({
    name :{
        type: String
    },
    location :{
        type: String
    },
});

module.exports = Items = mongoose.model('items', items);
