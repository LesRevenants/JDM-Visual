<?php

//error_reporting(E_ALL); ini_set('display_errors', 1);
session_start();

	if($_POST['output'] == "on") $_POST['output'] = "true";
	else { $_POST['output'] = "false"; }
	if($_POST['input'] == "on") $_POST['input'] = "true";
	else { $_POST['input'] = "false"; }
	$content = "{";
		$content .= "".'"motx":"';
		$content .= $_POST["motx"] . '",';

		if(strpos($_POST["predicat"], ',') !== false) {
			$content .= ""."\"predicates\":[";
			$predicat = explode(',',$_POST["predicat"]);
			foreach($predicat as $a) {
				$content .= "\"".$a."\",";
			}			
			$content = substr($content, 0, -1);
			$content .= "],";
		}
		else {
		$content .= ""."\"predicates\":";
		$content .= "[\"" . $_POST["predicat"] . '"],'; }
		
			if(strpos($_POST["moty"], ',') !== false) {
			$content .= ""."\"terms\":[";
			$predicat = explode(',',$_POST["moty"]);
			foreach($predicat as $a) {
				$content .= "\"".$a."\",";
			}			
			$content = substr($content, 0, -1);
			$content .= "],"; }
		else {	
		$content .= ""."\"terms\":";
		$content .= "[\"" . $_POST['moty'] . '"],';
		}
		$content .= ""."\"in\":\"";
		$content .= $_POST['input'] . '",';
		$content .= ""."\"out\":\"";
		$content .= $_POST['output'] . '",';
		$content .= ""."\"format\":\"";
		$content .= "grouped" . '"'. "}";


	try {
	 $out = phpClient($content);   //exec("java -jar ready.jar {$arg}", $output);
	} catch (Exception $e) { echo "yolo"; }
?>

<!doctype html>
	<html class="no-js" lang="en">
  		<head>
		    <meta charset="utf-8" />
		    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
		    <title>JDMVisual</title>
    
		    <!--    Stylesheet Files    -->
		    <link rel="stylesheet" href="css/normalize.css" />
		    <link rel="stylesheet" href="css/foundation.min.css" />
		    <link rel="stylesheet" href="css/main.css" />
		    <script src="js/Chart.bundle.js"></script>
		    <script src="js/Charts.js"></script>
		    <link rel="stylesheet" href="completion/awesomplete.css" /> <script src="completion/awesomplete.js" async></script>
		    <style>
			#main { width: 100%; margin: 0 auto; display: inline-block;}
			#outer { width: 50%; height: auto; display: inline-block; margin: 0 auto;}
			
     		    </style>
		    <style>
			canvas {
				-moz-user-select: none;
				-webkit-user-select: none;
				-ms-user-select: none;
			}
		    </style>

   		 <!--    Javascript files are placed before </body>    -->
  		</head>
  
		  <body>
		    <!--  Start Hero Section  -->
		    <section class="heros">
		      <header>
			<div class="row">
			  

			  <nav class="top-bar" data-topbar role="navigation">
			    
			    <!--    Start Logo    -->
			    <ul class="title-area">
			      <li class="name">
				<a href="#" class="logo">
				  <h1>JEUXDEMOTS<span class="tld">.org</span></h1>
				</a>
			      </li>
				<span class="toggle-topbar menu-icon"><a href="#"><span>Menu</span></a></span>
			      </li>
			    </ul>  
			    <!--    End Logo    -->

			    <!--    Start Navigation Menu    -->
			    <section class="top-bar-section" id="mean_nav">
			      <ul class="right">
				<li><a href="#connect">A propos</a></li>
			      </ul>
			    </section>

			    <!--    End Navigation Menu    -->

			  </nav>
			</div>
		      </header>
		</section>
		<?php
		$d = str_replace('{','',$out);
		$d = str_replace('}','',$d);
		$d = str_replace('\"','',$d);
		$d = explode('"r_', $d);
		//$d = explode(':', $d);
		$predicate = $d[0];
		$predicate = str_replace('"','',$predicate);
		$a = explode("]",$d[1]);
		$array = array();
		$arrouille = array();
		$cpt;
		foreach($a as $b) {
			$b = str_replace('[','',$b);
			}
			$a[0] = str_replace('[','',$a[0]);
			$a = explode(',',$a[0]);
			$a = str_replace('"','',$a);
			$h = array_shift($d);
			echo"<br>";
			$p = 1;
			foreach($d as $c) {
				$c = str_replace('":',',',$c);
				$c = explode(",[", $c);
				$c = str_replace('[','',$c);
				$c = str_replace(']','',$c);


				echo'  

				<div class="one">
				<table class="table" style="margin : auto; overflow-y:scroll; max-height: 50%; width: 500px; margin-top:3%; margin-bottom:3%;">
				  <thead class="thead-dark">
				    <tr>
				      <th scope="col">#</th>
				      <th scope="col">Mot X</th>
				      <th scope="col">Mot Y</th>
				      <th scope="col">Poids</th>
				    </tr>
				  </thead>



				<tbody>



				';

				$i = 1;
				//$m = array_shift($c);
				foreach($c as $e){

					$e = explode(",", $e);
					$e = str_replace('"','',$e);

					if($i == 1) {
						echo"
						<center><h2 style='display:inline;'>r_".$e[0]."</h2></center>"; $arrouille[$cpt] = $e[0];}
						else {
						echo"
						 <tr>
						      <th scope='row'>".($i-1)."</th>
						      <td>".$e[0]."</td>
						      <td>".$e[1]."</td>
						      <td>".$e[2]."</td>
						    </tr>

						"; 
					} 
					$i++;  
				} 
				$array[$cpt] = $i-2; 
				$cpt++;
			}
			?>

      			 </tbody>
		</table>
	</div>

	<div class="two">


		<div style="width: 75%">
			<canvas id="canvas"></canvas>
		</div>

	<script>
		var ctx = document.getElementById("canvas");
		var tablee = <?php echo js_array($array); ?>;
		var tablii = <?php echo js_array($arrouille); ?>;
		var myChart = new Chart(ctx, {
		    type: "bar",
		    data: {
			labels: tablii,
			datasets: [{
			    label: "# of Votes",
			    data: tablee,
			    backgroundColor: [
				"rgba(255, 99, 132, 0.2)",
				"rgba(54, 162, 235, 0.2)",
				"rgba(255, 206, 86, 0.2)",
				"rgba(75, 192, 192, 0.2)",
				"rgba(153, 102, 255, 0.2)",
				"rgba(255, 159, 64, 0.2)"
			    ],
			    borderColor: [
				"rgba(255,99,132,1)",
				"rgba(54, 162, 235, 1)",
				"rgba(255, 206, 86, 1)",
				"rgba(75, 192, 192, 1)",
				"rgba(153, 102, 255, 1)",
				"rgba(255, 159, 64, 1)"
			    ],
			    borderWidth: 1
			}]
		    },
		    options: {
			scales: {
			    yAxes: [{
				ticks: {
				    beginAtZero:true
				}
			    }]
			}
		    }
		});
	</script>

	</div>
 	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> 

    <!--  Start Footer Section  -->
	    <footer>
	      <div class="row">
		
		<!--    Start Copyrights    -->
		<div class="small-12 medium-4 large-4 columns">
		  <div class="copyrights">
		    <a class="logo" href="#">
		      <h1>jeuxdemots<span class="tld">.org</span></h1>
		    </a>
		    <p>Copyright © 2018 Renaud Colin & Florian Pascual Corp.</p>
		  </div>
		</div>
		<!--    End Copyrights    -->


		<div class="small-12 medium-8 large-8 columns">
		  <div class="contact_details right">
		    <nav class="social">
		      <ul class="no-bullet">
		        <li><a href="http://facebook.com/florianpascualsoawesome" target="_blank">Facebook</a></li>
		        <li><a href="http://instagram.com/florian_pascual" target="_blank">Instagram</a></li>
		      </ul>
		    </nav>

		    <div class="contact">
		      <div class="details">
		        <p>contact@jeuxdemots.org</p>
		        <p>06 46 20 37 12</p>
		      </div>

		      <p class="adress">161 Rue Ada
		       34095 Montpellier</p>
		    </div>
		  </div>
		</div>

	      </div>
	    </footer>
    <!--  End Footer Section  -->

	    <!--    Start Back To Top    -->
	    <a href="#" class="btn_fancy" id="back_top">
	      <div class="solid_layer"></div>
	      <div class="border_layer"></div>
	      <div class="text_layer"><img src="img/top_arrow.png" alt="Back to top" title="" class="top_arrow"></div>
	    </a>
    <!--    End Back To Top    -->

    <!--    Javascript Files    -->

	    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
	    <script type="text/javascript" src="js/jquery.js"></script>
	    <script type="text/javascript" src="js/touchSwipe.min.js"></script>
	    <script type="text/javascript" src="js/easing.js"></script>
	    <script type="text/javascript" src="js/foundation.min.js"></script>
	    <script type="text/javascript" src="js/foundation/foundation.topbar.js"></script>
	    <script type="text/javascript" src="js/carouFredSel.js"></script>
	    <script type="text/javascript" src="js/scrollTo.js"></script>
	    <script type="text/javascript" src="js/map.js"></script>
	    <script type="text/javascript" src="js/main.js"></script>
 	 </body>
</html>';

	<?php

	function phpClient($arg) {
	 $PORT = 9515; //the port on which we are connecting to the "remote" machine
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

	function js_str($s)
	{
	    return '"' . addcslashes($s, "\0..\37\"\\") . '"';
	}

	function js_array($array)
	{
	    $temp = array_map('js_str', $array);
	    return '[' . implode(',', $temp) . ']';
	}

	
?>
