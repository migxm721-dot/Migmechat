<?php
	fast_require("DAO", get_dao_directory() . "/dao.php");

	fast_require("Partner", get_domain_directory() . "/vas/partner.php");
	fast_require("VasPartnerUser", get_domain_directory() . "/vas/vas_partner_user.php");
	fast_require("VASWorld", get_domain_directory() . "/vas/vasworld.php");
	fast_require("Agreement", get_domain_directory() . "/vas/agreement.php");
	fast_require("AgreementStat", get_domain_directory() . "/vas/agreement_stat.php");
	fast_require("AgreementStatMonth", get_domain_directory() . "/vas/agreement_stat_month.php");
	fast_require("AgreementStatQuarter", get_domain_directory() . "/vas/agreement_stat_quarter.php");

	class PartnerDAO extends DAO
	{
		public function get_agreement_by_user_agent($user_agent)
		{
			$query = "SELECT pa.ID as ID,
							 pa.PartnerID as PartnerID,
							 pa.Name as Name,
							 pa.FinderFee as FinderFee,
							 pa.RevenueShare as RevenueShare,
							 pa.ProductSMS as ProductSMS,
							 pa.ProductVoice as ProductVoice,
							 pa.ProductGames as ProductGames,
							 pa.ProductVG as ProductVG,
							 pa.ProductOthers as ProductOthers,
							 pa.DateCreated as DateCreated,
							 pa.StartDate as StartDate,
							 pa.EndDate as EndDate
					    FROM partneragreement pa,
					    	 partneragreementbuild pab,
					    	 partnerbuild pb
					   WHERE pab.PartnerBuildID = pb.ID
					     AND pa.ID = pab.PartnerAgreementID
					     AND pb.UserAgent=?
					     AND pa.StartDate <= DATE(NOW())
					     AND pa.EndDate >= DATE(NOW())";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("s", $user_agent);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreement = null;
            if( $stmt->fetch() )
            {
				$agreement = new Agreement($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $agreement;
		}

		public function get_partner_by_agreement_id($agreement_id)
		{
			$query = "SELECT partner.ID as ID,
                             partner.Name as Name,
                             partner.DateCreated as DateCreated
                        FROM partner, partneragreement pa
                       WHERE partner.id = pa.PartnerID
                         AND pa.ID = ?";

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $agreement_id);
            $stmt->execute();

            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

            $partner = null;

            if( $stmt->fetch() )
            {
                $partner = new Partner($data);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $partner;
		}

        public function get_partner_by_user_agent( $build_user_agent )
        {
            $query = "SELECT partner.ID as ID,
                             partner.Name as Name,
                             partner.DateCreated as DateCreated
                        FROM partnerbuild, partner
                       WHERE partner.id = partnerbuild.PartnerID
                         AND partnerbuild.UserAgent = ?";

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("s", $build_user_agent);
            $stmt->execute();

            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

            $partner = null;

            if( $stmt->fetch() )
            {
                $partner = new Partner($data);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $partner;
        }

		// TODO: Add memcached
		public function get_partner_detail($user_id)
		{
			$query = "SELECT partner.ID as ID,
							 partner.Name as Name,
							 partner.DateCreated as DateCreated
                        FROM partneruser, partner
                       WHERE partner.id = partneruser.partnerid
                         AND partneruser.userid = ?";

            $stmt = $this->getSlaveConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $user_id);
            $stmt->execute();

            $this->getSlaveConnection()->stmt_bind_assoc($stmt, $data);

			$partner = null;
            if( $stmt->fetch() )
            {
				$partner = new Partner($data);
            }

            $stmt->close();
            $this->closeSlaveConnection();

            return $partner;
		}

		public function get_partner_user($partner_id, $user_id)
		{
			$query = "SELECT pu.UserID as UserID,
							 pu.PartnerID as PartnerID,
							 pu.Membership as Membership,
							 uid.username as Username
                        FROM partneruser pu, userid uid
                       WHERE pu.PartnerID = ?
                         AND pu.UserID = ?
                         AND uid.id = pu.UserID";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $partner_id, $user_id);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$partner_user = null;
            if( $stmt->fetch() )
            {
				$partner_user = new VasPartnerUser($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $partner_user;
		}

		public function get_partner_users($partner_id)
		{
			$query = "SELECT pu.UserID as UserID,
							 pu.PartnerID as PartnerID,
							 pu.Membership as Membership,
							 uid.Username as Username
                        FROM partneruser pu, userid uid
                       WHERE pu.PartnerID = ?
                         AND uid.id = pu.UserID
                    ORDER BY pu.Membership DESC";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $partner_id);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$partner_users = null;
            while( $stmt->fetch() )
            {
				$partner_users[] = new VasPartnerUser($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $partner_users;
		}

		public function add_partner_user($partner_id, $user_id, $membership)
		{
			if($this->get_partner_user($partner_id, $user_id) != null)
			{
				return null;
			}

			$query = "INSERT INTO partneruser (UserID, PartnerID, Membership) VALUES (?, ?, ?)";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iii", $user_id, $partner_id, $membership);
            $stmt->execute();

            $stmt->close();
            $this->closeMasterConnection();

            return true;
		}

		public function set_partner_user($partner_id, $user_id, $membership)
		{
			if($this->get_partner_user($partner_id, $user_id) == null)
			{
				return null;
			}

			$query = "UPDATE partneruser SET Membership=? WHERE UserID=? AND PartnerID=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iii", $membership, $user_id, $partner_id);
            $stmt->execute();

            $stmt->close();
            $this->closeMasterConnection();

            return true;
		}

		public function remove_partner_user($partner_id, $user_id)
		{
			if($this->get_partner_user($partner_id, $user_id) == null)
			{
				return null;
			}

			$query = "DELETE FROM partneruser WHERE UserID=? AND PartnerID=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $user_id, $partner_id);
            $stmt->execute();

            $stmt->close();
            $this->closeMasterConnection();

            return true;
		}

		public function change_vasworld_status($agreement_id, $vasworld_id, $status)
		{
			if ($status == VASWorld::$STATUS_PUBLISHED)
			{
				$query = "UPDATE partnervasworld pvw SET Status=?, DateUpdated=NOW() WHERE PartnerAgreementID=? AND Status=?";

				$stmt = $this->getMasterConnection()->get_prepared_statement($query);
	            $stmt->bind_param("iii", VASWorld::$STATUS_APPROVED, $agreement_id, VASWorld::$STATUS_PUBLISHED);
	            $stmt->execute();
			}

			$query = "UPDATE partnervasworld pvw SET Status=?, DateUpdated=NOW() WHERE PartnerAgreementID=? AND ID=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iii", $status, $agreement_id, $vasworld_id);
            $stmt->execute();

            $stmt->close();
            $this->closeMasterConnection();
		}

		public function get_vasworld_draft_by_agreement_id($agreement_id)
		{
			$query = "SELECT pvw.ID as ID,
			 				 pvw.PartnerAgreementID as AgreementID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated
						FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID=?
					     AND pvw.Status=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $agreement_id, VASWorld::$STATUS_DRAFT);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworld = null;
            if( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworld;
		}

		public function get_vasworld_draft($agreement_id, $draft_id)
		{
			$query = "SELECT pvw.ID as ID,
			 				 pvw.PartnerAgreementID as AgreementID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated
						FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID=?
					     AND pvw.ID=?
					     AND pvw.Status=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iii", $agreement_id, $draft_id, VASWorld::$STATUS_DRAFT);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworld = null;
            if( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworld;
		}

		public function get_vasworld_drafts($partner_id)
		{
			$query = "SELECT pvw.ID as ID,
			 				 pvw.PartnerAgreementID as AgreementID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated,

			 				 pa.PartnerID as PartnerID,
			 				 pa.Name as AgreementName,
			 				 pa.DateCreated as AgreementDateCreated,
			 				 pa.FinderFee as AgreementFinderFee,
			 				 pa.RevenueShare as AgreementRevenueShare,
			 				 pa.ProductSMS as AgreementProductSMS,
			 				 pa.ProductVoice as AgreementProductVoice,
			 				 pa.ProductGames as AgreementGames,
			 				 pa.ProductVG as AgreementProductVG,
			 				 pa.ProductOthers as AgreementProductOthers,
			 				 pa.StartDate as AgreementStartDate,
			 				 pa.EndDate as AgreementEndDate
						FROM partnervasworld pvw, partneragreement pa
					   WHERE pvw.PartnerAgreementID=pa.id
					     AND pa.PartnerID=?
					     AND pvw.Status=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $partner_id, VASWorld::$STATUS_DRAFT);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworlds = array();
           	while( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);

				$agreement = new Agreement(array());
				$agreement->id = $data['AgreementID'];
				$agreement->partner_id = $data['PartnerID'];
				$agreement->name = $data['AgreementName'];
				$agreement->date_created = $data['AgreementDateCreated'];
				$agreement->finder_fee = $data['AgreementFinderFee'];
				$agreement->revenue_share = $data['AgreementRevenueShare'];
				$agreement->product_sms = $data['AgreementProductSMS'];
				$agreement->product_voice = $data['AgreementProductVoice'];
				$agreement->product_games = $data['AgreementProductGames'];
				$agreement->product_vg = $data['AgreementProductVG'];
				$agreement->product_others = $data['AgreementProductOthers'];
				$agreement->start_date = $data['AgreementStartDate'];
				$agreement->end_date = $data['AgreementEndDate'];

				$vasworld->agreement = $agreement;

				$vasworlds[] = $vasworld;
	        }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworlds;
		}

		/*
		public function get_published_vasworld($agreement_id)
		{
			$query = "SELECT pvw.ID as ID,
			 				 pvw.PartnerID as PartnerID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated
						FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID=?
					     AND pvw.Status=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $agreement_id, VASWorld::$STATUS_PUBLISHED);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworld = null;
            if( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworld;
		}
		*/

		public function get_published_vasworld_by_agreement_id($agreement_id)
		{
			$query = "SELECT pvw.ID as ID,
							 pvw.PartnerAgreementID as AgreementID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated
						FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID = ?
					   	 AND pvw.Status=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $agreement_id, VASWorld::$STATUS_PUBLISHED);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworld = null;
            if( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworld;
		}

		public function get_published_vasworlds($partner_id)
		{
			$query = "SELECT pvw.ID as ID,
			 				 pvw.PartnerAgreementID as AgreementID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated,

			 				 pa.PartnerID as PartnerID,
			 				 pa.Name as AgreementName,
			 				 pa.DateCreated as AgreementDateCreated,
			 				 pa.FinderFee as AgreementFinderFee,
			 				 pa.RevenueShare as AgreementRevenueShare,
			 				 pa.ProductSMS as AgreementProductSMS,
			 				 pa.ProductVoice as AgreementProductVoice,
			 				 pa.ProductGames as AgreementGames,
			 				 pa.ProductVG as AgreementProductVG,
			 				 pa.ProductOthers as AgreementProductOthers,
			 				 pa.StartDate as AgreementStartDate,
			 				 pa.EndDate as AgreementEndDate
						FROM partnervasworld pvw, partneragreement pa
					   WHERE pvw.PartnerAgreementID=pa.id
					     AND pa.PartnerID=?
					     AND pvw.Status=?";

            $stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $partner_id, VASWorld::$STATUS_PUBLISHED);
            $stmt->execute();

            $this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworlds = array();
            while( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);

				$agreement = new Agreement(array());
				$agreement->id = $data['AgreementID'];
				$agreement->partner_id = $data['PartnerID'];
				$agreement->name = $data['AgreementName'];
				$agreement->date_created = $data['AgreementDateCreated'];
				$agreement->finder_fee = $data['AgreementFinderFee'];
				$agreement->revenue_share = $data['AgreementRevenueShare'];
				$agreement->product_sms = $data['AgreementProductSMS'];
				$agreement->product_voice = $data['AgreementProductVoice'];
				$agreement->product_games = $data['AgreementProductGames'];
				$agreement->product_vg = $data['AgreementProductVG'];
				$agreement->product_others = $data['AgreementProductOthers'];
				$agreement->start_date = $data['AgreementStartDate'];
				$agreement->end_date = $data['AgreementEndDate'];

				$vasworld->agreement = $agreement;

				$vasworlds[] = $vasworld;
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworlds;
		}

		public function save_vasworld_draft($agreement_id, $vasworld_id, $content, $name)
		{
			$query = "UPDATE partnervasworld pvw
						 SET pvw.Content = ?,
						 	 pvw.Name = ?,
						 	 pvw.DateUpdated=NOW()
                       WHERE pvw.PartnerAgreementID = ?
                         AND pvw.ID = ?
                       	 AND pvw.Status = ?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ssiii", $content, $name, $agreement_id, $vasworld_id, VASWorld::$STATUS_DRAFT);
            $stmt->execute();

            $stmt->close();
            $this->closeMasterConnection();
		}

		public function submit_vasworld_draft($agreement_id, $vasworld_id)
		{
			// Cancel all the pending approval drafts
			$query = "UPDATE partnervasworld pvw SET Status=?, Remarks='New submission', DateUpdated=NOW() WHERE PartnerAgreementID=? AND ID=? AND Status=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iiii", VASWorld::$STATUS_CANCELLED, $agreement_id, $vasworld_id, VASWorld::$STATUS_PENDING);
            $stmt->execute();

			// Submit for approval
			$query = "UPDATE partnervasworld pvw SET Status=?, DateUpdated=NOW() WHERE PartnerAgreementID=? AND ID=? AND Status=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iiii", VASWorld::$STATUS_PENDING, $agreement_id, $vasworld_id, VASWorld::$STATUS_DRAFT);
            $stmt->execute();

			// Insert a new draft
			$query = "INSERT INTO partnervasworld (PartnerAgreementID, Name, Content, Status, DateCreated, DateUpdated)
						   VALUES (?, '(optional)', ?,?,NOW(), NOW())";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("isi", $agreement_id, $content, VASWorld::$STATUS_DRAFT);
            $stmt->execute();

            $stmt->close();
            $this->closeMasterConnection();
		}

		public function get_vasworlds_by_partner_id($partner_id, $include_draft=false)
		{
			$agreement_id = $this->get_first_agreement($partner_id);

			$query = "SELECT pvw.ID as ID,
							 pvw.PartnerAgreementID as AgreementID,
							 pvw.Name as Name,
							 pvw.Content as Content,
							 pvw.Status as Status,
							 pvw.Remarks as Remarks,
							 pvw.DateCreated as DateCreated,
							 pvw.DateUpdated as DateUpdated
					    FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID=?";

			if(!$include_draft)
				$query .= " AND pvw.Status != " . VASWorld::$STATUS_DRAFT;

			$query .= " ORDER BY pvw.DateUpdated DESC";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $agreement_id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworlds = null;
            while( $stmt->fetch() )
            {
				$vasworlds[] = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return array('vasworlds'=>$vasworlds, 'agreement_id'=>$agreement_id);
		}

		private function get_first_agreement($partner_id)
		{
			$query = "SELECT id as AgreementID
			   			FROM partneragreement pa
					   WHERE pa.PartnerID=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $partner_id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreement_id = null;
            if( $stmt->fetch() )
            {
				$agreement_id = $data['AgreementID'];
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $agreement_id;
		}

		public function get_vasworlds_by_agreement_id($agreement_id, $include_draft=false)
		{
			$query = "SELECT pvw.ID as ID,
							 pvw.PartnerAgreementID as AgreementID,
							 pvw.Name as Name,
							 pvw.Content as Content,
							 pvw.Status as Status,
							 pvw.Remarks as Remarks,
							 pvw.DateCreated as DateCreated,
							 pvw.DateUpdated as DateUpdated
					    FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID=?";

			if(!$include_draft)
				$query .= " AND pvw.Status != " . VASWorld::$STATUS_DRAFT;

			$query .= " ORDER BY pvw.DateUpdated DESC";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $agreement_id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworlds = null;
            while( $stmt->fetch() )
            {
				$vasworlds[] = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworlds;
		}


		public function get_vasworld($agreement_id, $vasworld_id)
		{
			$query = "SELECT pvw.ID as ID,
			 				 pvw.PartnerAgreementID as AgreementID,
			 				 pvw.Name as Name,
			 				 pvw.Content as Content,
			 				 pvw.Status as Status,
			 				 pvw.Remarks as Remarks,
			 				 pvw.DateCreated as DateCreated,
							 pvw.DateUpdated as DateUpdated
						FROM partnervasworld pvw
					   WHERE pvw.PartnerAgreementID=?
					     AND pvw.ID=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $agreement_id, $vasworld_id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$vasworld = null;
            if( $stmt->fetch() )
            {
				$vasworld = new VASWorld($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $vasworld;
		}

		public function get_agreements($partner_id)
		{
			$query = "SELECT pa.ID as ID,
							 pa.PartnerID as PartnerID,
							 pa.Name as Name,
							 pa.FinderFee as FinderFee,
							 pa.RevenueShare as RevenueShare,
							 pa.ProductSMS as ProductSMS,
							 pa.ProductVoice as ProductVoice,
							 pa.ProductGames as ProductGames,
							 pa.ProductVG as ProductVG,
							 pa.ProductOthers as ProductOthers,
							 pa.DateCreated as DateCreated,
							 pa.StartDate as StartDate,
							 pa.EndDate as EndDate
					    FROM partneragreement pa
					   WHERE pa.PartnerID=?
					ORDER BY pa.DateCreated DESC";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $partner_id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreements = null;
            while( $stmt->fetch() )
            {
				$agreements[] = new Agreement($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $agreements;
		}

		public function get_agreement($agreement_id, $partner_id)
		{
			$query = "SELECT pa.ID as ID,
							 pa.PartnerID as PartnerID,
							 pa.Name as Name,
							 pa.FinderFee as FinderFee,
							 pa.RevenueShare as RevenueShare,
							 pa.ProductSMS as ProductSMS,
							 pa.ProductVoice as ProductVoice,
							 pa.ProductGames as ProductGames,
							 pa.ProductVG as ProductVG,
							 pa.ProductOthers as ProductOthers,
							 pa.DateCreated as DateCreated,
							 pa.StartDate as StartDate,
							 pa.EndDate as EndDate
					    FROM partneragreement pa
					   WHERE pa.ID=?
					     AND pa.PartnerID=?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("ii", $agreement_id, $partner_id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreement = null;
            if( $stmt->fetch() )
            {
				$agreement = new Agreement($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $agreement;
		}

		public function get_agreement_stats_weekly($agreement_id, $partner_id, $week, $year)
		{
			$query = "SELECT ps.ID as ID,
							 pa.PartnerID as PartnerID,
							 pa.ID as AgreementID,
							 ps.PartnerBuildID as PartnerBuildID,
							 CAST(SUM(ps.UniqueUsers) AS UNSIGNED INTEGER) as UniqueUsers,
							 CAST(SUM(ps.Registration) AS UNSIGNED INTEGER) as Registration,
							 CAST(SUM(ps.Authentication) AS UNSIGNED INTEGER) as Authentication,
							 CAST(SUM(ps.ActiveUsers) AS UNSIGNED INTEGER) as ActiveUsers,
							 CAST(SUM(ps.CreditSpending) AS UNSIGNED INTEGER) as CreditSpending,
							 ps.DateCreated as DateCreated,
							 DAYNAME(ps.DateCreated) as Day
					    FROM partnerstat ps,
					         partneragreementbuild pab,
					    	 partneragreement pa
					   WHERE pab.PartnerAgreementID=?
					     AND pa.ID = pab.PartnerAgreementID
					     AND pa.PartnerID=?
					     AND ps.PartnerBuildID=pab.PartnerBuildID
					     AND WEEK(ps.DateCreated,3)=?
					     AND YEAR(ps.DateCreated)=?
					GROUP BY ps.DateCreated, pab.PartnerAgreementID
					ORDER BY ps.DateCreated ASC";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iiii", $agreement_id, $partner_id, $week, $year);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreement_stats = null;
            while( $stmt->fetch() )
            {
				$agreement_stats[] = new AgreementStat($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $agreement_stats;
		}

		public function get_agreement_stats_monthly($agreement_id, $partner_id, $year)
		{
			$query = "SELECT ps.ID as ID,
							 pa.PartnerID as PartnerID,
							 pa.ID as AgreementID,
							 ps.PartnerBuildID as PartnerBuildID,
							 CAST(SUM(ps.UniqueUsers) AS UNSIGNED INTEGER) as UniqueUsers,
							 CAST(SUM(ps.Registration) AS UNSIGNED INTEGER) as Registration,
							 CAST(SUM(ps.Authentication) AS UNSIGNED INTEGER) as Authentication,
							 CAST(SUM(ps.ActiveUsers) AS UNSIGNED INTEGER) as ActiveUsers,
							 CAST(SUM(ps.CreditSpending) AS UNSIGNED INTEGER) as CreditSpending,
							 ps.DateCreated as DateCreated,
							 MONTH(ps.DateCreated) as Month,
							 YEAR(ps.DateCreated) as Year
					    FROM partnerstat ps,
					         partneragreementbuild pab,
					    	 partneragreement pa
					   WHERE pab.PartnerAgreementID = ?
					     AND pa.PartnerID = ?
					     AND pa.ID = pab.PartnerAgreementID
					     AND ps.PartnerBuildID = pab.PartnerBuildID
					     AND YEAR(ps.DateCreated) = ?
					GROUP BY MONTH(ps.DateCreated), pab.PartnerAgreementID
					ORDER BY ps.DateCreated ASC";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iii", $agreement_id, $partner_id, $year);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreement_stats = null;
            while( $stmt->fetch() )
            {
				$agreement_stats[] = new AgreementStatMonth($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $agreement_stats;
		}

		public function get_agreement_stats_quarterly($agreement_id, $partner_id, $num_of_entries=4, $page=1)
		{
			$query = "  SELECT year(ps.DateCreated) as Year,
  					  	       quarter(ps.DateCreated) as Quarter,
						  	   pa.PartnerID as PartnerID,
						  	   pa.ID as AgreementID,
						  	   pab.PartnerBuildID as BuildID,
							   CAST(SUM(ps.UniqueUsers) AS UNSIGNED INTEGER) as UniqueUsers,
							   CAST(SUM(ps.Registration) AS UNSIGNED INTEGER) as Registration,
							   CAST(SUM(ps.Authentication) AS UNSIGNED INTEGER) as Authentication,
							   CAST(SUM(ps.ActiveUsers) AS UNSIGNED INTEGER) as ActiveUsers,
							   CAST(SUM(ps.CreditSpending) AS UNSIGNED INTEGER) as CreditSpending
    					  FROM partnerstat ps,
    					  	   partneragreementbuild pab,
    					  	   partneragreement pa
   						 WHERE ps.PartnerBuildID = pab.PartnerBuildID
   						   AND pab.PartnerAgreementID = pa.ID
     					   AND pab.PartnerAgreementID = ?
     					   AND pa.PartnerID = ?
					  GROUP BY year(ps.DateCreated), quarter(ps.DateCreated)
					  ORDER BY ps.DateCreated DESC
					  LIMIT ?, ?";
			$offset = ($num_of_entries * ($page-1));
			$num_rec = $num_of_entries+1;

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("iiii", $agreement_id, $partner_id, $offset, $num_rec);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$agreement_stat_quarters = null;
            while( $stmt->fetch() )
            {
				$agreement_stat_quarters[] = new AgreementStatQuarter($data);
            }

            $stmt->close();
            $this->closeMasterConnection();

            $is_more = false;
            if (count($agreement_stat_quarters) > $num_of_entries)
			{
				array_pop($agreement_stat_quarters);
				$is_more = true;
			}


            return array('agreement_stat_quarters'=>$agreement_stat_quarters, 'is_more'=>$is_more);
		}

		// Parameter: (1) takes in the id from partnerbuild table
		// Returns DownloadUrl
		public function get_partner_download_url($id)
		{
			$query = "SELECT DownloadUrl
					    FROM partnerbuild
					   WHERE ID = ?";

			$stmt = $this->getMasterConnection()->get_prepared_statement($query);
            $stmt->bind_param("i", $id);
            $stmt->execute();

			$this->getMasterConnection()->stmt_bind_assoc($stmt, $data);

			$url = null;
            if( $stmt->fetch() ){
				$url= $data['DownloadUrl'];
            }

            $stmt->close();
            $this->closeMasterConnection();

            return $url;
		}
	}
?>