/*
* Created by Theodosis Makris
* Help from Internet Source: https://developers.google.com/google-apps/calendar/quickstart/js
*/


var CLIENT_ID = '938045938558-llcpsv49f6iplg5mgg3g4cotkr6upor8.apps.googleusercontent.com';
var SCOPES = ["https://www.googleapis.com/auth/calendar"];
var googleeventid = '';
var event1 = [];

$(document).ready(function() {
  // Your Client ID can be retrieved from your project in the Google
  // Developer Console, https://console.developers.google.com
  $('#container').html('<div><img class="center-block" src="http://130.233.42.186:3000/images/calendar365.png" style="align-center"/></div>');
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
  gapi.client.load('calendar', 'v3', addEventsToGoogle);
}

/**
* Print the summary and start datetime/date of the next ten events in
* the authorized user's calendar. If no events are found an
* appropriate message is printed.
*/
function addEventsToGoogle() {
  $.getJSON('/eventlist1', function( data2 ) {
    if(data2 && data2.length) {
    var index = 1;
    $.each(data2, function(){
        var event = {
          'summary': this.description,
          'location': this.place,
          'description': 'local event id=' + this._id + ': Description- ' + this.description,
          'start': {
            'dateTime': this.date+'T'+this.starttime+':00',
            'timeZone': 'Europe/Helsinki'
          },
          'end': {
            'dateTime': this.date+'T'+this.endtime+':00',
            'timeZone': 'Europe/Helsinki'
          }
        };
        var request = gapi.client.calendar.events.insert({
          'calendarId': 'primary',
          'resource': event
        });
        request.execute(function(eventres) {
          // now update the googleeventid in mongodb
          var when = eventres.start.dateTime;
          if (!when) when = eventres.start.date;
          var till = eventres.end.dateTime;
          if (!till) till = eventres.end.date; 
          
          var e = eventres.description;
          var et  = e.substr(e.indexOf('=')+1,e.indexOf(':'));
          var e_id = et.substr(0,et.indexOf(':'));
          var editevent = {
            'googleeventid': eventres.id
          }
          $.ajax({
            type: 'PUT',
            data: editevent,
            url: 'http://130.233.42.186:3000/updategoogleeventid/'+e_id
          }).done(function( response ) {
            if (response.success) {
            }
            else {
              alert("Error while invoking Edit Event Service: " + response);
              return false;
            }
          });
          appendPre('Event created: ' + eventres.htmlLink);
        });
        index++;
      });
    }
    else {
      appendPre('No new local events to Add in Google Calendar.');
      // Update the local events in Google
      $.getJSON('/eventlist', function( data3 ) {
          if(data3 && data3.length) {
              var index1 = 0;
              $.each(data3, function(){
                  event1[index1] = {
                     'summary': this.description,
                     'location': this.place,
                     'description' : 'local event id=' + this._id + ': Description- ' + this.description,
                     'start': {
                       'dateTime': this.date+'T'+this.starttime+':00',
                       'timeZone': 'Europe/Helsinki'
                      }, 'end': {
                       'dateTime': this.date+'T'+this.endtime+':00',
                       'timeZone': 'Europe/Helsinki'
                      }
                  };
                  index1++;
                  event1[index1] = this.googleeventid;
                  index1++;
                  googleeventid = this.googleeventid;
                  var request = gapi.client.calendar.events.get({ 
                          'calendarId': 'primary',
                          'eventId': googleeventid
                  });
                  request.execute(function(eventres) {
                  var e = eventres.description; var et = null; var e_id = null;
                  if (e)  et   = e.substr(e.indexOf('=')+1,e.indexOf(':'));
                  if (et) e_id = et.substr(0,et.indexOf(':'));
                  if(eventres && eventres.status == "cancelled" && e_id) {
                    // delete E_id from local DB as that event is cancelled from Google Calendar
                    $.ajax({
                        type: 'DELETE',
                        url: 'http://130.233.42.186:3000/deleteevent/' + e_id
                    }).done(function( response ) {
                        if (response) {
                           appendPre('Event with description: "' + response.description + '" is deleted from Local  calendar as that event is cancelled in Google Calendar.');
                        }
                        else {
                           alert('response: ' + response);
                           return false;
                        }
                    });
                  }
                  else {
                     // update the event in Google Calendar
                     var e_index  = event1.indexOf(eventres.id) - 1;
                     var resource = event1[e_index];
                     var request = gapi.client.calendar.events.update({
                      'calendarId': 'primary',
                      'eventId': eventres.id,
                      'resource': resource
                     });
                     request.execute(function(eventres) {
                       appendPre('Event with summary updated in Google Calendar i.e. ' + eventres.summary);
                     });
                  }
                });
            });
          }
          else {
            appendPre('No local events to update in Google Calendar.');
          }
      });
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
  var textContent = document.createTextNode(message + ' \\n ');
  pre.appendChild(textContent);
}

