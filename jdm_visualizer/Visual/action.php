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
fclose($js)

?>
