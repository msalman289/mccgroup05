var passportLocalStrategy   = require('passport-local').Strategy;
var User                    = require('../models/user');
var bCrypt                  = require('bcrypt-nodejs');

module.exports = function(calendar){ calendar.use('login', new passportLocalStrategy({ passReqToCallback : true }, function(req, username, password, done) {
            User.findOne({'username':username}, 
                function(err, user) {
                    if (err) return done(err);
                    if (!user){
                        console.log('User does not exists with username '+username);
                        return done(null, false, req.flash('message', 'User does not exists.'));                 
                    }
                    if (!validPassword(user, password)){
                        console.log('Wrong Password');
                        return done(null, false, req.flash('message', 'Entered Wrong Password'));
                    }
                    return done(null, user);
                }
            );
        })
    );
    var validPassword = function(user, password){
        return bCrypt.compareSync(password, user.password);
    }
}
