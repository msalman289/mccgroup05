/**
 * Created by Muhammad Salman Ishaq
 * Edited by Theodosis Makris
 * Date 29/10/2015
 */

var user;

$(document).ready(function() {
	$.getJSON( '/userinfo', function( data ) {
		user   = data;
		
		$('#container').html('<div><div><img class="center-block" src="http://130.233.42.186:3000/images/calendar365.png" style="align-center"/></div></div>');
        
        $('#userinfo').html('<p><b>Email: </b>' + user.email + '<br><b>First Name: </b>' + user.firstName + '<br><b>Last Name: </b>' + user.lastName +'<br><b>User Name: </b>'+user.username+'<br></p>');
        
        $('#searchevent').html('<form><input id="inputFromDate" type="date" placeholder="From Date"/><input id="inputToDate" type="date" placeholder="To Date"/><input id="inputStartTime" type="time" placeholder="From Time"/><input id="inputEndTime" type="time" placeholder="To Time"/><input id="inputPlace" type="text" placeholder="Location" size="40" class="text-left new-account"/><button id="btnSearchEvent" class="text-center new-account">Search</button><span class="clearfix"></span></form>');

        $('#events').html('<table border="1"><thead><tr><th>#</th><th>Event Date</th><th>Start Time</th><th>End Time</th><th>Location</th><th>Description</th></tr></thead><tbody></tbody></table>');

        $('#btnSearchEvent').on('click', searchEvent);    // Search Event Button Click
	});
});

// Search Event
function searchEvent(event) {
    event.preventDefault();
	
	var searchEvent = {
		'todate': $('#searchevent form input#inputToDate').val(),
		'fromdate': $('#searchevent form input#inputFromDate').val(),
		'starttime': $('#searchevent form input#inputStartTime').val(),
		'endtime': $('#searchevent form input#inputEndTime').val(),
		'place': $('#searchevent form input#inputPlace').val()
	}
	
	$.ajax({
		type: 'GET',
		data: searchEvent,
		url: 'http://130.233.42.186:3000/searchevent',
		dataType: 'JSON'
	}).done(function( response ) {
		if (response) {
			// Clear the form inputs
			$('#searchevent form input').val('');
			// Update the table
			populateSearchedEventsInTable(response.events);
		}
		else {
			alert('Error: ' + response);
		}
	});
};

// Fill table with data
function populateSearchedEventsInTable(data) {
    var tableContent = '';

    eventListData = data;
	if (data && data.length) {
		// For each item in our JSON, add a table row and cells to the content string
		var index = 1;
		$.each(data, function(){
			tableContent += '<tr>';
			tableContent += '<td>' + index + '</td>';
			tableContent += '<td>' + this.date + '</td>';
			tableContent += '<td>' + this.starttime + '</td>';
			tableContent += '<td>' + this.endtime + '</td>';
			tableContent += '<td>' + this.place + '</td>';
			tableContent += '<td>' + this.description + '</td>';
			tableContent += '</tr>';
			index++;
		});

		// Inject the whole content string into our existing HTML table
		$('#events table tbody').html(tableContent);
	}
	else {
		// In case when no event is found against the search criteria Inject empty string into our existing HTML table
		$('#events table tbody').html('');
	}

};
