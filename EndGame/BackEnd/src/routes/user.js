const router = require("express-promise-router")();
const userController = require("../controllers/user");

router.post("/newUser", userController.create);

router.patch("/user/", userController.update);

module.exports = router;
