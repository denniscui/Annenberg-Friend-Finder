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
	
	if ( empty($user) || empty($friend))
	{
		$xml['status']="Missing information.";
	} else {

		$sql = "SELECT * from friends ";
		$sql .=   " WHERE user = '$friend' and friend = '$user' " ;
		$result = $db->sql_query($sql);
		
		$alreadysql = "SELECT * from friends ";
		$alreadysql .=   " WHERE user = '$user' and friend = '$friend' " ;	
		$alreadyresult = $db->sql_query($alreadysql);
		if($alreadyresult && $result)
		{
		if(mysql_num_rows($result) == 0 && mysql_num_rows($alreadyresult) == 0)
		{
			$addsql = "INSERT into friends (user, friend, mutual)";
			$addsql .= " VALUES ('$user', '$friend', '0')";
			$addresult = $db->sql_query($addsql);
		}
		else if(mysql_num_rows($result) != 0 && mysql_num_rows($alreadyresult) == 0)
		{
			$addsql = "INSERT into friends (user, friend, mutual)";
			$addsql .= " VALUES ('$user', '$friend', '1')";
			$addresult = $db->sql_query($addsql);
			
			$updatesql = "UPDATE friends set mutual = '1' where user = '$friend' and friend = '$user'";
			$updateresult = $db->sql_query($updatesql);
		}
		
		$xml['status'] = "OK";
		}
		else
		{
			$xml['status'] = "We are encountering temporary difficulties. Please try again later.";
		}
	}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
