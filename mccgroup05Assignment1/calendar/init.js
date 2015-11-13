var loginuser  = require('./login');
var usersignup = require('./signup');
var User   = require('../models/user');

module.exports = function(calendar){
    calendar.serializeUser(function(user, done) {
        done(null, user._id);
    });
    calendar.deserializeUser(function(id, done) {
        User.findById(id, function(err, user) { done(err, user); });
    });
    loginuser(calendar);
    usersignup(calendar);
}
