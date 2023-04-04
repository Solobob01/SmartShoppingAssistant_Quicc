const mongoose = require('mongoose');

const lanes = mongoose.Schema({
    laneNumber :{
        type: String
    },
    numberOfPeople :{
        type: String
    },
});

module.exports = Lanes = mongoose.model('lanes', lanes);
