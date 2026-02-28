<?php
fast_require('DAO', get_dao_directory() . '/dao.php');
fast_require('IceDomain', get_domain_directory() . '/common/ice_domain.php');
fast_require('NotificationCount', get_domain_directory() . '/notification/count.php');

class NotificationDAO extends DAO
{
	private $user_notification_service;

	/**
	 * @throws com_projectgoth_fusion_slice_ObjectNotFoundException
	 * @throws Ice_LocalException
	 * @throws IceDAOException
	 * @return Ice_ObjectPrx
	 */
	public function __construct()
	{
		try
		{
			parent::__construct();
			$ice = new IceDomain('usernotificationservice');
			$this->user_notification_service = $ice->get_object_prx();
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/__construct: '.$ex->getMessage());
		}
	}

	public function get_notifications_count($user_id)
	{
		try
		{
			/*
			$user_notification_count = array();
			if(is_array($user_notifications) && sizeof($user_notifications) > 0)
			{
				foreach($user_notifications as $user_notification)
				{
					$user_notification_count[] = new NotificationCount($user_notification);
				}
			}
			return $user_notification_count;
			*/

			return $this->user_notification_service->getPendingNotificationsForUser($user_id);
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/get_notifications_count: '.$ex->getMessage());
		}

		return 0;
	}

	public function clear_all_notifications($user_id)
	{
		try
		{
			$this->user_notification_service->clearAllNotificationsForUser($user_id);
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/clear_all_notifications: '.$ex->getMessage());
		}

		return $this;
	}

	public function clear_all_notifications_by_type($user_id, $notification_type)
	{
		try
		{
			$this->user_notification_service->clearAllNotificationsByTypeForUser($user_id, $notification_type);
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/clear_all_notifications_by_type: '.$ex->getMessage());
		}

		return $this;
	}

	public function clear_single_notification($user_id, $notification_type, $notification_number)
	{
		try
		{
			$this->user_notification_service->clearNotificationsForUser($user_id, $notification_type, $notification_number);
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/clear_single_notification: '.$ex->getMessage());
		}

		return $this;
	}

	/**
	 * Notify user via email
	 * @param string $email_address
	 * @param string $subject
	 * @param string $body
	 * @return NotificationDAO $this
	 */
	function notify_user_via_email($email_address, $subject, $body)
	{
		try
		{
			$email = new com_projectgoth_fusion_slice_EmailUserNotification;
			$email->emailAddress = $email_address;
			$email->subject = $subject;
			$email->message = $body;

			$this->user_notification_service->notifyUserViaEmail($email);
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/notify_user_via_email email='.$email_address.' subject='.$subject.': '.$ex->getMessage());
		}
		return $this;
	}

	/**
	 * Notify fusion user
	 * @param string $insert_id
	 * @param integer $invited_user_id
	 * @param stringe $invited_username
	 * @param integer $notification_type
	 * @return NotificationDAO $this
	 */
	function notify_fusion_user($insert_id, $invited_user_id, $invited_username, $notification_type = 4)
	{
		try
		{
			$message = new com_projectgoth_fusion_slice_Message;
			$message->key = (string) $insert_id;
			$message->toUserId = $invited_user_id;
			$message->toUsername = $invited_username;
			$message->notificationType = $notification_type;
			$message->dateCreated = time();

			$this->user_notification_service->notifyFusionUser($message);
		}
		catch(Exception $ex)
		{
			error_log('NotificationDAO Exception: notification_dao/notify_fusion_user: '.$ex->getMessage());
		}

		return $this;
	}
}