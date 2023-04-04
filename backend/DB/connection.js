const mongoose = require('mongoose');

const connectDB = async (uri) => {
    try {
      const connection = await mongoose.connect(uri,   {
        useNewUrlParser: true,
        useUnifiedTopology: true,
      }
      );
      console.log(`MongoDB Connected: ${connection.connection.host}`);
    } catch (error) {
      console.log(`Error: ${error.message}`);
      // process.exit(1);
    }
};

module.exports = connectDB;