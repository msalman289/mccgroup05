/*
	Created by Salman 5-Oct-2015
	Edited by Theodosis 14-Oct-2015
	Edited by Salman 29-Oct-2015
*/

var dateFormat = require('dateformat');
var express    = require('express');
var router     = express.Router();
var Event      = require('../models/event');
var mtOverride = require('method-override'); //used to manipulate POST


router.use(mtOverride(function(req, res){
	// Control will come to this function when ever the _method is being used.
      if (req.body && typeof req.body === 'object' && '_method' in req.body) {
          var method = req.body._method // look in urlencoded POST bodies and delete it
		  if(method == 'DELETE')
			 delete req.body._method
		  if(method == 'PUT')
			 console.log(req.body._method);
          return method
      }
  })
)

var isValid = function (req, res, next) {
  if (req.isAuthenticated())
    return next();
  res.redirect('/');
}


module.exports = function(passport){

  // Index Page  i.e. http://130.233.42.186:8080/
  router.get('/', function(req, res) {
    res.render('index', {});
  });
  
  // Search Page i.e. http://130.233.42.186:8080/search
  router.get('/search', isValid, function(req, res) {
    	res.render('search', {});
  });

  // Login
  router.post('/login', passport.authenticate('login', { successRedirect: '/validUser', failureRedirect: '/notValidUser', failureFlash : true }));
  
  
  router.get('/notValidUser', function(req, res){
    res.json({"success":false, "message": "Entered credentials are not correct"});
  });
 
  router.get('/validUser', function(req, res){
    res.json({"success":true, "url":'/home'});
  });
  
 
  // Signup Get
  router.get('/signup', function(req, res){
    res.json({"success":true, "url":'/register', "message": req.flash('message')});
  });

  // Signup Get
  router.get('/signup1', function(req, res){
    res.json({"success":false, "message": "Entered wrong credentials or user already exist."});
  });
  
  // Register Page i.e. http://130.233.42.186:8080/register
  router.get('/register', function(req, res){
    res.render('register',{});
  });

  // Signup Post 
  router.post('/signup', passport.authenticate('signup', { successRedirect: '/validUser', failureRedirect: '/signup1', failureFlash : true }));
 
  // Home Page i.e. http://130.233.42.186:8080/home
  router.get('/home', isValid, function(req, res){
	res.render('home', {});
  });

  // Signout
  router.get('/signout', function(req, res) {
    req.logout();
    res.redirect('/');
  });
  
  // adduserevent.
  router.post('/adduserevent', isValid, function(req, res) {
  	var newEvent = new Event();
  	newEvent.date = req.body.date; 
  	newEvent.starttime = req.body.starttime;
  	newEvent.endtime = req.body.endtime;
  	newEvent.place = req.body.place;
  	newEvent.description = req.body.description;
  	newEvent.userid = req.user._id;
    newEvent.googleeventid = req.body.googleeventid;
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
	Event.findOneAndRemove({_id:req.params.id}, function(err, events) {
		if (err) {
			console.log(err);
		}
		else {
			res.json(events);
		}
	});
  });
  
  // Edit display call using GET
  router.get('/editevent/:id', isValid, function(req, res) {
	var query = Event.find({_id: req.params.id});
	query.exec(function(err, events){
	  if (err) {
		console.log(err);
	  }
	  else {
		res.render('edit', {});
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
		res.json({"success":true, "url":"/home"});
	  }
	});
  });
  
  // Edit PUT call 
  router.put('/updategoogleeventid/:id', function(req, res) {
  var query = {_id: req.params.id};
  var update = {googleeventid:req.body.googleeventid};
  var options = {new: true};
  Event.findOneAndUpdate(query, update, options, function(err, events) {
    if (err) {
    console.log(err);
    }
    else {
    res.json({"success":true});
    }
  });
  });

  // Edit on the bsis of googleEvent Id 
  router.put('/save_editevent1/:id', function(req, res) {
    var query = {googleeventid: req.params.id};
    var update = {date:req.body.date, starttime:req.body.starttime, endtime:req.body.endtime, description:req.body.description, place:req.body.place};
    var options = {new: true};
    Event.findOneAndUpdate(query, update, options, function(err, events) {
      if (err) {
      console.log(err);
      }
      else {
      res.json({"success":true});
      }
    });
  });

  // Get userInfo
  router.get('/userinfo', function(req, res) { res.json(req.user);});
  
  // Get Single Event Info by unique event id
  router.get('/eventinfo/:id', function(req, res) {
  	var query = Event.find({_id: req.params.id});
  	query.exec(function(err, _event){
			if (err) {
				console.log(err);
			}
			else {
				res.json(_event);
			}
		});
  });

  // Get Single Event Info by unique google event id
  router.get('/eventinfo1/:id', function(req, res) {
    var query = Event.find({googleeventid: req.params.id});
    query.exec(function(err, _event){
      if (err) {
        console.log(err);
      }
      else {
        res.json(_event);
      }
    });
  });

  // Get valid events Information
  router.get('/eventlist', function(req, res) { 
	if(req.user) {	
		var query = Event.find({userid: req.user._id});
		//query.where('date').gte(new Date().toISOString());     /// Undo this comment
		query.sort({date: 'asc'});
		query.exec(function(err, events){
			if (err) {
				console.log(err);
			}
			else {
				res.json(events);
			}
		});
	}
	else {
		res.json({});
	}
  }); 

  // Get events with googleeventid = null
  router.get('/eventlist1', function(req, res) { 
  if(req.user) {  
    var query = Event.find({userid: req.user._id});
    query.where('googleeventid').equals(null);
    query.exec(function(err, events){
      if (err) {
        console.log(err);
      }
      else {
        res.json(events);
      }
    });
  }
  else {
    res.json({});
  }
  }); 

  // Searched events
  router.get('/searchevent', isValid, function(req, res) {
    var testSearchRes = Event.find();
    testSearchRes.where('userid').equals(req.user._id);
    if(req.query.fromdate)	 testSearchRes.where('date').gte(req.query.fromdate);
	if(req.query.todate)	 testSearchRes.where('date').lte(req.query.todate);
	if(req.query.starttime)  testSearchRes.where('starttime').gte(req.query.starttime);
	if(req.query.endtime)    testSearchRes.where('endtime').lte(req.query.endtime);
    if(req.query.place) 	 testSearchRes.where('place').regex(req.query.place);
	testSearchRes.exec(function(err, events){
		if (err) console.log(err);
		else res.json({ "user": req.user, "events": events});
	});
  });

  router.get('/calendarview', function(req, res) { 
  	res.render('calendar',{});
  });
  
  router.get('/fromgoogle', function(req, res) { 
    res.render('fromgoogle',{});
  });
  
  router.get('/togoogle', function(req, res) { 
    res.render('togoogle',{});
  });

  return router;
}