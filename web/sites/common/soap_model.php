<?php
	require_once("web_utilities.php");

	abstract class SoapModel extends Model
	{
		protected function make_soap_call($method_name, $data)
		{
			return make_soap_call($method_name, $data);
		}
	}
?>