<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    //set up response array object that will send info back to application
    $response = array();

    if(isset($_POST['coffeeID']) && isset($_POST['userID']) && isset($_POST['coffeeScore'])
        && isset($_POST['wifiScore']) && isset($_POST['order']) && isset($_POST['atmosphere'])) {
      $coffeeID = $_POST['coffeeID'];
      $userID = $_POST['userID'];
      $coffeeScore = $_POST['coffeeScore'];
      $wifiScore = $_POST['wifiScore'];
      $order = $_POST['order'];
      $atmosphere = $_POST['atmosphere'];

      // start transaction
      $con -> query("START TRANSACTION");

      // define the SQL for both statements
      $stmtCheckIn = $con -> prepare("INSERT INTO checkins (userID, coffeeID, coffeeScore, wifiScore, order_item, submit_time, atmosphere)
      VALUES (?,?,?,?,?,now(),?)");
      $stmtUpdateUser = $con -> prepare("UPDATE users SET total_checkins = total_checkins + 1 WHERE userID = ?");
      //add parameters to the statements
      $stmtCheckIn -> bind_param("iiddss",$userID, $coffeeID, $coffeeScore, $wifiScore, $order,$atmosphere);
      $stmtUpdateUser -> bind_param("i",$userID);

      if($stmtCheckIn -> execute() && $stmtUpdateUser -> execute()) {
        $con -> query("COMMIT");
        $response['error'] = false;
        $response['message'] = "Check In transaction successful";
      } else {
        $con -> query("ROLLBACK");
        $response['error'] = true;
        $response['message'] = "Had to rollback transaction";
      }
    } else {
      $response['error'] = true;
      $response['message'] = "Info not provided";
    }

    echo json_encode($response);
?>
