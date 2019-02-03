<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    //set up response array object that will send info back to application
    $response = array();

    if(isset($_POST['userID'])) {
      $userID = $_POST['userID'];
      $stmt = $con -> prepare("DELETE FROM users WHERE userID = ?");
      $stmt->bind_param("i",$userID);
      $stmt->execute();

      //everything is good to go
      $response['error'] = false;
      $response['message'] = 'User deleted';
    } else {
      $response['error'] = true;
      $response['message'] = "No ID given";
    }
    //display response on screen as JSON array
    echo json_encode($response);
?>
