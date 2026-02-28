<?php
	class MenuItem
	{
		private $label;
		private $href;

		public function __construct( $label, $href )
		{
			$this->label = $label;
			$this->href = $href;
		}

		public function getLabel()
		{
			return $this->label;
		}

		public function getHref()
		{
			return $this->href;
		}

		public function show()
		{
			if( empty($this->label) )
			{
				printf('<menuitem />');
			}
			else
			{
				printf('<menuitem label="%s" href="%s" />', $this->label, $this->href );
			}
		}
	}

	class Menu
	{
		private $type;
		private $label;
		private $title;
		private $menuItems = Array();

		public function __construct( $type, $label, $title )
		{
			$type = strtoupper($type);
			if( $type != "LEFT" && $type != "RIGHT" ) throw new Exception("Invalid type.");

			$this->type = $type;
			$this->label = $label;
			$this->title = $title;
		}

		public function addMenuItem( $label, $href )
		{
			$menuitem = new MenuItem($label, $href);
			$this->menuItems[] = $menuitem;
		}

		public function addMenuDivider()
		{
			$this->addMenuItem("", "");
		}

		public function show()
		{
			printf('<menu type="%s" title="%s" label="%s">', $this->type, $this->title, $this->label);
			for($i=0; $i<sizeof($this->menuItems); $i++ )
			{
				$this->menuItems[$i]->show();
			}
			printf('</menu>');
		}
	}
?>