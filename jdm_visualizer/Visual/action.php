<?php

session_start();
$content = "\n\t[\n\t\t";
	$content .= "\n\t\t\t".'"motx":"';
	$content .= $_POST["motx"] . '",';
	$content .= "\n\t\t\t"."\"predicat\":\"";
	$content .= $_POST["predicat"] . '",';
	$content .= "\n\t\t\t"."\"moty\":\"";
	$content .= $_POST['moty'] . '",';
	$content .= "\n\t\t\t"."\"input\":\"";
	$content .= $_POST['input'] . '",';
	$content .= "\n\t\t\t"."\"output\":\"";
	$content .= $_POST['output'] . '"'. "\n\n\t]";
$json = fopen('./json/query.json', 'w+');
$js = fopen('json/query.json', 'c+');
fwrite($json, $content);
fwrite($js, $content);
fclose($json);
fclose($js);
try {
 $out = phpClient($arg);   //exec("java -jar ready.jar {$arg}", $output);
} catch (Exception $e) { echo "yolo"; }
$outpute = explode("@", $out);
$output = explode("\n", $outpute[0]);
$other = $outpute[1];

function phpClient($arg) {
 $PORT = 8000; //the port on which we are connecting to the "remote" machine
 $HOST = "localhost"; //the ip of the remote machine (in this case it's the same machine)
 
 $sock = socket_create(AF_INET, SOCK_STREAM, 0) //Creating a TCP socket
         or die("error: could not create socket\n");
 
 $succ = socket_connect($sock, $HOST, $PORT) //Connecting to to server using that socket
         or die("error: could not connect to host\n");
 
$text = $arg; //the text we want to send to the server
socket_write($sock, $text . "\n", strlen($text) + 1) //Writing the text to the socket
       or die("error: failed to write to socket\n");
$reply = socket_read($sock, 100000) //Reading the reply from socket
        or die("error: failed to read from socket\n");
  return $reply; }

?>
