<?php
	class Bot
	{
		public $id;
		public $game;
		public $displayname;
		public $description;
		public $commandname;
		public $type;
		public $leaderboards;
		public $emoticonkeylist;
		public $sortorder;
		public $status;
		public $group_id;
		public $group_name;
		public $group_picture;

		public function __construct($data)
		{
			$free_bots_array = array(1, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15, 20, 21);

			$this->id = get_value_from_array('id', $data, 'integer', 0);
			$this->game = get_value_from_array('game', $data);
			$this->displayname = get_value_from_array('displayname', $data);
			$this->description = get_value_from_array('description', $data);
			$this->commandname = get_value_from_array('commandname', $data);
			$this->type = get_value_from_array('type', $data, 'integer', 0);
			$this->leaderboards = get_value_from_array('leaderboards', $data, 'integer', 0);
			$this->emoticonkeylist = get_value_from_array('emoticonkeylist', $data);
			$this->sortorder = get_value_from_array('sortorder', $data, 'integer', 0);
			$this->is_free = in_array($this->id, $free_bots_array);
			$this->status = get_value_from_array('status', $data, 'integer', 0);

			$this->group_id = get_value_from_array('group_id', $data, 'integer', 0);
			$this->group_name = get_value_from_array('group_name', $data);
			$this->group_picture = get_value_from_array('group_picture', $data);
		}
	}
?>