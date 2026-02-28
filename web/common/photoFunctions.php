<?php
require_once("common-inc.php");
require_once("pageletFunctions.php");
require_once("profileFunctions.php");
require_once($_SERVER["DOCUMENT_ROOT"] . "/sites/common/utilities.php");
fast_require("UserDAO", get_dao_directory() . "/user_dao.php");
fast_require("PhotosDAO", get_dao_directory() . "/photos_dao.php");

/*
* Get the photos for a particular user
*/
function viewPhotos($username, UserPagingObject &$pagingObject, $showSend, $showSet, $viewOwn, $showUploadWall=false)
{
	try
	{
		global $server_root;
		global $mogileFSImagePath;
		global $sessUser;
		// Check UserLevel
		$user_dao = new UserDAO();
		$reputation_level_permission = $user_dao->get_user_level_and_reputation_level_permission($sessUser, 'PostCommentLikeUserWall');
		if ($reputation_level_permission['user_level'] < $reputation_level_permission['required_level'])
		{
			$allow_like = false;
		}
		else
		{
			$allow_like = true;
		}
		$pagingObject->error='';
		if( $username != $pagingObject->username )
		{
			$content = soap_call_ejb('getGallery',
				array($username, $pagingObject->username, $pagingObject->pageNumber, $pagingObject->numberOfEntries));
		}
		else
		{
			$content = soap_call_ejb('getScrapbook',
				array( $username, $pagingObject->pageNumber, $pagingObject->numberOfEntries));
		}
		$page = $content[0]['page'];
		$num_pages = $content[0]['numPages'];
		$num_entries = $content[0]['numEntries'];

		$style = new Style();
		showHeader("Photos");
		if($num_entries <= 0)
		{
			echo '<p>User does not have any published photos.</p><br>';
		}
		else
		{
			$headers = apache_request_headers();
			$screenWidth = $headers["sw"];
			$screenHeight = $headers["sh"];
			// Get the photos IDs first to query the ratings
			$photos_ids = array();
			for ($i = 1; $i < sizeof($content); $i++)
			{
				$photos_ids[] = $content[$i]['id'];
			}
			$photos_dao = new PhotosDAO();
			$photos_likes = $photos_dao->get_photos_likes($photos_ids);
			for ($i = 1; $i < sizeof($content); $i++)
			{
				$fileId = $content[$i]['file.id'];
				$id = $content[$i]['id'];
				$originalWidth = $content[$i]['file.width'];
				$originalHeight = $content[$i]['file.height'];
				$status = $content[$i]['status'];

				$requestedHeight = $screenHeight * (0.25);
				$requestedWidth = $screenHeight * (0.25);
				$result = getRatio($originalWidth, $originalHeight, $requestedWidth, $requestedHeight);

				$datetime = $content[$i]['dateCreated'];
				//$td = ($content[$i]['description']=='')?ereg_replace(' ', '%20', date('M-d-Y h:m', $datetime)):ereg_replace(' ', '%20',strip_tags($content[$i]['description']));
				//$rd = ($content[$i]['description']=='')?date('M-d-Y h:m', $datetime):strip_tags($content[$i]['description']);
				$description = $content[$i]['description'];
				if( !empty($description) )
				{
					$description = str_replace(":", " ", $description);
				}

				$td = (empty($description))?ereg_replace(' ', '%20', date('M-d-Y', $datetime)):ereg_replace(' ', '%20',strip_tags($description));
				$rd = (empty($description))?date('M-d-Y', $datetime):strip_tags($description);
				$description = '<a href="'.$server_root.'/sites/index.php?c=photo&v=midlet&a=view_photo&imgid='.$fileId.'&itid='.$id.'&username='.$pagingObject->username.'">'.$rd.'</a>';
				$send = "";
				$uploadWall = "";
				if( $showSend )
				{
					if( isMidletVersion4() )
					{
						$send = sprintf('<a href="mig33:sendScrapbookPhoto(%s)">Send to Friend</a><br>', $fileId);
					}
					else
					{
						$send = sprintf('<tag id="%s" type="2">Send to Friend</tag><br>', $fileId);
					}
				}
				if( $viewOwn )
				{
					if( $showSet )
					{
						$setProfile = '<a href="'.$server_root.'/sites/index.php?c=photo&v=midlet&a=set_profile_picture&imgid='.$fileId.'&itid='.$id.'">Set as Portrait</a><br>';
					}
				}
				if($allow_like)
				{
					//Generate Like String
					$like_string = '<a href="'.$server_root.'/sites/index.php?c=photo&v=midlet&a=photo_like&itid='.$id.'&imgid='.$fileId.'&username='.$pagingObject->username.'">Like</a> ('.intval($photos_likes[$id]['numlikes']).')&nbsp;&nbsp;';

					//Generate Dislike String
					$dislike_string = '<a href="'.$server_root.'/sites/index.php?c=photo&v=midlet&a=photo_dislike&itid='.$id.'&imgid='.$fileId.'&username='.$pagingObject->username.'">Dislike</a> ('.intval($photos_likes[$id]['numdislikes']).')';

					printf('<p><img src="%s/%s.jpeg?w=%d&h=%d&c=1&a=1" vspace="2" hspace="2" width="%d" height="%d" style="float:left">%s%s%s<br>%s</p><br>', $mogileFSImagePath, $fileId, $result[0], $result[0], $result[0], $result[0], $send, $setProfile, $description, $like_string.$dislike_string);
				}
				else
				{
					printf('<p><img src="%s/%s.jpeg?w=%d&h=%d&c=1&a=1" vspace="2" hspace="2" width="%d" height="%d" style="float:left">%s%s%s</p><br>', $mogileFSImagePath, $fileId, $result[0], $result[0], $result[0], $result[0], $send, $setProfile, $description);
				}
			}
		}
		return $content;
 	}
 	catch(Exception $e)
 	{
 		echo '<p>Unable to retrieve user\'s published photos. '.$e->getMessage().'</p><br>';
 		$pagingObject->error = $e->getMessage();
 	}
}

class RetrievePhotoException extends Exception
{
}

class Photo
{
	public $image_id;
	public $description;
	public $date_created;
	public $item_id;
	public $width;
	public $height;
	public $status;
	public $prev_image_id = 0;
	public $next_image_id = 0;
	public $current_page = 0;
	public $total_pages = 0;

	public function __construct($image_id, $sessionUser, $username)
	{
		settype($image_id, "integer");
		$photoDetail = soap_call_ejb('getPhoto', array($image_id, $sessionUser, $username));
		if( $photoDetail == null )
			throw new RetrievePhotoException();

		$this->image_id = $photoDetail['file.id'];
		$this->date_created = $photoDetail['dateCreated'];
		$this->description = ($photoDetail['description']=='')?date('M-d-Y h:m', $this->date_created):
									strip_tags($photoDetail['description']);
		$this->item_id = $photoDetail['id'];
		settype( $this->item_id, "integer");
		$this->width = $photoDetail['file.width'];
		$this->height = $photoDetail['file.height'];
		$this->status = $photoDetail['status'];

		if( isset($photoDetail['prevId']) )
		{
			$this->prev_image_id = $photoDetail['prevId'];
			settype($this->prev_image_id, "integer");
		}

		if( isset($photoDetail['nextId']) )
		{
			$this->next_image_id = $photoDetail['nextId'];
			settype($this->next_image_id, "integer");
		}

		if( isset($photoDetail['page']) )
		{
			$this->current_page = $photoDetail['page'];
			settype($this->current_page, "integer");
		}

		if( isset($photoDetail['total']) )
		{
			$this->total_pages = $photoDetail['total'];
			settype($this->total_pages, "integer");
		}
	}
}

?>