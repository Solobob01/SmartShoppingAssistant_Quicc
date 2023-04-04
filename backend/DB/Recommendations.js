const mongoose = require('mongoose');

const recommendations = mongoose.Schema({
    item: {
        type: String
    },
    recommendations:
    [
        {
            type: String
        }
    ],
});

module.exports = Recommendations = mongoose.model('recommendations', recommendations);
