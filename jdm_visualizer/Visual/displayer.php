<?php

error_reporting( E_ALL );
ini_set('display_errors',1);

session_start();
print_r($_GET['param']);
echo('<script>    
    if(typeof window.history.pushState == 'function') {
        window.history.pushState({}, "Hide", "http://localhost/join/prog/ex.php");
    }
</script>');
?>
