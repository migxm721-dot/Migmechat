<?php
	fast_require('ChatRoomDAO', get_dao_directory() . '/chatroom_dao.php');

	class BotHelpModel extends Model
	{
		public function get_data($model_data)
		{
			$id = get_value('id', 'integer', 0);
			$dao = new ChatRoomDAO();
			return $dao->get_chatroom_game_bot($id);
		}
	}
?>