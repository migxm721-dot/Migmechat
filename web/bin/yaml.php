#!/usr/bin/php
<?php
if (PHP_SAPI != 'cli') exit("Access Denied\n");
$_SERVER['DOCUMENT_ROOT'] = $_SERVER['PWD'];
$_SERVER['SERVER_PORT'] = 80;
$_SERVER['SERVER_NAME'] = 'http://devlab.projectgoth.com/';
$_SERVER['CONTENT_TYPE'] = 'application/json';

if (! function_exists('apache_request_headers'))
{
	function apache_request_headers(){}
}

require_once('sites/common/utilities.php');
fast_require('ControllerDefinition', get_framework_common_directory() . '/controller_definition.php');
fast_require('Spyc', get_framework_common_directory() . '/spyc.php');

class CLI_COLOR
{
	const RED    = "\033[31m";
	const GREEN  = "\033[32m";
	const YELLOW = "\033[33m";
	const BLUE   = "\033[34m";
	const RESET  = "\033[0m";
	public static function RED($string = '')
	{
		return self::RED    . $string . self::RESET;
	}
	public static function GREEN($string = '')
	{
		return self::GREEN  . $string . self::RESET;
	}
	public static function YELLOW($string = '')
	{
		return self::YELLOW . $string . self::RESET;
	}
	public static function BLUE($string = '')
	{
		return self::BLUE   . $string . self::RESET;
	}
}

class ControllerMap
{
	public $directory = 'sites/controller/';
	public $temp = '/var/tmp/controller_definition.json';
	public $arguments = array();
	public $filters = array();
	public $option_map = array(
		  '--force'              => 'force'
		, '-f'                   => 'force'
		, '--verbose'            => 'verbose'
		, '-v'                   => 'verbose'
		, '--all'                => 'all'
		, '-a'                   => 'all'
		, '--help'               => 'help'
		, '-h'                   => 'help'
		, '--cache'              => 'cache'
		, '-c'                   => 'cache'
		, '--model'              => 'model'
		, '-m'                   => 'model'
		, '--validation'         => 'validation'
		, '--validators'         => 'validation'
		, '--validator'          => 'validation'
		, '-va'                  => 'validation'
		, '--function'           => 'function'
		, '-fn'                  => 'function'
		, '--decorators'         => 'decorators'
		, '--decorator'          => 'decorators'
		, '-d'                   => 'decorators'
		, '--view'               => 'view'
		, '-vi'                  => 'view'
		, '--url'                => 'url'
		, '-u'                   => 'url'
		, '--URL'                => 'URL'
		, '-U'                   => 'URL'
		, '--encode'             => 'encode'
		, '-e'                   => 'encode'
		, '--controller_action'  => 'controller_action'
		, '--controller-action'  => 'controller_action'
		, '-ca'                  => 'controller_action'
		, '--list'               => 'list'
		, '-l'                   => 'list'
	);
	public function __construct($argv)
	{
		foreach ($argv as $index => $arg)
		{
			if (! $index) continue;
			foreach ($this->option_map as $option => $filter)
			{
				if ($option == $arg)
				{
					if (in_array($filter, array('force', 'all', 'help', 'verbose', 'list', 'url', 'URL')))
					{
						$this->arguments[] = $filter;
					}
					else
					{
						$this->filters[$filter][] = isset($argv[$index+1]) ? $argv[$index+1] : null;
					}
				}
			}
		}

		if (empty($this->filters) && count($argv) > 1)
			foreach ($argv as $index => $arg)
				$index && 0 !== strpos($arg, '-') && $this->filters['controller_action'][] = $arg;

		empty($this->filters) && empty($this->arguments) && $this->arguments[] = 'help';
	}

	/**
	 * run only once
	 */
	public function generate_definition()
	{
		$yamls = scandir($this->directory);
		foreach ($yamls as &$file)
			$file = current(explode('.', $file));
		$yamls = array_unique($yamls);
		foreach ($yamls as $i => $controller)
		{
			unset($yamls[$i]);
			$yamls[$controller] = array();
			$actions = Spyc::YAMLLoad($this->directory . $controller . '.yaml');
			foreach ($actions as $action => $array)
			{
				if ($action == 'all') continue;
				$definition = new ControllerDefinition($controller, $action, false);
				$yamls[$controller][$action] = $definition->get_definition();
			}
		}
		return $yamls;
	}

	public function get_all_definitions()
	{
		if (! file_exists($this->temp) || in_array('force', $this->arguments))
			file_put_contents($this->temp, json_encode($this->generate_definition()));
		return file_get_contents($this->temp);
	}

	public function run()
	{
		if (in_array('help', $this->arguments))
			return $this->help();

		$definitions = $this->get_all_definitions();
		if (in_array('all', $this->arguments))
			exit($definitions);

		$controller_action = array();
		$definitions = json_decode($definitions, true);

		foreach ($definitions as $controller => $actions)
		{
			foreach ($actions as $action => $definition)
			{
				$models = array();
				$validators = array();
				foreach($definition['components'] as $component)
				{
					if($component['type'] == 'Model')
					{
						array_push($models, $component);
					}

					if($component['type'] == 'Validator')
					{
						array_push($validators, $component);
					}
				}

				if (empty($this->filters) && in_array('list', $this->arguments))
				{
					echo $this->format_output($definition, $controller, $action);
					continue;
				}
				foreach ($this->filters as $type => $values)
				{
					switch ($type)
					{
						case 'cache':
							$cache = $definition['cache'];
							if ($cache) echo $this->format_output($definition, $controller, $action);
							break;
						case 'model':
							foreach ($models as $index => $model)
								foreach ($values as $value)
									if (isset($model['class']) && in_array($value, array($model['class'], $model['name'], $model['base'] . '.' . $model['name'], $model['base'] . '/' . $model['name'])))
										echo $this->format_output($definition, $controller, $action, $model['args']);
							break;
						case 'validation':
							foreach ($validators as $index => $validator)
								foreach ($values as $value)
									if (isset($validator['class']) && in_array($value, array($validator['class'], $validator['name'], $validator['base'] . '.' . $validator['name'], $validator['base'] . '/' . $validator['name'])))
										echo $this->format_output($definition, $controller, $action, $validator['args']);
							break;
						case 'function':
							$function = $definition['function'];
							if ($function && (is_null($values[0]) || in_array($function['class'] . '::' . $function['method'], $values)))
								echo $this->format_output($definition, $controller, $action, $function['args']);
							break;
						case 'decorators':
							$decorators = $definition['decorators'];
							foreach ($decorators as $view => $prop)
								if (in_array($prop['name'], $values))
									echo $this->format_output($definition, $controller, $action);
							break;
						case 'view':
							$view = $definition['view'];
							if ($view)
								foreach ($values as $value)
									if (is_null($value) || in_array($value, array($view, $controller . '.' . $view, $controller . '/' . $view)))
										echo $this->format_output($definition, $controller, $action);
							break;
						case 'encode':
							$encode = $definition['encode'];
							if ($encode) echo $this->format_output($definition, $controller, $action);
							break;
						case 'controller_action':
							foreach ($values as $index => $ca)
							{
								if ($controller == $ca)
								{
									if (in_array('list', $this->arguments))
										echo $this->format_output($definition, $controller, $action);
									else
										$controller_action[$controller][$action] = $this->summary($definition, $controller, $action);
								}
								else if (strpos($ca, '/') || strpos($ca, '.'))
								{
									list($c, $a) = preg_split('/\.|\//', $ca, 2);
									if ($controller == $c && $action == $a)
										$controller_action[$controller][$action] = $this->summary($definition, $controller, $action);
								}
							}
							break;
						default:
							echo $this->format_output($definition, $controller, $action);
							break;
					}
				}
			}
		}
		if (! empty($controller_action))
		{
			if (in_array('url', $this->arguments) || in_array('URL', $this->arguments))
			{
				foreach ($controller_action as $c => $as)
				{
					foreach ($as as $a => $d)
					{
						echo $this->format_output($d, $c, $a);
					}
				}
			}
			else if (exec('which json'))
			{
				foreach ($controller_action as $c => $as)
					if (count($as) === 1)
						foreach ($as as $a => $d)
							$controller_action = $d;
				passthru(sprintf("echo %s | json", escapeshellarg(json_encode($controller_action))));
			}
			else echo json_encode($controller_action);
		}
	}

	protected function summary($definition, $controller, $action)
	{
		if (in_array('verbose', $this->arguments)) return $definition;
		foreach ($definition as $key=>&$properties)
		{
			if (empty($properties))
			{
				unset($definition[$key]);
				continue;
			}

			switch ($key)
			{
				case 'components':
					foreach ($properties as &$property)
					{
						if($property['type'] == 'Validator')
							$property['name'] .= '_validator';

						$property = $property['base'] . '.' . $property['name'] . (empty($property['args'])?'':' ' . implode(' ', $property['args']));
					}
					break;
				default:
					break;
			}
		}
		return $this->trim_decorator_by_view($definition, $controller);
	}

	protected function trim_decorator_by_view($definition, $controller)
	{
		if (empty($definition['view']) || empty($definition['decorators']))
		{
			unset($definition['decorators']);
			return $definition;
		}
		foreach ($definition['decorators'] as $view => &$property)
		{
			if (is_string($property)) continue;
			$property = implode(' ', $property);
			$view_template = sprintf('sites/view/%s/%s/template/%s_template.php', $controller, $view, $definition['view']);
			if (! file_exists($view_template)) unset($definition['decorators'][$view]);
		}
		return $definition;
	}

	protected function format_output($definition, $controller, $action, $args = array())
	{
		$args = implode(' ', $args);
		if (! empty($args)) $args = ' ' . $args;
		if (! in_array('url', $this->arguments) && ! in_array('URL', $this->arguments))
		{
			return $controller . '/' . $action . $args . "\n";
		}
		$definition = $this->trim_decorator_by_view($definition, $controller);
		$server_root = in_array('URL', $this->arguments) ? $_SERVER['SERVER_NAME'] : '';
		if (! empty($definition['encode']))
		{
			return $server_root . 'sites/json/' . $controller . '/' . $action . $args . "\n";
		}
		else if (empty($definition['decorators']) && empty($definition['view']))
		{
			if (empty($definition['function']))
				return $server_root . 'sites/' . CLI_COLOR::RED('*') . '/' . $controller . '/' . $action . $args . "\n";
			else
				return $server_root . 'sites/' . CLI_COLOR::RED('FUNCTION') . '/' . $controller . '/' . $action . $args . "\n";
		}
		else
		{
			$array = array();
			if (empty($definition['decorators']))
			{
				$templates = glob(sprintf('sites/view/%s/*/template/%s_template.php', $controller, $definition['view']));
				foreach ($templates as $template)
				{
					$view = explode('/', $template);
					$array[] = $server_root . 'sites/' . $view[3] . '/' . $controller . '/' . $action . $args . "\n";
				}
			}
			else
			foreach ($definition['decorators'] as $view => $template)
				$array[] = $server_root . 'sites/' . $view . '/' . $controller . '/' . $action . $args . "\n";
			return implode('', $array);
		}
	}

	public function help()
	{
		global $argv;
		$help = array();
		$help[] = 'YAML Filter';
		$help[] = '';
		$help[] = 'php ' . $argv[0] . ' [-ca|--controller_action] [profile[/home_migcore]]...';
		$help[] = 'php ' . $argv[0] . ' [-h|--help] [-f|--force] [-v|--verbose] [-u|--url|-U|--URL]';
		$help[] = 'php ' . $argv[0] . ' [-c|--cache]';
		$help[] = 'php ' . $argv[0] . ' [[-m|--model] [UserModel]...]';
		$help[] = 'php ' . $argv[0] . ' [[-va|--validat[ion|or[s]]] [IsCaptchaRequiredValidator]...]';
		$help[] = 'php ' . $argv[0] . ' [[-fn|--function] [DeveloperSvcController::get_applications]...]';
		$help[] = 'php ' . $argv[0] . ' [[-d|--decorator[s]] [common.ajax]...]';
		$help[] = 'php ' . $argv[0] . ' [[-vi|--view] [list]...]';
		$help[] = 'php ' . $argv[0] . ' [-e|--encode]';
		$help[] = '';

		exit(implode("\n", $help));
	}
}

$map = new ControllerMap($argv);
$map->run();