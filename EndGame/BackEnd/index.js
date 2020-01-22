const express = require("express");
const bodyParser = require("body-parser");
const helmet = require("helmet");
const mongoose = require("mongoose");
const morgan = require("morgan");
const axios = require("axios");
const config = require("config");
const cors = require("cors");
const dbConfig = config.get("Backend.dbConfig");
const app = express();

//db
mongoose
  .connect(dbConfig.MONGO_URI, {
    useNewUrlParser: true,
    useFindAndModify: false,
    useCreateIndex: true,
    useUnifiedTopology: true
  })
  .then(() => console.log("DB connected"));

mongoose.connection.on("error", err => {
  console.log(`DB connection error: ${err.message}`);
});

const locationRoute = require("./src/routes/location");
const userRoute = require("./src/routes/user");

app.use(morgan("dev"));
app.use(
  bodyParser.urlencoded({
    extended: true
  })
);

app.use(bodyParser.json());
app.use(cors());

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "*");
  res.header(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );
  next();
});

app.use(helmet());

app.use("/api/", locationRoute);
app.use("/api/", userRoute);

app.use((err, req, res, next) => {
  console.log(err);
});

// server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server at: http://localhost:${PORT}`);
});
