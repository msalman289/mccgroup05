/**
 * Created by Muhammad Salman Ishaq
 * Date 29/10/2015
 */
var pathname;
var eventID;

// DOM Ready
$(document).ready(function() {

	pathname = window.location.pathname;
	eventID  = pathname.substr(pathname.indexOf('t/'), pathname.length).substr(2);

	
	$.getJSON( '/eventinfo/'+eventID, function( data ) {
		$('#container1').html('<div><img class="center-block" src="http://localhost:3000/images/calendar365.png" style="align-center"/></div>');

		$('#container').html('<h2>Edit Event</h2><form><input id="inputDate" type="date" placeholder="Date" required autofocus="autofocus" value="'+data[0].date+'"/><input id="inputStarttime" type="time" placeholder="Start Time" required value="'+data[0].starttime+'"/><input id="inputEndtime" type="time" placeholder="End Time" required value="'+data[0].endtime+'"/><input id="inputDescription" type="text" placeholder="Description" required value="'+data[0].description+'" size="64" class="text-left new-account"/><input id="inputPlace" type="text" placeholder="Location" required autofocus="autofocus" value="'+data[0].place+'" size="40" class="text-left new-account"/><button id="btnEditevent" type="submit" class="text-center new-account">Edit Event</button><br/><span class="clearfix"></span></form>');	

		$('#btnEditevent').on('click', editEvent);      // Sign Up Button Click

	});
});

// editEvent
function editEvent(event) {
    event.preventDefault();

    var errorCnt = 0;
    $('#container input').each(function(index, val) {
        if($(this).val() === '') { errorCnt++; }
    });
	
	if(errorCnt === 0) {
		var editevent = {
			'place': $('#container form input#inputPlace').val(),
            'description': $('#container form input#inputDescription').val(),
            'date': $('#container form input#inputDate').val(),
            'starttime': $('#container form input#inputStarttime').val(),
            'endtime': $('#container form input#inputEndtime').val()
		}
		// Use AJAX to PUT the object to our editEvent service
		$.ajax({
			type: 'PUT',
			data: editevent,
			url: 'http://localhost:3000/save_editevent/'+eventID
		}).done(function( response ) {
			if (response.success) {
				window.location.href = response.url;
			}
			else {
				alert("Error while invoking Edit Event Service: " + response);
			}
		});
	}
	else {
        alert('All Event fields are mandatory.');
        return false;
    }
};