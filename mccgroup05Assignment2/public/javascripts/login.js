/**
 * Created by Muhammad Salman Ishaq
 * Date 29/10/2015
 */

var userListData  = [];
var eventListData = [];

// DOM Ready
$(document).ready(function() {
	
	$('#loginUser').html('<form class="form-signin"><input id="inputUserName" type="text" placeholder="Username" required autofocus="autofocus" class="form-control"/><br/><input id="inputUserPassword" type="password" placeholder="Password" required class="form-control"/><br/><button id="btnLoginUser" class="btn btn-lg btn-primary btn-block">Login</button><span class="clearfix"></span></form>');

	$('#signupUser').html('<button id="btnSignupUser" style="margin-left: 140px;  background-color: #428bca; border-color: #357ebd; color: #fff; border-radius: 6px;" class="text-center new-account">Sign up</button>');

	$('#btnLoginUser').on('click', loginUser);        // Login Button Click
	$('#btnSignupUser').on('click', signupUser);      // Sign Up Button Click
});

// Login User
function loginUser(event) {
    event.preventDefault();
    var errorCnt = 0;
    $('#loginUser input').each(function(index, val) {
        if($(this).val() === '') { errorCnt++; }
    });
    if(errorCnt === 0) {
        var loginuser = {
            'username': $('#loginUser form input#inputUserName').val(),
            'password': $('#loginUser form input#inputUserPassword').val()
        }
        // Use AJAX to post the object to our login service
        $.ajax({
            type: 'POST',
            data: loginuser,
            url: 'http://130.233.42.186:3000/login'
        }).done(function( response ) {
            if(response.success){
				window.location.href = response.url;
			}
			else{
				alert(response.message);
			}
        });
    }
    else {
        alert('Please provide correct information for Login');
        return false;
    }
};

// SignUp User
function signupUser(event) {
    event.preventDefault();
	// Use AJAX to Get the object to our SignUp service
	$.ajax({
		type: 'GET',
		url: 'http://130.233.42.186:3000/signup'
	}).done(function( response ) {
		if (response.success) {
			window.location.href = response.url;
		}
		else {
			alert(response.message);
		}
	});
};
