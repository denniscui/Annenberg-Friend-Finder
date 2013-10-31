<?php 

require_once ('db_aff.php');
require_once("JSON.php");
$xml=array();
$allarray =array();
if(!$db->open()){
   	$xml['status']="We are encountering temporary technical difficulties. Please try again later";
	$json = new Services_JSON();
	echo $json->encode($xml);
	exit();
}
		$token = $_POST['token'];
		if(empty($token))
		{
			$xml['status']="Missing information.";
		}
		else{
		$sql = "SELECT time as time, status as state, huid as huid, name as name, users.table as tableNum, image as imageUri from users ";
			$sql .=   " WHERE isEating = 'Y' " ;

//error_log($sql);
		$result = $db->sql_query($sql);  
		if ( $result) {   
			while( $raw = mysql_fetch_assoc($result))
				$allarray[] = $raw;
				
			//$statesql = "SELECT state as state from states ";
//			$statesql .= " WHERE state_id = $cardarray[state] " ;
//			$tablesql = "SELECT tables.table as tableNum from tables ";
//			$tablesql .= " WHERE table_id = $cardarray[tableNum] " ;
//			
//			$stateresult = $db->sql_query($statesql);
//			if($stateresult)
//				while($raw = mysql_fetch_assoc($stateresult))
//					$cardarray['state'] = $raw;
//					
//			$tableresult = $db->sql_query($tablesql);
//			if($tableresult)
//				while($raw = mysql_fetch_assoc($tableresult))
//					$cardarray['tableNum'] = $raw;
			$xml['status']="OK";
			$xml['list']=$allarray;
		} else {
			$xml['status']="We are encountering temporary technical difficulties. Please try again later";
		}
		}
	$json = new Services_JSON();
	
	echo $json->encode($xml);
	exit();
?>
