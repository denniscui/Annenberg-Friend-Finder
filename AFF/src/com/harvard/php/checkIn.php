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
	$table = $_POST['table'];
	
	if(empty($huid) || empty($table))
	{
		$xml['status']="Missing Information.";
	}
	else
	{
	
	$updatesql = "UPDATE users set status = 3, users.table='$table', isEating='Y', time = now() where huid = '$huid' ";
	$updateresult = $db->sql_query($updatesql);
		
		if ( !$updateresult)    
		{    
   			$xml['status']="We are encountering temperory technical difficulties. Please try again later";
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
