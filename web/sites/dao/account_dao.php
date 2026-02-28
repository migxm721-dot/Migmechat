<?php
	fast_require('Memcached', get_framework_common_directory() . '/memcached.php');
	fast_require('DAO', get_dao_directory() . '/dao.php');
	fast_require('Balance', get_domain_directory() . '/account/balance.php');
    fast_require('AccountTransaction', get_domain_directory() . '/account/account_transaction.php');
    fast_require('Constants', get_framework_common_directory(). '/constants.php');

	class AccountDAO extends DAO
	{

        public function get_balance($username)
        {
            $query = '  SELECT user.balance, user.fundedbalance, currency.*
                        FROM user, currency
                        WHERE user.currency = currency.code
                        AND username = ?';

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param('s', $username);
            $stmt->execute();

            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

            if($stmt->fetch())
            {
                $balance = new Balance($data);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $balance;

        }
        
        
        public function get_pending_payment_transactions($userID)
        {
        	try
			{
				$payments = FusionRest::get_instance()->get(sprintf(FusionRest::KEYSPACE_USER_PENDING_PAYMENTS, $userID));
				for($i=0; $i < count($payments); $i++) 
				{
					$payments[$i]['dateCreated'] = substr($payments[$i]['dateCreated'], 0, -4);
				}
				return $payments;
				
			}
			catch(Mig33apiException $e)
			{
				throw new Exception("Unable to retrieve pending payments.");
			}
        }

    }
?>