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
	
	$huid = $_POST['huid'];
	$isEating = $_POST['eatStatus'];
	$state = $_POST['state'];
	
	if(empty($huid) || empty($isEating) || empty($state))
	{
		$xml['status']="Missing Information.";
	}
	else
	{
	$updatesql = "UPDATE users set ";
	if($isEating == 'Y')
	{
		$updatesql .= " status='$state', isEating='$isEating', time = now() where huid = '$huid' ";
	}
	else
	{
		$updatesql .= " users.table = 0, status='$state', isEating='$isEating', time = now() where huid = '$huid' ";
	}
	$updateresult = $db->sql_query($updatesql);
		
		if ( !$updateresult)    
		{    
   			$xml['status']="We are encountering temperory technical difficulties. Please try again later"			;
		}
		else
		{
			$xml['status'] = "OK";
		}
	}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
