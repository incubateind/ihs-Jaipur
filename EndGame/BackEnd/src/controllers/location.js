const axios = require("axios");
const config = require("config");
const apiKey = config.get("Backend.rest.apiKey");
const appID = config.get("Backend.rest.appID");
const literacy = require("../../helper/constData");
var isoCountry = require("i18n-iso-countries");

module.exports = {
  locationCoordinates: async (req, res) => {
    let { loc } = req.params;
    loc = encodeURI(loc);
    axios
      .get(
        `https://geocoder.ls.hereapi.com/6.2/geocode.json?searchtext=${loc}&gen=9&apiKey=${apiKey}`
      )
      .then(result => {
        let resAddress =
          result.data.Response.View[0].Result[0].Location.DisplayPosition;
        console.log(resAddress);
        return res.status(200).json(resAddress);
      })
      .catch(function(err) {
        console.error("Error!", err);
        return res.status(400).json({ error: "Location Not Found!" });
      });
  },
  findRestaurants: async (req, res) => {
    const { Latitude, Longitude } = req.body;
    axios
      .get(
        `https://places.ls.hereapi.com/places/v1/discover/explore?in=${Latitude}%2C${Longitude};r=30000&cat=restaurant&apiKey=${apiKey}`
      )
      .then(result => {
        console.log(result.data.results.items);
        return res.status(200).json(result.data.results.items);
      })
      .catch(function(err) {
        console.error("Error!", err);
        return res.status(400).json({ err });
      });
  },
  touristAttractions: async (req, res) => {
    const { Latitude, Longitude } = req.body;
    axios
      .get(
        `https://places.ls.hereapi.com/places/v1/discover/explore?in=${Latitude}%2C${Longitude};r=30000&cat=leisure-outdoor%2Cnatural-geographical%2Csights-museums%2Cshopping&apiKey=${apiKey}`
      )
      .then(result => {
        console.log(result.data.results.items);
        return res.status(200).json(result.data.results.items);
      })
      .catch(function(err) {
        console.error("Error!", err);
        return res.status(400).json({ err });
      });
  },
  literacy: async (req, res) => {
    const { Latitude, Longitude } = req.body;
    let axiosResponse = await axios.get(
      `https://reverse.geocoder.ls.hereapi.com/6.2/reversegeocode.json?prox=${Latitude}%2C${Longitude}%2C250&mode=retrieveAddresses&maxresults=1&gen=9&apiKey=${apiKey}`
    );
    let Country =
      axiosResponse.data.Response.View[0].Result[0].Location.Address.Country;
    Country = isoCountry.getName(Country, "en");
    let Literacy = literacy.filter(function(ele) {
      return ele[0].toUpperCase().includes(Country.toUpperCase());
    });
    if (!Literacy.length) Literacy = [["default", "73.0"]];
    return res.status(200).json({ Country, Literacy: Literacy[0][1] });
  },
  traffic: async (req, res) => {
    const { data } = req.body;

    let url = `https://traffic.ls.hereapi.com/traffic/6.2/flow.json?bbox=${
      data.bounds.tl.Latitude
    },${data.bounds.tl.Longitude};${data.bounds.tl.Latitude + 0.4},${data.bounds
      .tl.Longitude + 0.4}&apiKey=${apiKey}`;

    axios
      .get(url)
      .then(result => {
        // console.log(result.data);
        return res.status(200).json(result.data);
      })
      .catch(err => {
        return res.status(400).json({
          message: "Exit axios get",
          err
        });
      });
  }
};
