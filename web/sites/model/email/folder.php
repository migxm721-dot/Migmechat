<?php
	class FolderModel extends Model
	{
		public function get_data($model_data)
		{
			// IMAP Server details
			global $imap_server, $imap_port;

			// Username & Password
			$username = get_value_from_array('session_user', $model_data);
			$password = $model_data['session_user_detail']->get_password();

			// Variables
			$folder_id = get_value('bid', 'integer', 1);
			$number_of_entries = get_attribute_value('number_of_entries', 'integer', 10);
			$page = get_attribute_value('page', 'integer', 1);

			// Pagination
			if($page <= 0)
			{
				$page = 1;
			}
			$offset = ($page-1)*$number_of_entries;

			if (!function_exists('imap_open'))
			{
				return array('error' => 'imap is not installed!');
			}

			// Open The Mailbox
			try
			{
				$emails = array();

				switch($folder_id)
				{
					case 2:
						$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Sent Items', $username, $password, CL_EXPUNGE);
						break;
					case 3:
					 	$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Trash', $username, $password, CL_EXPUNGE);
					 	break;
					 case 1:
					 default:
					 	$mailbox = @imap_open('{'.$imap_server.':'.$imap_port.'/imap}Inbox', $username, $password, CL_EXPUNGE);
				}

				if($mailbox)
				{
					// Sort the mailbox by date
					$headers = @imap_sort($mailbox, 1, 1);

					// Pagination
					$total_entries = sizeof($headers);
					$total_pages = ceil($total_entries/$number_of_entries);
					$model_data['page'] = $page;
					$model_data['total_entries'] = $total_entries;
					$model_data['total_pages'] = $total_pages;
					$raw_emails = array_slice($headers, $offset, $number_of_entries);

					// Process the emails
					if(sizeof($raw_emails) > 0)
					{
						foreach($raw_emails as $raw_email)
						{
							// Get the structure
							$struct = @imap_fetchstructure($mailbox, $message_id);
							$parts = $struct->parts;
							$part_number = '1';
							$i = 1;

							// If the message is multipart then loop through the parts. We want to use the HTML part preferably. Otherwise, PLAIN. If neither can be found, we assume the content is part '1'.
							if($parts)
							{
								foreach($parts as $part)
								{
									if ($part-subtype == 'HTML')
									{
										$msg_type = 'HTML';
										$part_number = $i;
									}
									else if ($part-subtype == 'PLAIN')
									{
										$msg_type = 'PLAIN';
										$part_number = $i;
									}
									$i++;
								}
							}

							$email_header = @imap_headerinfo($mailbox, $raw_email);
							$from = '';
							$to = '';
							$subject = '';
							$date = '';

							// From
							$from_object = $email_header->from;
							$reply_email = mail_return_address($email_header);
							if (! empty($from_object) && is_array($from_object))
							foreach ($from_object as $id => $object)
							{
								$from = strip_tags($object->personal);
							}
							if(empty($from))
								$from = _('Unknown');

							// To
							$to = strip_tags($email_header->toaddress);
							if(empty($to))
								$to = _('Unknown');

							// Subject
							$subject = strip_tags($email_header->subject);
							if (strlen($subject) == 0)
								$subject = _('(No Subject)');

							// Date
							$date = $email_header->date;

							// Message ID
							$message_id = trim($email_header->Msgno);

							// Unread Flag
							$unread = false;
							if($email_header->Unseen == 'U')
								$unread = true;

							// Recent Flag
							$recent = false;
							if($email_header->Recent == 'N')
								$recent = true;

							//Fetch message body
							$message_body = @imap_fetchbody($mailbox, $message_id, $part_number);


							if($msg_type == 'HTML')
							{
								$message_body = $message_body;
							}
							else
							{
								$message_body = nl2brNoSlash($message_body);
							}

							$emails[] = array('from' => $from, 'reply_email' => $reply_email, 'to' => $to, 'subject' => $subject, 'date' => $date, 'message_id' => $message_id, 'unread' => $unread, 'recent' => $recent, 'message_body' => $message_body);
						}
					}
					@imap_close($mailbox);
				}

				$model_data['emails'] = $emails;

				return $model_data;
			}
			catch(Exception $e)
			{
				return array('error' => $e->getMessage());
			}
		}
	}
?>