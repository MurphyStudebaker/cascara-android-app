<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    //set up response array object that will send info back to application
    $response = array();

    //check to make sure an id was posted, name of id determines which checkins to pull
   if(isset($_POST['userID'])) {
      //pull all checkins by a certain user
      $id = $_POST['userID'];

      $stmt = $con -> prepare("SELECT checkins.coffeeID, order_item, house_name, img
                                FROM checkins
                                LEFT JOIN coffeeshops ON checkins.coffeeID = coffeeshops.coffeeID
                                WHERE userID = ?");
        $stmt->bind_param("i",$id);
        $stmt->execute();
        $stmt->store_result();
        if($stmt->num_rows > 0) {
          //user exists, return that user as a JSON Object
          $stmt->bind_result($coffeeID, $order_item, $house_name, $img);

          $checkins = array();

          while($stmt->fetch()) {
            //fill array with necessary user information
            $checkIn = array(
              'coffeeID'=>$coffeeID,
              'orderItem'=>$order_item,
              'houseName'=>$house_name,
              'img'=>$img
            );
            array_push($checkins, $checkIn);
          }

          //everything is good to go
          $response['error'] = false;
          $response['checkins'] = $checkins;
      } else {
        //no match for that id
        $response['error'] = true;
        $response['message'] = 'User not found';
      }
    } else {
      $response['error'] = true;
      $response['message'] = 'No information provided';
    }

    //display response on screen as JSON array
    echo json_encode($checkins);
 ?>
