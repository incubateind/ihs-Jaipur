const axios = require("axios");
const config = require("config");
const User = require("../models/user");

module.exports = {
  create: async (req, res) => {
    let { email } = req.body;
    let alreadyExists = await User.findOne({ email });

    if (alreadyExists) {
      const error = { error: "User already exists" };
      return res.status(400).json(error);
    }
    const user = new User(req.body);
    const result = await user.save();

    if (!result) {
      const error = { error: "Saving to DB Error" };
      return res.status(400).json(error);
    }
    res.status(200).json(user);
  },
  update: async (req, res) => {
    let user = await User.findOne({ email: req.body.email });
    if (!user) {
      const error = { error: "User doesnot exist" };
      return res.status(400).json(error);
    }
    const raw = await User.updateOne({ email: user.email }, { $set: req.body });
    if (!raw) {
      const error = { error: "Something went wrong" };
      res.status(400).json(error);
    }
    res.status(200).json({ message: "User updated successfully" });
  }
};
