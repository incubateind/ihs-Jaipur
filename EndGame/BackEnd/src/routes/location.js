const router = require("express-promise-router")();
const locationController = require("../controllers/location");

router.get("/location/:loc", locationController.locationCoordinates);

router.post("/restaurants/", locationController.findRestaurants);
router.post("/touristAttractions/", locationController.touristAttractions);
router.post("/literacy/", locationController.literacy);
router.post("/traffic", locationController.traffic);

module.exports = router;
