<html>
<head>
</head>
<body>

<?php

date_default_timezone_set("America/New_York");

function checkTime($evtTime){  
    if(time() < strtotime($evtTime))
        return "No";
    else
        return "YES";  
}

$limit = 10;
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

echo "<table border=1><tr><th>eventId</th>".
                        "<th>eventDate</th>".
                        "<th>eventTime</th>".
                        "<th>venueId</th>".
                        "<th>timeZone</th>".
                        "<th> Expired </th></tr>";
for($i=0;$i<$json->count;$i++){

    if($json->events[$i]->offers[0]->enabled === true && 
        $json->events[$i]->timeZone == "America/New_York")
    {     

        echo "<tr><td>".$json->events[$i]->eventId."</td>".
        "<td>".$json->events[$i]->eventDate."</td>".
        "<td>".$json->events[$i]->eventTime."</td>".
        "<td>".$json->events[$i]->venueId."</td>".       
        "<td>".$json->events[$i]->timeZone."</td>".
        "<td>".checkTime($json->events[$i]->eventDate." ".$json->events[$i]->eventTime)."</td></tr>";
    }

}
echo "</table>";

?>

<div id='vettix'><?php echo $json->count; ?></div>
</body>
</html>





