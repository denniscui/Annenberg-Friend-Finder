<?php 

require_once ('db_aff.php');
require_once("JSON.php");
require_once ('hash.php');
$xml=array();
if(!$db->open()){
   	$xml['status']="We are encountering temporary technical difficulties. Please try again later";
	$json = new Services_JSON();
	echo $json->encode($xml);
	exit();
}
	$huid= intval($_POST['huid']);
	$passwd = $_POST['passwd'];
	$u_name  = $_POST['name'];
	
	if ( empty($passwd) || empty($u_name) || empty($huid))
	{
		$xml['status']="Please enter all the fields.";
	}
	else if (id_exists($db, $huid))
	{
		echo("!id_exists");
		$xml['status']="The HUID is already in use.";
	} else {
		$name_to_enter = mysql_real_escape_string ($u_name);
		$passwd = mysql_real_escape_string ($passwd);
		if (empty($valid)) $valid = '';
		
		$sql = "INSERT INTO users(huid, password,  name)";
		$sql .= " VALUES ('$huid', '$passwd', '$name_to_enter')" ;
		if ( !$db->sql_query($sql) )
		{
			$xml['status']="We are encountering temporary technical difficulties. Please try again later";
		} else {
			
		$bound = 99999999;
		$hash_code = (int) sax_hash ( $uid, $bound );
		if ( $hash_code < 10000000) 
			$hash_code  += 10000000;
		
		$bound = 99999999;
		$hash_code = (int) oat_hash ( $uid, $bound );
		if ( $hash_code < 10000000) 
			$hash_code  += 10000000;
		$xml['status']="OK";
		
		$xml['h'] = $hash_code;
		$xml['huid'] = $huid;
		$xml['n'] = $u_name;
		}
	}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();

function id_exists($db, $user_id)
{
	$id_exist = false;
	$sql ="select * from users where huid = '$user_id'";
	$result = $db->sql_query($sql);    
	if ( !$result)    
	{    
		$xml['status']="We are encountering temperory technical difficulties. Please try again later";
	}    
	if( $raw = $db->sql_fetchrow($result)) 
	{
		$id_exist = true;
	}
	return $id_exist;
}
?>
