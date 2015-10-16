var passportLocalStrategy   = require('passport-local').Strategy;
var User            = require('../models/user');
var bCrypt          = require('bcrypt-nodejs');

module.exports = function(calendar){

	calendar.use('signup', new passportLocalStrategy({ passReqToCallback : true }, function(req, username, password, done) {
            findOrCreateUser = function(){
                User.findOne({ 'username' :  username }, function(err, user) {
                    if (err){
                        console.log('Error in SignUp: '+err);
                        return done(err);
                    }
                    // already exists
                    if (user) {
                        console.log('This'  + username + 'exists already.');
                        return done(null, false, req.flash('message','Someone already has this username. Please try another.'));
                    } else {
                        // create the user, if there is no user present with that email.
                        var newUser = new User();
                        newUser.username = username;
                        newUser.password = createHash(password);
                        newUser.email = req.param('email');
                        newUser.firstName = req.param('firstName');
                        newUser.lastName = req.param('lastName');
                        newUser.save(function(err) {
                            if (err){
                                console.log('Error while saving the user: '+err);  
                                throw err;  
                            }  
                            return done(null, newUser);
                        });
                    }
                });
            };
            // Delay the execution of findOrCreateUser and execute the method in the next tick of the event loop
            process.nextTick(findOrCreateUser);
        })
    );
    // Generates hash using bCrypt
    var createHash = function(password){
        return bCrypt.hashSync(password, bCrypt.genSaltSync(10), null);
    }
}