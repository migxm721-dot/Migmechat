<?php

	class ContactDTO
	{
		public $contactId;
		public $isContact;
		public $shareNumber;
		public $mobilePhone;

		public function __construct( &$contactDetail )
		{
			$this->shareNumber = 0;
			$this->isContact = $contactDetail['isContact'];
			if( isset( $contactDetail['id'] ) )
				$this->contactId = $contactDetail['id'];
			/*
			if( isset( $contactDetail['shareMobilePhone'] ) )
				$this->shareNumber = $contactDetail['shareMobilePhone'];
			*/
			if( !empty( $contactDetail['mobilePhone'] ) )
			{
				$this->shareNumber = 1;
				$this->mobilePhone = $contactDetail['mobilePhone'];
			}
		}

		/**
		* Check if the number is shared among other people
		**/
		public function isNumberShared()
		{
			if( empty($this->shareNumber) ) return false;
			else return ($this->shareNumber!=0);
		}
	}

	class UserDTO extends UserProfileDTO
	{
		public $dateRegistered;
		public $displayName;
		public $displayPicture;
		public $statusMessage;
		public $chatRoomAdmin;
		public $mobilePhone;
		public $allowBuzz;
		public $contact;
		public $countryId;
		public $emailActivated = 0;
		public $type;

		public function __construct( &$userProfile, &$userDetails, &$contactDetail )
		{
			if( !empty($userProfile) )
				parent::__construct( $userProfile );

			if( !empty($userDetails) )
			{
				$this->dateRegistered = $userDetails['dateRegistered'];
				$this->displayName = strip_tags($userDetails['displayName']);
				$this->displayPicture = $userDetails['displayPicture'];
				$this->statusMessage = strip_tags($userDetails['statusMessage']);
				$this->chatRoomAdmin = $userDetails['chatRoomAdmin'];
				$this->mobilePhone = $userDetails['mobilePhone'];
				$this->allowBuzz = $userDetails['allowBuzz'];
				$this->countryId = $userDetails['countryID'];
				$this->emailActivated = $userDetails['emailActivated'];
				$this->type = $userDetails['type'];
			}
			$this->contactDetail = $contactDetail;
			$this->contact = new ContactDTO($contactDetail);
		}

		public function isMerchant()
		{
			return (strtolower($this->type)!="mig33");
		}

		/**
		* Return true if the user is a moderator
		**/
		public function isModerator()
		{
			return ($this->chatRoomAdmin == 1);
		}

		/**
		* is email activated
		**/
		public function isEmailActivated()
		{
			return ($this->emailActivated == 1 );
		}

		/**
		* Get the member since
		*/
		public function getMemberSince()
		{
			return date('M y', $this->dateRegistered);
		}

		public function canViewProfile()
		{
			switch( $this->status )
			{
				case "PRIVATE":
					return false;
					break;
				case "CONTACTS_ONLY":
					if( isset($this->contact) )
					{
						return ( $this->contact->isContact );
					}
					else
					{
						return false;
					}
					break;
				case "PUBLIC":
					return true;
					break;
				default:
					return true;
			}
		}

		public function canViewUpdates()
		{
			switch( $this->status )
			{
				case "PRIVATE":
					return false;
				case "PUBLIC":
				case "CONTACTS_ONLY":
					if( isset($this->contact) )
					{
						return ( $this->contact->isContact );
					}
					else
					{
						return false;
					}
					break;
				default:
					return false;
			}
		}

		public function canViewFriends()
		{
			if( $this->isMerchant() ) return false;
			return $this->canViewProfile();
		}
	}

	class UserProfileDTO
	{
		public $username;
		public $firstName;
		public $lastName;
		public $homeTown;
		public $city;
		public $state;
		public $dateOfBirth;
		public $gender;
		public $jobs;
		public $schools;
		public $hobbies;
		public $likes;
		public $dislikes;
		public $relationshipStatus;
		public $status = "PRIVATE";
		public $anonymousViewing;
		public $aboutMe;

		public function __construct( &$userProfile )
		{
			$this->username = strip_tags($userProfile['username']);
			$this->firstName = strip_tags($userProfile['firstName']);
			$this->lastName = strip_tags($userProfile['lastName']);
			$this->homeTown = strip_tags($userProfile['homeTown']);
			$this->city = strip_tags($userProfile['city']);
			$this->state = strip_tags($userProfile['state']);
			$this->dateOfBirth = $userProfile['dateOfBirth'];

			if(isset($userProfile['gender']))
			{
				if($userProfile['gender'] == 'MALE')
					$this->gender = "Male";
				else if( $userProfile['gender'] == 'FEMALE' )
					$this->gender = "Female";
				else
					$this->gender = "Undecided";
			}

			$this->jobs = strip_tags($userProfile['jobs']);
			$this->schools = strip_tags($userProfile['schools']);
			$this->hobbies = strip_tags($userProfile['hobbies']);
			$this->likes = strip_tags($userProfile['likes']);
			$this->dislikes = strip_tags($userProfile['dislikes']);
			if($userProfile['relationshipStatus'] != '')
			{
				if($userProfile['relationshipStatus'] == 'SINGLE')
				{
					$this->relationshipStatus = 'Single ';
				}
				else if($userProfile['relationshipStatus'] == 'IN_A_RELATIONSHIP')
				{
					$this->relationshipStatus = 'In a Relationship ';
				}
				else if($userProfile['relationshipStatus'] == 'DOMESTIC_PARTNER')
				{
					$this->relationshipStatus = 'Domestic Partner ';
				}
				else if($userProfile['relationshipStatus'] == 'MARRIED')
				{
					$this->relationshipStatus = 'Married ';
				}
				else if($userProfile['relationshipStatus'] == 'COMPLICATED')
				{
					$this->relationshipStatus = 'Complicated ';
				}
				else
				{
					$this->relationshipStatus = strip_tags($userProfile['relationshipStatus']);
				}
			}

			$this->status = strip_tags($userProfile['status']);
			$this->anonymousViewing = $userProfile['anonymousViewing'];
			$this->aboutMe = strip_tags($userProfile['aboutMe']);
		}

		/**
		* Get the age of the user
		**/
		public function getAge()
		{
			$age = 0;
			if( !empty($this->dateOfBirth) )
			{
				$year = date("Y");
				$age = $year - date('Y', $this->dateOfBirth);
				$bday = date("m/d/",$this->dateOfBirth).$year;
				if((time()-strtotime($bday))<0) $age = $age - 1;
			}
			return $age;
		}

		/**
		* Return the status string
		**/
		public function getStatus()
		{
			if( !empty($this->status ) )
			{
				if( $this->status == "PUBLIC" ) return "Everyone";
				if( $this->status == "PRIVATE" ) return "Myself";
				else if( $this->status == "CONTACTS_ONLY" ) return "Friends";
				else return "Everyone";
			}
			else
				return "Everyone";
		}
	}
?>