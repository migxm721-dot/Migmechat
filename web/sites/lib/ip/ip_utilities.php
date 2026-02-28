<?php

class IPv4Utilities
{
	/**
	 * Returns the remote IP address of the user.
	 *
	 * Normally we would use "$_SERVER['REMOTE_ADDR']".
	 * However, the cookie-based load balancer places the remote IP at the end of
	 * the "X-Forwarded-For" HTTP header, so we have to get it from there.
	 *
	 * The "X-Forwarded-For" may contain multiple addresses including any load
	 * balancer IPs, separated by ", ".
	 * e.g.:
	 * X-Forwarded-For: 196.207.40.236, 196.207.40.212, 10.3.1.132, 10.3.2.145
	 * If this is the case, we want to use the last address on the line that is
	 * not the load balancer.
	 *
	 * @return string
	 */
	public static function getRemoteIPAddress()
	{
		$remoteIP = isset($_SERVER['REMOTE_ADDR']) ? $_SERVER['REMOTE_ADDR'] : '';

		if (empty($_SERVER['HTTP_X_FORWARDED_FOR']))
		{
			return $remoteIP;
		}
		// pick the last IP which does not start with 10.XXX.XXX.XXX
		else if (preg_match('/(\d{1,3}(\.\d{1,3}){3})(\s*,\s*10(\.\d{1,3}){3})*$/i', $_SERVER['HTTP_X_FORWARDED_FOR'], $addresses))
		{
			return $addresses[1];
		}
		else
		{
			// X-Forwarded-For found but the above regex fails
			// use error_log instead of Logger
			error_log('getRemoteIPAddress, no IP address found, HTTP_X_FORWARDED_FOR: ' . $_SERVER['HTTP_X_FORWARDED_FOR']);
			return $remoteIP;
		}
	}

	/**
	 * @param string $ip
	 * @param array $whitelist
	 * @return boolean
	 */
	public static function isIPinWhitelist($ip, $whitelist)
	{
		$ip = ip2long($ip);
		if ($ip == -1 || $ip === FALSE) return false;

		foreach($whitelist as $entry)
		{
			if (is_array($entry))
			{
				if (($ip >> $entry[1]) === $entry[0])
				{
					return true;
				}
			}
			else if ($ip === $entry)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Prepare whitelist
	 *
	 * @param array $whitelist
	 * @throws Exception
	 * @return array number
	 */
	public static function prepareWhitelist($whitelist)
	{
		$entries = array();

		foreach($whitelist as $item)
		{
			if (!preg_match('#^(\d{1,3}(?:\.\d{1,3}){3})(?:/(\d{1,2}))?$#', $item, $matches))
			{
				throw new Exception("invalid whitelist pattern detected: $item");
			}

			$ip = ip2long($matches[1]);
			if ($ip == -1 || $ip == false)
			{
				throw new Exception("invalid ip detected in item: $item");
			}

			if (!empty($matches[2]))
			{
				$mask = intval($matches[2]);
				if ($mask < 1 || $mask > 32)
				{
					throw new Exception("invalid subnet mask detected in item: $item");
				}

				$bits_to_shift = 32 - $mask;

				$entries[] = array($ip >> $bits_to_shift, $bits_to_shift);
			}
			else
			{
				$entries[] = $ip;
			}
		}

		return $entries;
	}

	/**
	 * get the first ip and last ip from cidr(network id and mask length)
	 * i will integrate this function into "Rong Framework" :)
	 * @author admin@wudimei.com
	 * @param string $cidr 56.15.0.6/16 , [network id]/[mask length]
	 * @return array $ipArray = array( 0 =>"first ip of the network", 1=>"last ip of the network" );
	 *                         Each element of $ipArray's type is long int,use long2ip( $ipArray[0] ) to convert it into ip string.
	 * example:
	 * list( $long_startIp , $long_endIp) = getIpRange( "56.15.0.6/16" );
	 * echo "start ip:" . long2ip( $long_startIp );
	 * echo "<br />";
	 * echo "end ip:" . long2ip( $long_endIp );
	 */
	public static function getIpRang($cidr)
	{
		list($ip, $mask) = explode('/', $cidr);

		$maskBinStr =str_repeat("1", $mask ) . str_repeat("0", 32-$mask );      //net mask binary string
		$inverseMaskBinStr = str_repeat("0", $mask ) . str_repeat("1",  32-$mask ); //inverse mask

		$ipLong = ip2long( $ip );
		$ipMaskLong = bindec( $maskBinStr );
		$inverseIpMaskLong = bindec( $inverseMaskBinStr );
		$netWork = $ipLong & $ipMaskLong;

		$start = $netWork+1;//去掉网络号 ,ignore network ID(eg: 192.168.1.0)

		$end = ($netWork | $inverseIpMaskLong) -1 ; //去掉广播地址 ignore brocast IP(eg: 192.168.1.255)
		return array( $start, $end );
	}

	/**
	 * netmask('192.168.6.255', 8)  will return 192.0.0.0
	 * netmask('192.168.6.255', 16) will return 192.168.0.0
	 * netmask('192.168.6.255', 24) will return 192.168.6.0
	 *
	 * @param string $ip
	 * @param integer $cidr
	 * @return string
	 */
	public static function netmask($ip, $cidr)
	{
		$bitmask = $cidr == 0 ? 0 : 0xffffffff << (32 - $cidr);
		return long2ip(ip2long($ip) & $bitmask);
	}
}