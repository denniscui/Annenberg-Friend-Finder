<?php 

require_once ('db_aff.php');
require_once("JSON.php");
$xml=array();
$info=array();
if(!$db->open()){
   	$xml['status']="We are encountering temperory technical difficulties. Please try again later";
	$json = new Services_JSON();
	echo $json->encode($xml);
	exit();
}
	
	$huid = $_POST['huid'];
	
	if(empty($huid))
	{
		$xml['status']="Missing Information.";
	}
	else
	{
	
	$sql = "select status as statusNum, time as time, users.table as tableNum, users.image as imgUri from users where huid = '$huid' ";
	$result = $db->sql_query($sql);
		
		if ( !$result)    
		{    
   			$xml['status']="We are encountering temperory technical difficulties. Please try again later"			;
		}
		else
		{
			while($raw = mysql_fetch_assoc($result))
				$info[] = $raw;
			$xml['status'] = "OK";
			$xml['list'] = $info;
		}
	}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
