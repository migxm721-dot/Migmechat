<?php
/**
 * Created by PhpStorm.
 * User: timothee
 * Date: Jun 22, 2011
 * Time: 12:46:38 PM
 * To change this template use File | Settings | File Templates.
 */

fast_require("DAO", get_dao_directory() . "/dao.php");
fast_require('XCache', get_framework_common_directory() . '/xcache.php');

class VasDAO extends DAO
{
	const XCACHE_TTL_IN_SECONDS = 600; // 10mn

	public function get_menu($vas_user_agent, $menu_type=0)
	{
		return XCache::getInstance()->get
		(
			XCache::KEYSPACE_VAS . $vas_user_agent . '/' . XCache::KEYSPACE_VAS_MENU . $menu_type
			, array(&$this, '_get_menu_from_source')
			, self::XCACHE_TTL_IN_SECONDS
			, array('callback_args' => array($vas_user_agent, $menu_type))
		);
	}

	public function _get_menu_from_source($vas_user_agent, $menu_type=0)
	{
		try
		{
			$query = "
				SELECT me.*, pme.Position, p.MainMenuVersion
				FROM
					  menuentry me
					, partnerbuildmenuentry pme
					, partnerbuild p

				WHERE
					    p.UserAgent = ?
					AND p.Status = 1
					AND p.id = pme.BuildID
					AND me.ID = pme.EntryID
					AND pme.MenuType = ?

				ORDER BY pme.Position ASC
			";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("si", $vas_user_agent, $menu_type);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$entries = array();

			while($stmt->fetch())
			{
				$entries[] = array
				(
					  'id'         => $row['ID']
					, 'label'      => $row['Label']
					, 'icon_url'   => $row['IconURL']
					, 'action_url' => $row['ActionURL']
					, 'position'   => $row['Position']
				);
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $entries;
		}
		catch (Exception $e)
		{
			error_log("unable to fetch partnerbuild menu entries from database: [$vas_user_agent], [$menu_type]");
		}

		return null;
	}

	public function get_partner_build($vas_user_agent)
	{
		return XCache::getInstance()->get
		(
			XCache::KEYSPACE_VAS . $vas_user_agent
			, array(&$this, '_get_partner_build_from_source')
			, self::XCACHE_TTL_IN_SECONDS
			, array('callback_args' => array($vas_user_agent))
		);
	}

	public function _get_partner_build_from_source($vas_user_agent)
	{
		try
		{
			$query = "
				SELECT p.*, of.Name as OptionName, o.Value as OptionValue
				FROM
					  partnerbuild p LEFT OUTER JOIN
					  	(partnerbuildoption o INNER JOIN partnerbuildoptiondefinition of ON of.ID = o.OptionID)
					  	ON p.id = o.BuildID

				WHERE
					    p.UserAgent = ?
					AND p.Status = 1
			";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
			$stmt->bind_param("s", $vas_user_agent);
			$stmt->execute();
			$this->getSlaveConnection()->stmt_bind_assoc($stmt, $row);

			$build = null;

			while($stmt->fetch())
			{
				if (null === $build)
				{
					$build = array
					(
						  'ID'              => $row['id']
						, 'PartnerID'       => $row['PartnerID']
						, 'UserAgent'       => $row['UserAgent']
						, 'DateCreated'     => $row['DateCreated']
						, 'DownloadURL'     => $row['DownloadURL']
						, 'SmsMsg'          => $row['SmsMsg']
						, 'Platform'        => $row['Platform']
						, 'Version'         => $row['Version']
						, 'Status'          => $row['Status']
						, 'MainMenuVersion' => $row['MainMenuVersion']
						, 'Options'         => array()
					);
				}

				$build['Options'][$row['OptionName']] = $row['OptionValue'];
			}

			$stmt->close();
			$this->closeSlaveConnection();

			return $build;
		}
		catch (Exception $e)
		{
			error_log("unable to fetch partnerbuild from database: [$vas_user_agent]");
		}

		return null;
	}
}
