<?php
fast_require('DatasvcDAO', get_dao_directory() . '/datasvc_dao.php');

class DatasvcController
{
	public function route($data)
	{
		return new ControllerMethodReturn('datasvc', $data);
	}

	public function datasvc(&$model_data)
	{
		$result = new RestResult();
		$method = strtoupper($_SERVER['REQUEST_METHOD']);
		$method = in_array($method, array(Rest_request::POST, Rest_request::PUT, Rest_request::DELETE))
			? $method : Rest_request::GET;
		$request = new Rest_request();
		$request->method = $method;

		if (empty($model_data['session_user']))
			return $result->set_http_status(401)
				->set_app_status(401, _('Invalid session.'));

		$request->rest_path = $this->get_rest_path();
		if ($request->rest_path == '/sso/check')
			$request->rest_path = sprintf(FusionRest::KEYSPACE_SESSION_CHECK
				, SessionUtilities::get_sso_view()
				, SessionUtilities::get_session_id()
			);

		$datasvc_dao = DatasvcDAO::get_instance();
		$request = $datasvc_dao->determine_data_provider($request);

		switch ($request->provider)
		{
			case DatasvcDAO::FusionRest:
				return $datasvc_dao->request_from_fusion_rest($request);
				break;
			case DatasvcDAO::MigcoreRestService:
				return $datasvc_dao->back_to_controller($request, $model_data);
				break;
			default:
				return $result->set_http_status(404)->set_app_status(404, sprintf(_('Invalid API: %s'), $request->rest_path));
				break;
		}
	}

	private function get_rest_path()
	{
		$segments = explode('/', $_SERVER['SCRIPT_NAME']);

		// Cleanup empty slots
		foreach($segments as $i => $v)
			if(empty($v))
				unset($segments[$i]);

		array_shift($segments); //remove leading slash
		array_shift($segments); //remove sites
		array_shift($segments); //remove view

		return '/' . implode('/', $segments);
	}
}
