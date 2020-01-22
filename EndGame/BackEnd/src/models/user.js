const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const userSchema = new Schema({
  userName: {
    type: String,
    maxlength: 100
  },
  email: {
    type: String,
    trim: true,
    required: true,
    lowercase: true
  },
  password: {
    type: String,
    required: true
  },
  contactNo: {
    type: String,
    trim: true
  },
  created: {
    type: Date,
    default: Date.now
  },
  Literacy: {
    type: String
  },
  CrimeRate: {
    type: String
  },
  Restaurants: {
    type: String
  },
  Tourist: {
    type: String
  },
  Attractions: {
    type: String
  },
  Traffic: {
    type: String
  }
});

module.exports = mongoose.model("user", userSchema);
