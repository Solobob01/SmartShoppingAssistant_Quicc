const mongoose = require('mongoose');

const users = mongoose.Schema({
    fullName :{
        type: String
    },    
});

module.exports = Users = mongoose.model('users', users);
