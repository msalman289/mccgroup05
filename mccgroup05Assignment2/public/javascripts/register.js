/**
 * Created by Muhammad Salman Ishaq
 * Date 29/10/2015
 */

// DOM Ready
$(document).ready(function() {
	
	$('#signupUser').html('<form class="form-signin"><input id="inputUsername" type="text" placeholder="Username" required autofocus="autofocus" class="form-control"/><input id="inputPassword" type="password" placeholder="Password" required class="form-control nomargin"/><input id="inputEmail" type="email" placeholder="Email" required class="form-control"/><input id="inputFirstName" type="text" placeholder="First Name" required class="form-control"/><input id="inputLastName" type="text" placeholder="Last Name" required class="form-control"/><button id="btnRegisterUser" class="btn btn-lg btn-primary btn-block">Register</button></form><span class="clearfix"></span>');	

	$('#btnRegisterUser').on('click', registerUser);      // Sign Up Button Click
});

// SignUp User
function registerUser(event) {
    event.preventDefault();

    var errorCnt = 0;
    $('#signupUser input').each(function(index, val) {
        if($(this).val() === '') { errorCnt++; }
    });
	
	if(errorCnt === 0) {
		var signupuser = {
			'username': $('#signupUser form input#inputUsername').val(),
            'password': $('#signupUser form input#inputPassword').val(),
            'email': $('#signupUser form input#inputEmail').val(),
            'firstName': $('#signupUser form input#inputFirstName').val(),
            'lastName': $('#signupUser form input#inputLastName').val()
		}
		// Use AJAX to POST the object to our registerUser service
		$.ajax({
			type: 'POST',
			data: signupuser,
			url: 'http://130.233.42.186:3000/signup'
		}).done(function( response ) {
			if (response.success) {
				window.location.href = response.url;
				if(response.message)
					alert(response.message);
			}
			else {
				if(response.message)
					alert(response.message);
			}
		});
	}
	else {
        alert('Please provide all information for SignUp');
        return false;
    }
};