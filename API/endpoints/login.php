<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    //set up response array object that will send info back to application
    $response = array();

    //check to make sure email and password parameters were posted
   if(isset($_POST['email']) && isset($_POST['password'])) {
      $email = $_POST['email'];
      $password = $_POST['password'];
      //check to see if matching user exists
      $stmt = $con -> prepare("SELECT userID, first_name, last_name, email,
        total_checkins FROM users WHERE email LIKE ? AND password = ?");
        $stmt->bind_param("ss",$email,$password);
        $stmt->execute();
        $stmt->store_result();
        if($stmt->num_rows > 0) {
          //user exists, return that user as a JSON Object
          $stmt->bind_result($id, $firstName, $lastName, $email, $totalCheckins);
          $stmt->fetch();
          //fill array with necessary user information
          $user = array(
            'userID'=>$id,
            'first_name'=>$firstName,
            'last_name'=>$lastName,
            'email'=>$email,
            'total_checkins'=>$totalCheckins
          );
          //everything is good to go
          $response['error'] = false;
          $response['message'] = 'Login Successful';
          $response['user'] = $user;
      } else {
        //user doesn't exist, there's an error
        $response['error'] = true;
        $response['message'] = 'User not found';
      }
    } else {
      $response['error'] = true;
      $response['message'] = 'No information provided';
    }

    //display response on screen as JSON array
    echo json_encode($response);
 ?>
