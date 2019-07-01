<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,height=device-height, initial-scale=1">
<meta name="apple-mobile-web-app-capable" content="yes">
<style>
table, th , td {
    border: 1px solid grey;
    border-collapse: collapse;
    padding: 5px;
}

table tr:nth-child(odd) {
    background-color: #f1f1f1;
}

table tr:nth-child(even) {
    background-color: #ffffff;
}
</style>
</head>
<body>
<div id='vettix'></div>

<?php

date_default_timezone_set("America/New_York");

function get_timezone_offset($remote_tz, $origin_tz = null) {
    if($origin_tz === null) {
        if(!is_string($origin_tz = date_default_timezone_get())) {
            return false; // A UTC timestamp was returned -- bail out!
        }
    }
    $origin_dtz = new DateTimeZone($origin_tz);
    $remote_dtz = new DateTimeZone($remote_tz);
    $origin_dt = new DateTime("now", $origin_dtz);
    $remote_dt = new DateTime("now", $remote_dtz);
    $offset = $origin_dtz->getOffset($origin_dt) - $remote_dtz->getOffset($remote_dt);
    return $offset;
}

function add_hours_to_timestamp($offset,$timestamp){
    return strtotime($timestamp)+ ($offset*3600);
}


$limit = 100;
if(isset($_GET['limit']) & !empty($_GET['limit']))
{	
    $limit = $_GET['limit'];
}

$url = "https://www.vettix.org/sandbox/api/tm-events.php";
$hdr = "HTTP_CUSTOM_VETTIX: aoekjr02%weragwkL51";

$context = stream_context_create(array(
    'http'=>array(
        'method'=> "GET",
        'header'=> $hdr
    )
));
$result = file_get_contents($url."?limit=".$limit, false, $context);

$json = json_decode($result);//turn string to json object
$cnt = 1;

echo "<table><tr>".
        "<th>Event Id</th>".
        "<th>Venue Id</th>".
        "<th>Event Local Time</th>".
        "<th>Eastern Standard Time</th></tr>";

for($i=0;$i<$json->count;$i++){

    $evtTime = $json->events[$i]->eventDate." ".$json->events[$i]->eventTime;
    $evtTZ   = $json->events[$i]->timeZone;    

    if($json->events[$i]->offers[0]->enabled === true 
        && time() <= strtotime($evtTime))
    {

        $offset = get_timezone_offset($evtTZ,'America/New_York')/3600;
        $EST = add_hours_to_timestamp($offset,$evtTime);
        $cnt++;

        echo "<tr><td>".$json->events[$i]->eventId."</td>".
        "<td>".$json->events[$i]->venueId."</td>".       
        "<td>".$evtTime."</td>".       
        "<td>".date("Y-m-d H:i:s", $EST)."</td></tr>";
    }
    //echo "<script> document.getElementById('vettix').innerHTML = '<b>".($cnt-1)." Event(s) Found.</b><br>'; </script>";  
}
echo "</table>";

?>
</body>
</html>





