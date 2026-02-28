<?php
	class XmlConstruct extends DOMDocument
	{
		public function get_xml_from_array($data, DOMElement $domElement = null)
		{
			$domElement = is_null($domElement) ? $this : $domElement;

			if (is_array($data))
			{
				foreach( $data as $index => $mixedElement )
				{
					if ( is_int($index) )
					{
						if ( $index == 0 )
						{
							$node = $domElement;
						}
						else
						{
							$node = $this->createElement($domElement->tagName);
							$domElement->parentNode->appendChild($node);
						}
					}
					else
					{
						$node = $this->createElement($index);
						$domElement->appendChild($node);
					}
					$this->get_xml_from_array($mixedElement, $node);
				}
			}
			else
			{
				$domElement->appendChild($this->createTextNode($data));
			}
		}
	}
?>