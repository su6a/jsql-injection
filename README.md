jSQL Injection
==============

[Screenshot](https://sites.google.com/site/jsqlinjection/_/rsrc/1350152856008/home/images/screenshot.png)

An easy to use SQL injection tool for retrieving database informations from a distant server. 

You can discuss about jSQL Injection on the [discussion group](https://groups.google.com/d/forum/jsql-injection).

jSQL Injection features:
  * GET, POST, header, cookie methods
  * normal, error based, blind, time based algorithms
  * automatic best algorithms detection
  * data retrieving progression
  * proxy setting
  * evasion

For now supports MySQL.

Running injection requires the distant server url and the name of the parameter to inject.

If you know an injection should work but the jSQL tool doesn't access the database, you can inform me by email or use the discussion group.

For a local test, you can save the following PHP code in a script named for example simulate_get.php, and use the URL http://127.0.0.1/simulate_get.php?lib= in the first field of the tool, then click Connect to access the database:
```php
<?php
    mysql_connect("localhost", "root", "");
    mysql_select_db("my_own_database");

    $result = mysql_query("SELECT * FROM my_own_table where my_own_field = {$_GET['lib']}") # time based
    or die( mysql_error() ); # error based

    if(mysql_num_rows($result)!==0) echo" true "; # blind

    while ($row = mysql_fetch_array($result, MYSQL_NUM))
        echo join(',',$row); # normal
?>
```