<?php

	fast_require('DAO', get_dao_directory() . '/dao.php');
	fast_require('Currency', get_domain_directory() . '/account/currency.php');

	class CurrencyDAO extends DAO
	{

		public function convert($amount, $from_currency, $to_currency)
		{
			$query = "SELECT ? / fromcurrency.exchangerate * tocurrency.exchangerate AS amount
					  FROM currency fromcurrency
					  	   , currency tocurrency
					  WHERE fromcurrency.code = ?
					  AND tocurrency.code=?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("dss", $amount, $from_currency, $to_currency);
            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

            $converted_amount = null;
            if ($stmt->fetch())
            	$converted_amount = $data['amount'];

            if (is_null($converted_amount))
            	throw new Exception(_('Unknown currency code(s).'));

            $stmt->close();
            $this->closeSlaveConnection();

            return $converted_amount;
		}

		public function get_currency($code)
		{
			$query = "SELECT code
							 , name
							 , symbol
							 , exchangeRate
							 , lastUpdated
					  FROM currency
					  WHERE code = ?";

			$stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("s", $code);
            $stmt->execute();
            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

            $currency = null;
            if ($stmt->fetch())
            	$currency = new Currency($data);

            $stmt->close();
            $this->closeSlaveConnection();

            return $currency;

		}

	}

?>