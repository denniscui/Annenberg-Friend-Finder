<?php 

require_once ('db_aff.php');
require_once("JSON.php");
$xml=array();
if(!$db->open()){
   	$xml['status']="We are encountering temperory technical difficulties. Please try again later";
	$json = new Services_JSON();
	echo $json->encode($xml);
	exit();
}
	
	$user  = $_POST['huid'];
	$friend  = $_POST['f_huid'];
	$add = $_POST['add'];
	
	if ( empty($user) || empty($friend))
	{
		$xml['status']="Missing information.";
	} else {
		
		if($add == 'Y')
		{
			$addsql = "INSERT into friends (user, friend, mutual)";
			$addsql .= " VALUES ('$user', '$friend', '1') ON DUPLICATE KEY UPDATE mutual = 1";
			$addresult = $db->sql_query($addsql);
			
			$updatesql = "UPDATE friends set mutual = '1' where user = '$friend' and friend = '$user'";
			$updateresult = $db->sql_query($updatesql);
			
			if($updateresult)
			{
				$xml['status'] = "ADD";
			}
			else
			{
				$xml['status'] = "We are currently experiencing technical difficulties. Please try again later.";
			}
		}
		else
		{
			$removesql = "DELETE from friends where user = '$friend' and friend = '$user'";
			$removeresult = $db->sql_query($removesql);
			$removesql = "DELETE from friends where user = '$user' and friend = '$friend'";
			$removeresult = $db->sql_query($removesql);
			
			if($removeresult)
			{
				$xml['status'] = "REMOVE";
			}
			else
			{
				$xml['status'] = "We are currently experiencing technical difficulties. Please try again later.";
			}
		}	
	}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
