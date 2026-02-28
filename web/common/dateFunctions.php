<?php

	class DateTimeDifference
	{
		// members
		// time in unix time
		private $date1;
		// time in unix time
		private $date2;
		private $secondsDiff;
		private $year1;
		private $year2;
		private $month1;
		private $month2;
		private $day1;
		private $day2;
		private $hour1;
		private $hour2;

		// constructor
		public function __construct( $d1, $d2 )
		{
			if( !is_numeric($d1) ) throw new Exception("Invalid type for date1.");
			if( !is_numeric($d2) ) throw new Exception("Invalid type for date2." );
			if( $d1 > $d2 )
			{
				$td2 = $d2;
				$d2 = $d1;
				$d1 = $td2;
			}
			$this->date1 = $d1;
			$this->date2 = $d2;
			$this->year1 = date("Y", $this->date1);
			settype($year1, "integer");
			$this->year2 = date("Y", $this->date2);
			settype($year2, "integer");
			$this->month1 = date("m", $this->date1);
			settype($month1, "integer");
			$this->month2 = date("m", $this->date2);
			settype($month2, "integer");
			$this->day1 = date("d", $this->date1);
			settype($day1, "integer");
			$this->day2 = date("d", $this->date2);
			settype($day2, "integer");
			$this->hour1 = date("H", $this->date1);
			settype($hour1, "integer");
			$this->hour2 = date("H", $this->date2);
			settype($hour2, "integer");
			$this->secondsDiff = $this->date2 - $this->date1;
			settype($secondsDiff, "integer");
		}

		// public methods
		public function getDifferenceInSeconds()
		{
			return $this->secondsDiff;
		}

		public function getDifferenceInMinutes()
		{
			return $this->secondsDiff/60;
		}

		public function getDifferenceInHours()
		{
			return $this->secondsDiff/3600;
		}

		public function getDifferenceInDays()
		{
			$hoursAgo = $this->getDifferenceInHours();
			return ceil($hoursAgo/24);
		}

		public function getDifferenceInMonths()
		{
			$months = ($this->year2 * 12 + $this->month2) - ($this->year1 * 12 + $this->month1);
			return $months;
		}

		public function getDateTimeDifferenceSmallString()
		{
			if( $this->isSameDate() ) return "Today";
			if( $this->isYesterday() )
				return "Yesterday";
			else
			{
				$days = $this->getDifferenceInDays();
				settype($days, "integer");
				if( $this->month1 == $this->month2 )
				{
					return sprintf("%d day%s ago", $days, ($days>1?"s":""));
				}
				else
				{
					if( $this->isLastMonth() )
						return "Last month";
					else
					{
						$months = $this->getDifferenceInMonths();
						settype( $months, "integer" );
						return sprintf("%d mth%s ago", $months, ($months>1?"s":""));
					}
				}
			}
		}

		public function getDateTimeDifferenceString()
		{
			if( $this->isSameDate() )
			{
				$minutes = $this->getDifferenceInMinutes();
				settype($minutes, "integer");

				if( $this->hour1 == $this->hour2 )
				{
					if( $minutes <= 0 )
						return "Moments ago";
					else
						return sprintf("%d min%s ago", $minutes, ($minutes>1?"s":""));
				}
				else
				{
					$hours = $this->getDifferenceInHours();
					settype($hours, "integer");
					if( $hours == 0 )
						return sprintf("%d min%s ago", $minutes, ($minutes>1?"s":""));
					else
						return sprintf("%d hr%s ago", $hours, (($hours>1)?"s":""));
				}
			}
			else
			{
				if( $this->isYesterday() )
					return "Yesterday";
				else
				{
					$days = $this->getDifferenceInDays();
					settype($days, "integer");
					if( $this->month1 == $this->month2 )
					{
						return sprintf("%d day%s ago", $days, ($days>1?"s":""));
					}
					else
					{
						if( $this->isLastMonth() )
							return "Last month";
						else
						{
							$months = $this->getDifferenceInMonths();
							settype( $months, "integer" );
							return sprintf("%d mth%s ago", $months, ($months>1?"s":""));
						}
					}
				}
			}
		}

		// private methods
		/**
		* Determine if the two unix times passed are the same date
		**/
		private function isSameDate()
		{
			if( $this->date1 == $this->date2 ) return true;
			$d1 = date("Ymd", $this->date1);
			$d2 = date("Ymd", $this->date2);
			return ($d1==$d2);
		}

		/**
		* check for yesterday
		**/
		private function isYesterday()
		{
			if( $this->date1 == $this->date2 ) return false;

			$hourDifference = $this->getDifferenceInHours();

			settype($hourDifference, "integer");
			if( $hourDifference >= 24 && $hourDifference < 48) return true;
			else return false;
		}

		/**
		* Check for last month
		**/
		private function isLastMonth()
		{
			if( $this->year1 == $this->year2 )
				return (($this->month2 - 1) == $this->month1 );
			else if( ($this->year2-1) == $this->year1 )
			{
				if( $this->month2 == 1 && $this->month1 == 12 ) return true;
			}
			return false;
		}
	}

?>