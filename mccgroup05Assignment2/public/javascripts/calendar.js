/**
 * Created by Muhammad Salman Ishaq
 * Date 29/10/2015
 * Help from Internet Source: http://fullcalendar.io/js/fullcalendar-2.4.0/demos/theme.html
 */

$(document).ready(function() {
	$('#container').html('<div><img class="center-block" src="http://localhost:3000/images/calendar365.png" style="align-center"/></div>');
	$.getJSON( '/eventlist', function( data ) {

		var events = [];
		if (data && data.length) {
			k=0;
			$.each(data, function(){
				events[k] = {title: this.description, start: this.date+'T'+this.starttime+':00', end: this.date+'T'+this.endtime+':00'};
				k++;
			});
		}

		$('#calendar').fullCalendar({
			theme: true,
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			defaultDate: new Date(), //'2015-02-12',
			editable: true,
			eventLimit: true, // allow "more" link when too many events
			events: events
		});
	});
	
});