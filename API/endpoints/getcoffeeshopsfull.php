<?php
    // set up a database connection
    require_once '../includes/DbConnect.php';
    $db = new DbConnect();
    $con = $db->connect();

    //set up response array object that will send info back to application
    $response = array();

    //check to make sure email and password parameters were posted
    if(isset($_POST['coffeeID'])) {
      $id = $_POST['coffeeID'];
      $stmt = $con -> prepare("SELECT address, city, state, phone, popular_item, website,
        item, price, amenity, atmosphere FROM coffeeshops
        LEFT JOIN menus ON coffeeshops.coffeeID = menus.coffeeID
        RIGHT JOIN coffee_amenities ON coffee_amenities.coffeeID = menus.coffeeID
        RIGHT JOIN coffee_atmosphere ON coffee_amenities.coffeeID = coffee_atmosphere.coffeeID
        WHERE coffeeshops.coffeeID = ?");
      $stmt -> bind_param("i",$id);
      $stmt -> execute();
      $stmt->store_result();
      if($stmt->num_rows > 0) {
        $stmt->bind_result($address, $city, $state, $phone, $popular_item, $website,
            $item, $price, $amenity, $atmosphere);

        // these arrays will store all the data
        $currShop = array();
        $menu = array();
        $atmospheres = array();
        $amenities = array();

        while($stmt->fetch()) {
          //add information from coffeeshops table to object
          $currShop['address'] = $address;
          $currShop['city'] = $city;
          $currShop['state'] = $state;
          $currShop['phone'] = $phone;
          $currShop['popularItem'] = $popular_item;
          $currShop['website'] = $website;
          //push unique atmosphere, amenities, and menu items to their appropriate arrays
          if(!in_array($atmosphere, $atmospheres)) {
            array_push($atmospheres, $atmosphere);
          }
          if(!in_array($amenity, $amenities)) {
            array_push($amenities, $amenity);
          }
          $menuEntry = array();
          $menuEntry['item'] = $item;
          $menuEntry['price'] = $price;
          if(!in_array($menuEntry, $menu)) {
            array_push($menu, $menuEntry);
          }
        }
        $response['error'] = false;
        $response['shop'] = $currShop;
        //add the other table arrays to the shop object
        $response['atmospheres'] = $atmospheres;
        $response['amenities'] = $amenities;
        $response['menu'] = $menu;
      } else {
        $response['error'] = true;
        $response['message'] = "No results found";
      }
    } else {
      $reponse['error'] = true;
      $response['message'] = "No ID parameter given";
    }

    //display response on screen as JSON array
    echo json_encode($response);
 ?>
