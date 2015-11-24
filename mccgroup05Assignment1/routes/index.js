/*
	Created by Salman 5-Oct-2015
	Edited by Theodosis 14-Oct-2015
*/

var dateFormat = require('dateformat');
var express    = require('express');
var router     = express.Router();
var Event      = require('../models/event');
var mtOverride = require('method-override'); //used to manipulate POST

// Control will come to this function when ever the _method is being used.
router.use(mtOverride(function(req, res){
      if (req.body && typeof req.body === 'object' && '_method' in req.body) {
        var method = req.body._method // look in urlencoded POST bodies and delete it
		if(method == 'DELETE')
			delete req.body._method
		if(method == 'PUT')
			console.log(req.body._method);
        return method
      }
}))

var isValid = function (req, res, next) {
  if (req.isAuthenticated())
    return next();
  res.redirect('/');
}

module.exports = function(passport){
  // Index Page
  router.get('/', function(req, res) {
    //res.render('index', { message: req.flash('message') });
	res.json({message: "Provide username & password", error_message: true}); // new addition
  });
  
  // Search Page
  router.get('/search', function(req, res) {
    var testSearchRes = Event.find();
    testSearchRes.where('userid').equals(req.user._id);
    if(req.query.fromdate) {
		testSearchRes.where('date').gte(req.query.fromdate);          //testSearchRes.where('date').equals(req.query.fromdate);
	}
	if(req.query.todate) {
		testSearchRes.where('date').lte(req.query.todate);
	}
    if(req.query.starttime) {
		testSearchRes.where('starttime').gte(req.query.starttime);   //testSearchRes.where('starttime').equals(req.query.starttime);
	}
    if(req.query.endtime) {
		testSearchRes.where('endtime').lte(req.query.endtime);       //testSearchRes.where('endtime').equals(req.query.endtime);
	}
    if(req.query.place) {
		testSearchRes.where('place').equals(req.query.place);
	}
	
	//console.log(testSearchRes);
	testSearchRes.exec(function(err, events){
		if (err) {
			console.log(err);
		}
		else {
			//console.log(events);
			//res.render('search', { "user": req.user, "events": events});
			res.json({ "user": req.user, "events": events}); // new addition
		}
	});
  });

  // Login Page 
  router.post('/login', passport.authenticate('login', { successRedirect: '/home', failureRedirect: '/', failureFlash : true }));
  
  // Signup Get
  router.get('/signup', function(req, res){
    //res.render('register',{message: req.flash('message')});
	res.json({message:"Enter User Credentials i.e. username, firstname, lastname, email, password", error_message: true});
  });
  
  // Signup Post 
  router.post('/signup', passport.authenticate('signup', { successRedirect: '/home', failureRedirect: '/signup', failureFlash : true }));
 
  // Home Page
  router.get('/home', function(req, res){
	var query = Event.find({userid: req.user._id});
	query.where('date').gte(new Date().toISOString());
	query.sort({date: 'asc'});
	query.exec(function(err, events){
		if (err) {
			console.log(err);
		}
		else {
			//res.render('home', { "user": req.user, "events": events});
			res.json({ "error_message": false, "message": 'Hello', "user": req.user, "events": events}); // new addition
		}
    });
  });

  // Signout
  router.get('/signout', function(req, res) {
    req.logout();
    res.redirect('/');
  });
  
  // adduserevent.
  router.post('/adduserevent', function(req, res) {
  	var newEvent = new Event();
	newEvent.date = req.body.date; 
	newEvent.starttime = req.body.starttime;
	newEvent.endtime = req.body.endtime;
	newEvent.place = req.body.place;
	newEvent.description = req.body.description;
	newEvent.userid = req.body.userid;
	newEvent.googleeventid = "";
	newEvent.save(function(err,events) {
		if (err) {
			console.log(err);
		}
		else {
			res.json(events);
		}
	});
  });
  
  // deleteuser. 
  router.delete('/deleteevent/:id', function(req, res) {
	//console.log(req.params.id);
	Event.findOneAndRemove({_id:req.params.id}, function(err, events) {
		if (err) {
			console.log(err);
		}
		else {
			//console.log(events);
			//res.redirect("/home");
			res.json({event:events}); // new addition
		}
	});
  });
  
  // Edit display call using GET
  router.get('/editevent/:id', function(req, res) {
	var query = Event.find({_id: req.params.id});
	query.exec(function(err, events){
	  if (err) {
		console.log(err);
	  }
	  else {
		//console.log(events);
		//res.render('edit', { "user": req.user, "events": events});
		res.json({ "user": req.user, "events": events}); // new addition
	  }
    });
  });
  
  // Edit PUT call 
  router.put('/save_editevent/:id', function(req, res) {
	var query = {_id: req.params.id};
	var update = {date:req.body.date, starttime:req.body.starttime, endtime:req.body.endtime, description:req.body.description, place:req.body.place};
	var options = {new: true};
	Event.findOneAndUpdate(query, update, options, function(err, events) {
	  if (err) {
		console.log(err);
	  }
	  else {
		//console.log(events);
		//res.redirect("/home");
		res.json({event:events}); // new addition 
	  }
	});
  });
  
  // Get valid events Information
  router.get('/eventlist/:id', function(req, res) { 
	var query = Event.find({userid: req.params.id});
	query.sort({date: 'asc'});
	query.exec(function(err, events){
		if (err) {
			console.log(err);
		}
		else {
			res.json({"events": events});
		}
	});
  });
  return router;
}