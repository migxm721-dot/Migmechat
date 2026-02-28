<?php
	class BotController
	{
		public function bot_help(&$model_data)
		{
			$id = get_value("id");
			$view = new ControllerMethodReturn();
			$view->model_data = array();

			switch($id)
			{
				case 1:
					$view->method = "werewolf_help";
					break;
				case 2:
					$view->method = "roulette_help";
					break;
				case 3:
					$view->method = "trivia_help";
					break;
				case 4:
					$view->method = "vampire_help";
					break;
				case 5:
					$view->method = "one_help";
					break;
				case 6:
					$view->method = "rps_help";
					break;
				case 7:
					$view->method = "hot_help";
					break;
				case 8:
					$view->method = "trivia_help";
					break;
				case 9:
					$view->method = "dice_help";
					break;
				case 10:
					$view->method = "lowcard_help";
					break;
				case 11:
					$view->method = "trivia_help";
					break;
				case 12:
					$view->method = "trivia_help";
					break;
				case 13:
					$view->method = "trivia_help";
					break;
				case 14:
					$view->method = "trivia_help";
					break;
				case 15:
					$view->method = "trivia_help";
					break;
				case 16:
					$view->method = "football_help";
					break;
				case 17:
					$view->method = "knockout_help";
					break;
				case 18:
					$view->method = "blackjack_help";
					break;
				case 19:
					$view->method = "baccarat_help";
					break;
				case 20:
					$view->method = "girlfriend_help";
					break;
				case 21:
					$view->method = "boyfriend_help";
					break;
				case 22:
					$view->method = "guess_help";
					break;
				case 23:
					$view->method = "danger_help";
					break;
				case 24:
					$view->method = "migcricket_help";
					break;
				case 25:
					$view->method = "questions_help";
					break;
				case 26:
					$view->method = "taring_help";
					break;
				case 27:
					$view->method = "warriors_help";
					break;
			}
			return $view;
		}

	}
?>