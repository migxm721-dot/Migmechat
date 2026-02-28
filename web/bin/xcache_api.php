<?php

// we first check the validity of the request, only few accepted patterns
// * action=get&key=key
// * action=delete&key=key
// * action=delete&prefix=prefix
// * action=list
// *    with combination of: prefix=prefix&olderthan=seconds&notaccessed=1

$action = strtolower($_GET['action']);

if (!in_array($action, array('get', 'delete', 'list')))
{
    header("HTTP/1.0 400 Bad Request - Invalid Action");
    exit();
}

if ('get' == $action)
{
    if (isset($_GET['key']))
    {
        if (!is_array($_GET['key']))
        {
            sendResponse(xcache_get($_GET['key']));
        }

        $response = array();
        foreach($_GET['key'] as $key)
        {
           $response[] = xcache_get($key);
        }

        sendResponse($response);
    }
    else
    {
        header("HTTP/1.0 400 Bad Request - Invalid Action");
        exit();
    }
}
elseif ('delete' == $action)
{
    if (isset($_GET['key']))
    {
        if (!is_array($_GET['key']))
        {
            sendResponse(xcache_unset($_GET['key']));
        }

        $response = array();
        foreach($_GET['key'] as $key)
        {
           $response[] = xcache_unset($key);
        }

        sendResponse($response);
    }
    elseif(isset($_GET['prefix']))
    {
        if (function_exists('xcache_unset_by_prefix'))
        {
            sendResponse(xcache_unset_by_prefix($_GET['prefix']));
        }
        else
        {
            $list = getKeyList(array
            (
                'prefix' => $_GET['prefix']
            ));

            $success = true;

            foreach($list as $entry)
            {
                if (! ($success = $success && xcache_unset($entry['name'])))
                {
                    break;
                }
            }

            sendResponse($success);
        }
    }
    else
    {
        header("HTTP/1.0 400 Bad Request - Invalid Action");
        exit();
    }
}
else if ('list' == $action)
{
   sendResponse(getKeyList($_GET));
}


function sendResponse($data)
{
    header('Content-type: application/json');
    echo json_encode($data);
    exit();
}

function getKeyList($query = array())
{
    // TODO: sanitize query parameters first

    $full_list = array();

    $now = time();
    $timestamp_created = isset($query['olderthan']) ? $now - (int)$query['olderthan'] : $now;

    foreach(range(0, xcache_count(XC_TYPE_VAR)-1) as $idx)
    {
        $list = xcache_list(XC_TYPE_VAR, $idx);

        foreach($list['cache_list'] as $entry)
        {
            if (isset($query['prefix']) && 0 !== strpos($entry['name'], $query['prefix'])) continue;
            if ($entry['ctime'] > $timestamp_created) continue;
            if (isset($query['notaccessed']))
            {
                if ((boolean) $query['notaccessed'])
                {
                    if ($entry['hits'] > 0) continue;
                }
                else
                {
                    if ($entry['hits'] <= 0) continue;
                }
            }

            $full_list[] = $entry;
        }
    }

    // sort by name
    usort($full_list, 'sort_by_name');

    return $full_list;
}

function sort_by_name($a, $b)
{
    $score = strcasecmp($a['name'], $b['name']);
    return $score === 0 ? 0 : ($score > 0 ? 1 : -1);
}

