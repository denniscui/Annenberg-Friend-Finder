<?php 

require_once ('db_aff.php');
require_once("JSON.php");
$xml=array();
$friendarray =array();
if(!$db->open()){
   	$xml['status']="We are encountering temporary technical difficulties. Please try again later";
	$json = new Services_JSON();
	echo $json->encode($xml);
	exit();
}
	
	$user  = intval($_POST['huid']);
		
	if ( empty($user))
	{
		$xml['status']="Missing information.";
	} else {
		$sql = "SELECT users.huid as huid, users.name as name, users.table as tableNum, users.image as imageUri, users.status as state, users.time as time from friends, users WHERE friends.user = $user and mutual = 1 and users.huid = friends.friend";
		$result=$db->sql_query($sql);
		if($result)
		{
			while($raw = mysql_fetch_assoc($result))
				$friendarray[] = $raw;
				
			$xml['status'] = "OK";
			$xml['list'] = $friendarray;
		}
		else
			$xml['status'] = "fetchFriends failed";
	}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
