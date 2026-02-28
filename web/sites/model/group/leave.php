<?php
	class LeaveModel extends Model
	{
		public function get_data($model_data)
		{
			$session_user = $model_data["session_user"];
			$group_id = get_attribute_value("group_id", "integer", 0);

			if(!$model_data["session_user_creator"]){
				$call_return = make_soap_call("leaveGroup", array($session_user, $group_id));
				return array("ok"=>($call_return->is_error() == false));
			}
			return array("ok"=>false);
		}
	}
?>