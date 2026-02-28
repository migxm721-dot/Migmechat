<?php

$pipe = $argc < 2;

$content  = $initial_content = file_get_contents($pipe ? 'php://stdin' :  $argv[1]);

// fix all <?php opening
$content = preg_replace('/<\?(?!=|php|xml)[ \t]*/i', '<?php ', $content);

// delete trailing whitespace after <?php open
$content = preg_replace('/<\?php[ \t]+([\r\n])/i', '<?php${1}', $content);

// fix case
$content = preg_replace('/<\?PHP/', '<?php', $content);

// unifomalize php closing tags too
$content = preg_replace('/([:;}{])[ \t]*\?>/', '${1} ?>', $content);

// fix random obvious syntax eye-sores
$content = preg_replace('/\}else\{/', '} else {', $content);
$content = preg_replace('/\}\(/', '} (', $content);

// delete trailing whitespace everywhere!
$content = preg_replace('/[ \t]+([\r\n])/', '${1}', $content);

if ($content && $content != $initial_content)
{
    file_put_contents($pipe ? 'php://stdout' : $argv[1], $content);
}
elseif ($pipe)
{
    echo $initial_content;
}

