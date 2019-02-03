<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    $stmt = null;
    $response = array();

    //check which filter option has been selected
    /*
    if(isset($_POST['atmosphereFilter'])) {
      $desiredAtmosphere = $_POST['atmosphereFilter'];
      $stmt = $con -> prepare("SELECT coffeeshops.coffeeID, house_name, coffee_score, wifi_score, img, atmosphere
                                FROM coffeeshops
                                LEFT JOIN coffee_atmosphere ON coffee_atmosphere.coffeeID = coffeeshops.coffeeID
                                WHERE atmosphere = ?");
      $stmt -> bind_param("s",$desiredAtmosphere);
    }*/
    if(isset($_POST['amenityFilter'])) {
      $desiredAmenity = $_POST['amenityFilter'];
      $stmt = $con -> prepare("SELECT coffeeshops.coffeeID, house_name, coffee_score, wifi_score, img, amenity
                                FROM coffeeshops
                                LEFT JOIN coffee_amenities ON coffee_amenities.coffeeID = coffeeshops.coffeeID
                                WHERE amenity = ?");
      $stmt -> bind_param("s",$desiredAmenity);
      //return filtered coffeeshops
      $stmt->execute();
      $stmt->store_result();
      if($stmt->num_rows > 0) {
        //query was successful, return all of the objects
        $stmt->bind_result($coffeeID, $houseName, $coffeeScore, $wifiScore, $img,$amenity);

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
        $response['error'] = false;
        $response['shops'] = $coffeeShops;
      } else {
        //query did not execute
        $response['error'] = true;
        $response['message'] = "No matches found";
      }
    }
    /*
    elseif(isset($_POST['minWifi'])) {
      $minWifi = $_POST['minWifi'];
      $stmt = $con -> prepare("SELECT coffeeID, house_name, coffee_score, wifi_score, img
                                FROM coffeeshops WHERE wifi_score > ?");
      $stmt -> bind_param("d",$minWifi);
    }
    elseif(isset($_POST['minCoffee'])) {
      $minCoffee = $_POST['minCoffee'];
      $stmt = $con -> prepare("SELECT coffeeID, house_name, coffee_score, wifi_score, img
                                FROM coffeeshops WHERE coffee_score > ?");
      $stmt -> bind_param("d",$minCoffee);
    } else {
      //no parameters
    }*/


    echo json_encode($coffeeShops);
 ?>
