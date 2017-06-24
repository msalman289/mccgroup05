/*
* Created by Salman Ishaq
* Help from Internet Source: https://developers.google.com/google-apps/calendar/quickstart/js
*/

var CLIENT_ID = '938045938558-llcpsv49f6iplg5mgg3g4cotkr6upor8.apps.googleusercontent.com';
var SCOPES = ["https://www.googleapis.com/auth/calendar"];
var events = '';
var _event = [];
var when   = '';
var till   = '';

$(document).ready(function() {
  // Your Client ID can be retrieved from your project in the Google
  // Developer Console, https://console.developers.google.com
  $('#container').html('<div><img class="center-block" src="http://130.233.42.186:8080/images/calendar365.png" style="align-center"/></div>');
  checkAuth();
}); 

/**
* Check if current user has authorized this application.
*/
function checkAuth() {
  if(gapi && gapi.auth) {
    gapi.auth.authorize({
        'client_id': CLIENT_ID,
        'scope': SCOPES.join(' '),
        'immediate': true
    }, handleAuthResult);
  }
}

/**
* Handle response from authorization server.
*
* @param {Object} authResult Authorization result.
*/
function handleAuthResult(authResult) {
  var authorizeDiv = document.getElementById('authorize-div');
  if (authResult && !authResult.error) {
      // Hide auth UI, then load client library.
      authorizeDiv.style.display = 'none';
      loadCalendarApi();
  } else {
      // Show auth UI, allowing the user to initiate authorization by
      // clicking authorize button.
      authorizeDiv.style.display = 'inline';
  }
}

/**
* Initiate auth flow in response to user clicking authorize button.
*
* @param {Event} event Button click event.
*/
function handleAuthClick(event) {
  gapi.auth.authorize(
      {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
      handleAuthResult);
  return false;
}

/**
* Load Google Calendar client library. List upcoming events
* once client library is loaded.
*/
function loadCalendarApi() {
  gapi.client.load('calendar', 'v3', listUpcomingEvents);
}

/**
* Print the summary and start datetime/date of the next ten events in
* the authorized user's calendar. If no events are found an
* appropriate message is printed.
*/
function listUpcomingEvents() {
  var request = gapi.client.calendar.events.list({
  'calendarId': 'primary',
  'timeMin': (new Date()).toISOString(),
  'showDeleted': false,
  'singleEvents': true,
  'maxResults': 10,
  'orderBy': 'startTime'
  });
  request.execute(function(resp) {
    events = resp.items;
    if (events.length > 0) {
      appendPre('Upcoming events:');
      var index1 = 0;
      $.each(events, function(){
        _event[index1] = this;
        index1++;
        _event[index1] = this.id;
        index1++;
        // check Google event in mongodb if it is not already stored
        $.getJSON( '/eventinfo1/'+this.id, function( data1 ) {
          if(data1 && data1.length) {
            // Google Event is already in Mongodb
            var ev_index = _event.indexOf(data1[0].googleeventid);
            var evt      = _event[ev_index- 1];
            _event[ev_index] = 0;
            _event[ev_index-1] = 1;
            when  = evt.start.dateTime;
            till  = evt.end.dateTime;
            if(!when) when = evt.start.date;
            if(!till) till = evt.end.date;
            var editevent = {
              'place': evt.location,
              'description': evt.summary,
              'date': when.substr(0,when.indexOf('T')),
              'starttime': when.substr(when.indexOf('T')+1,5),
              'endtime': till.substr(till.indexOf('T')+1,5)
            };
            $.ajax({
              type: 'PUT',
              data: editevent,
              url: 'http://130.233.42.186:8080/save_editevent1/'+ data1[0].googleeventid
            }).done(function( response ) {
              if (response.success) {

              }
              else {
              alert("Error while invoking Edit Event Service: " + response);
              return false;
              }
            });
            appendPre(evt.summary + ' (' + when + ')');
          }
          else {
            // Store Google Event in Mongodb
            var geid = this.url.substr(this.url.indexOf('1/')+2);
            var et_index = _event.indexOf(geid);
            var et    = _event[et_index-1];
            when  = et.start.dateTime;
            till  = et.end.dateTime;
            if(!when) when = et.start.date;
            if(!till) till = et.end.date;

            var newEvent = {
              'date': when.substr(0,when.indexOf('T')),
              'starttime': when.substr(when.indexOf('T')+1,5),
              'endtime': till.substr(till.indexOf('T')+1,5),
              'description': et.summary,
              'place': et.location,
              'googleeventid' : et.id
            };
            $.ajax({
              type: 'POST',
              data: newEvent,
              url: 'http://130.233.42.186:8080/adduserevent',
              dataType: 'JSON'
            }).done(function( response ) {
              if (response) {
              }
              else {
                alert('Error: ' + response);
                return false;
              }
            });
            appendPre(et.summary + ' (' + when + ')');
          }
        });
      });
    }
    else {
      appendPre('No upcoming events found.');
    }
  });
}

/**
* Append a pre element to the body containing the given message
* as its text node.
*
* @param {string} message Text to be placed in pre element.
*/
function appendPre(message) {
  var pre = document.getElementById('output');
  var textContent = document.createTextNode(message + '\\n');
  pre.appendChild(textContent);
}

