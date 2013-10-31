<?php 

require_once ('db_aff.php');
require_once("JSON.php");
require_once ('hash.php');
$xml=array();
if(!$db->open()){
   	$xml['status']="We are encountering temperory technical difficulties. Please try again later";
	$json = new Services_JSON();
	echo $json->encode($xml);
	exit();
}
	
	$huid= intval($_POST['huid']);
	$passwd = $_POST['passwd'];
	
	$id_exist = false;
	$sql = "select * from users where huid = '$huid' and password='$passwd'";
	$result = $db->sql_query($sql);    
	if ( !$result)    
	{    
   		$xml['status']="We are encountering temperory technical difficulties. Please try again later";
	}    
	if( $raw = $db->sql_fetchrow($result)) 
	{
		$id_exist = true; 
			
		$bound = 99999999;
		$hash_code = (int) sax_hash ( $huid, $bound );
		if ( $hash_code < 10000000) 
			$hash_code  += 10000000;
		
		$bound = 99999999;
		$hash_code = (int) oat_hash ( $huid, $bound );
		if ( $hash_code < 10000000) 
			$hash_code  += 10000000;
		$xml['status']="OK";
		
		$xml['h'] = $hash_code;
		$xml['huid'] = $huid;
		$xml['n'] = $raw['name'];
	} else {
		$xml['status']="Invalid User";
	}
		
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
