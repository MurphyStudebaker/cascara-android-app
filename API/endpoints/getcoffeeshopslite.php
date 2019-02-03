<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    //fetch only information needed for small display of coffee shops
    $stmt = $con -> prepare("SELECT coffeeID, house_name, coffee_score, wifi_score, img FROM coffeeshops");
    $stmt->execute();
    $stmt->store_result();
    if($stmt->num_rows > 0) {
      //query was successful, return all of the objects
      $stmt->bind_result($coffeeID, $houseName, $coffeeScore, $wifiScore, $img);

      $coffeeShops = array(); //will hold all the results

      // iterate through each row and build a shop object
      while($stmt->fetch()){
       $currShop = array(); //holds the current row
       $currShop['coffeeID'] = $coffeeID;
       $currShop['houseName'] = $houseName;
       $currShop['coffeeScore'] = $coffeeScore;
       $currShop['wifiScore'] = $wifiScore;
       $currShop['img'] = $img;
       array_push($coffeeShops, $currShop);
      }
      //everything is good to go
      echo json_encode($coffeeShops);
  } else {
    //query did not execute
  }
 ?>
