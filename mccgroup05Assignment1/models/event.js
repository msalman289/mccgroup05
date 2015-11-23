
var mongoose = require('mongoose');

module.exports = mongoose.model('Event',{
	id: String,
	date: String,
	starttime: String,
	endtime: String,
	description: String,
	place: String,
	userid: String,
	googleeventid: String
});