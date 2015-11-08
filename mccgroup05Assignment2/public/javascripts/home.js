/**
 * Created by Muhammad Salman Ishaq
 * Date 29/10/2015
 */

var user;

$(document).ready(function() {
	$.getJSON( '/userinfo', function( data ) {
		user   = data;
		
		$('#container').html('<div><img class="center-block" src="http://130.233.42.186:3000/images/calendar365.png" style="align-center"/></div>');
        
        $('#userinfo').html('<p><b>Email: </b>' + user.email + '<br><b>First Name: </b>' + user.firstName + '<br><b>Last Name: </b>' + user.lastName +'<br><b>User Name: </b>'+user.username+'<br></p>');
        
        $('#addevent').html('<form><input id="inputEventdate" type="date" placeholder="Date" required autofocus="autofocus"/><input id="inputEventstarttime" type="time" placeholder="Start Time" required/><input id="inputEventendtime" type="time" placeholder="End Time" required/><input id="inputEventdescription" type="text" placeholder="Description" required size="66" class="text-left new-account"/><input id="inputEventplace" type="text" name="place" placeholder="Location" required autofocus="autofocus" size="40" class="text-left new-account"/><button id="btnAddEvent" class="text-center new-account">Add Event</button><span class="clearfix"></span></form>');

        $('#events').html('<table border="1"><thead><tr><th>#</th><th>Event Date</th><th>Start Time</th><th>End Time</th><th>Location</th><th>Description</th><th>Edit</th><th>Delete</th></tr></thead><tbody></tbody></table>');

        populateEventListTable();

        $('#btnAddEvent').on('click', addEvent);                                     // Add Event Button Click
        
        $('#events table tbody').on('click', 'td a.linkdeleteevent', deleteEvent);   // Delete Event link click
	
		$('#events table tbody').on('click', 'td a.linkeditevent', EditEvent);        // Edit Event link click

	});
});

// Add Event
function addEvent(event) {
    event.preventDefault();
	var errorCount = 0;
    $('#addevent input').each(function(index, val) {
        if($(this).val() === '') { errorCount++; }
    });
	if(errorCount === 0) {
		var newEvent = {
			'date': $('#addevent form input#inputEventdate').val(),
			'starttime': $('#addevent form input#inputEventstarttime').val(),
			'endtime': $('#addevent form input#inputEventendtime').val(),
			'description': $('#addevent form input#inputEventdescription').val(),
			'place': $('#addevent form input#inputEventplace').val()
		}
		// Use AJAX to post the object to our addevent service
		$.ajax({
			type: 'POST',
			data: newEvent,
			url: 'http://130.233.42.186:3000/adduserevent',
			dataType: 'JSON'
		}).done(function( response ) {
			if (response) {
				// Clear the form inputs
				$('#addevent form input').val('');
				// Update the table
				populateEventListTable();
			}
			else {
				alert('Error: ' + response);
			}
		});
	}
	else {
		alert('Please fill in all fields');
        return false;
	}
};

// Fill table with data
function populateEventListTable() {
    var tableContent = '';
    // jQuery AJAX call for JSON
    $.getJSON( '/eventlist', function( data ) {
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
				tableContent += '<td><a href="#" class="linkeditevent" rel="' + this._id + '"><img src="http://130.233.42.186:3000/images/edit2.png" style="width:30px;height:30px;"/></a></td>';
				tableContent += '<td><a href="#" class="linkdeleteevent" rel="' + this._id + '"><img src="http://130.233.42.186:3000/images/delete2.png" style="width:30px;height:30px;"/></a></td>';
				tableContent += '</tr>';
				index++;
			});

			// Inject the whole content string into our existing HTML table
			$('#events table tbody').html(tableContent);
		}
		else{
			// Inject the whole content string into our existing HTML table
			$('#events table tbody').html(tableContent);

		}
    });
};

// Edit Event
function EditEvent(event) {
    event.preventDefault();
    // Pop up a confirmation dialog
    var confirmation = confirm('Are you sure you want to edit this event?');
    // Check and make sure the user confirmed
    if (confirmation === true) {
		window.location.href = '/editevent/'+$(this).attr('rel');
    }
    else {
        return false;
    }
};

// Delete Event
function deleteEvent(event) {
    event.preventDefault();
    // Pop up a confirmation dialog
    var confirmation = confirm('Are you sure you want to delete this event?');
    // Check and make sure the user confirmed
    if (confirmation === true) {
        $.ajax({
            type: 'DELETE',
            url: 'http://130.233.42.186:3000/deleteevent/' + $(this).attr('rel')
        }).done(function( response ) {
            // Check for a successful response
            if (response) {
				// Update the event list table
				populateEventListTable();
            }
            else {
                alert('Error: ' + response);
            }
        });
    }
    else {
        return false;
    }
};
