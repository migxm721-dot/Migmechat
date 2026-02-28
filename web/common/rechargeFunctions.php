<?php
	//include_once($_SERVER["DOCUMENT_ROOT"]."/common/common-inc.php");

	class Discount
	{
		public $percentage = 0;
		public $minimum = 0;
		public $currency;
		public $starterDiscount = false;

		public function getDiscountString()
		{
			if( $this->percentage == 0 ) return;
			if( $this->starterDiscount == false )
			{
				return sprintf("To get a %d%% discount you need to spent %s$%d on credits in a calendar month. Discount will be applied once you reach %s$%d.",
						round_twodec($this->percentage),
						$this->currency,
						round_twodec($this->minimum),
						$this->currency,
						round_twodec($this->minimum));
			}
			else
			{
				return "Get started with at least US$5 and you can buy at a Starter Discount of 30%, for your first purchase.";
			}
		}

	}

	class RechargeDiscounts
	{
		public $paymentType = 0;
		public $username = '';
		public $discounts = array();

		public function __construct($type, $username)
		{
			if( empty($type) )
				throw new Exception("Type not set for recharge discount.");

			if( empty($username) )
				throw new Exception("Username not set for recharge discount.");

			switch( strtolower($type) )
			{
				case "cc":
					$this->paymentType = 1;
					break;
				case "tt":
					$this->paymentType = 2;
					break;
				case "bt":
					$this->paymentType = 3;
					break;
				case "wu":
					$this->paymentType = 4;
					break;
			}
			$this->username = $username;
			try
			{
				$this->getDiscount();
			}
			catch(Exception $e)
			{
			}
		}

		private function isDiscountActive(&$discountTier)
		{
			return ($discountTier['canBeApplied']);
		}

		private function getDiscount()
		{
			$paymentType = 0;

			try
			{
				$discountTiers = soap_call_ejb('getDiscountTiers', array($this->paymentType, $this->username));
				for ($i = 0; $i < sizeof($discountTiers); $i++)
				{
					$discountTier = $discountTiers[$i];
					if( $this->isDiscountActive($discountTier) )
					{
						if (i < sizeof($discountTiers)-1 &&
							$this->isDiscountActive($discountTiers[$i+1]) &&
							$discountTier['percentageDiscount'] == $discountTiers[$i+1]['percentageDiscount'] &&
							$discountTier['actualMin'] > $discountTiers[$i+1]['actualMin'])
						{
									continue;
						}


						$discount = new Discount();
						$discount->percentage = round_twodec($discountTier['percentageDiscount']);
						$discount->minimum = round_twodec($discountTier['displayMin']);
						$discount->currency = $discountTier['currency'];
						$discount->starterDiscount = ($discountTier['type']=="FIRST_TIME_ONLY");
						$this->discounts[] = $discount;
					}
				}
			}
			catch(Exception $e)
			{
				if( empty($this->discounts) )
				{
					$discount = new Discount();
					$this->discounts[] = $discount;
				}
			}
		}
	}
?>