<?php

function get_logo_url($raw_bankname){

	//The returned logo url
	global $logo_url;

	$logo['BBVA Banco Frances (017)'] = '../images/merch/logo/argentina_bbva.png';
	$logo['Raiffeisen Zentral Bank Osterreich A.G.'] = '../images/merch/logo/austria_raiffeisen_zentral_bank_osterreich_ag.png';
	$logo['Commonwealth Bank'] = '../images/merch/logo/australia_commonwealth_bank.png';
	$logo['Fortis Bank'] = '../images/merch/logo/belgium_fortis_bank.png';
	$logo['Raiffeisen Bank Bulgaria'] = '../images/merch/logo/bulgaria_raiffeisen_bank.png';
	$logo['Banco Bradesco S.A.'] = '../images/merch/logo/brazil_banco_bradesco.png';
	$logo['Bank of Montreal'] = '../images/merch/logo/canada_bank_of_montreal.png';
	$logo['Raiffeisenbank St. Gallen'] = '../images/merch/logo/switzerland_raiffeisenbank.png';
	$logo['Citibank Chili'] = '../images/merch/logo/chile_citibank.png';
	$logo['Banco Santander Santiago'] = '../images/merch/logo/chile_banco_santander_santiago.png';
	$logo['HSBC China / Hui Feng Ying Hang'] = '../images/merch/logo/china_hsbc.png';
	$logo['招商银行深圳深南中路支行'] = '../images/merch/logo/china_ccyh.png';
	$logo['Raiffeisen Bank a.s.'] = '../images/merch/logo/czech_republic_raiffeisen_bank.png';
	$logo['WestLB'] = '../images/merch/logo/germany_westlb.png';
	$logo['Postbank AG'] = '../images/merch/logo/germany_postbank_ag.png';
	$logo['Den Danske Bank'] = '../images/merch/logo/denmark_den_danske_bank.png';
	$logo['Eesti Uhispank'] = '../images/merch/logo/estonia_eesti_uhispank.png';
	$logo['SEB- Eesti Ãœhispank'] = '../images/merch/logo/estonia_seb_eesti_uhispank.png';
	$logo['Banco Bilbao Vizcaya Argentaria'] = '../images/merch/logo/spain_bbva.png';
	$logo['Nordea Bank'] = '../images/merch/logo/finland_nordea_bank.png';
	$logo['BNP Paribas'] = '../images/merch/logo/france_bnp_paribus.png';
	$logo['Barclays Bank'] = '../images/merch/logo/united_kingdom_barclays_bank.png';
	$logo['Citibank Greece'] = '../images/merch/logo/worldwide_citibank.png';
	$logo['Geniki Bank'] = '../images/merch/logo/greece_geniki_bank.png';
	$logo['HSBC Hong Kong'] = '../images/merch/logo/worldwide_hsbc.png';
	$logo['Raiffeisenbank Austria'] = '../images/merch/logo/worldwide_raiffeisenbank.png';
	$logo['Raiffeisen Bank Rt.'] = '../images/merch/logo/worldwide_raiffeisenbank.png';
	$logo['HSBC Indonesia'] = '../images/merch/logo/worldwide_hsbc.png';
	$logo['Allied Irish Bank'] = '../images/merch/logo/ireland_allied_irish_bank.png';
	$logo['HDFC Bank Ltd'] = '../images/merch/logo/india_hdfc_bank.png';
	$logo['ICICI Bank'] = '../images/merch/logo/india_icici_bank.png';
	$logo['Landsbanki Islands'] = '../images/merch/logo/iceland_landsbanki.png';
	$logo['San Paolo IMI'] = '../images/merch/logo/italy_san_paolo_imi.png';
	$logo['Sumitomo Mitsui Banking Corporation'] = '../images/merch/logo/japan_sumitomo_mitsui_bank.png';
	$logo['Korea Exchange Bank Cheongdam Station BR'] = '../images/merch/logo/korea_korea_exchange_bank.png';
	$logo['Korea First Bank'] = '../images/merch/logo/korea_korea_first_bank.png';
	$logo['Nordea Bank Lietuva'] = '../images/merch/logo/worldwide_nordea_bank.png';
	$logo['SEB Vilnius Bankas'] = '../images/merch/logo/lithuania_seb_vilnius_bankas.png';
	$logo['Fortis Banque Luxembourg S.A.'] = '../images/merch/logo/worldwide_fortis_bank.png';
	$logo['Rietumu Bank'] = '../images/merch/logo/latvia_rietumu_banka.png';
	$logo['Rietumu Banka'] = '../images/merch/logo/latvia_rietumu_banka.png';
	$logo['Banamex'] = '../images/merch/logo/mexico_banamex.png';
	$logo['Bancomer - BBVA'] = '../images/merch/logo/mexico_bancomer_bbva.png';
	$logo['RHB Bank Berhad'] = '../images/merch/logo/malaysia_rhb_bank.png';
	$logo['ABN Amro Bank N.V.'] = '../images/merch/logo/netherlands_abn_amro_bank_n.v.png';
	$logo['DnBNor'] = '../images/merch/logo/norway_dnbnor.png';
	$logo['ASB Bank'] = '../images/merch/logo/new_zealand_asb_bank.png';
	$logo['Citibank Philippines'] = '../images/merch/logo/worldwide_citibank.png';
	$logo['HSBC Philippines'] = '../images/merch/logo/worldwide_hsbc.png';
	$logo['ING Bank'] = '../images/merch/logo/poland_ing_bank.png';
	$logo['Banco Santander Totta SA'] = '../images/merch/logo/portugal_santander_totta.png';
	$logo['Raiffeisen Bank S.A.'] = '../images/merch/logo/worldwide_raiffeisenbank.png';
	$logo['ZAO Raifeissenbank Austria'] = '../images/merch/logo/worldwide_raiffeisenbank.png';
	$logo['Citibank Manama'] = '../images/merch/logo/worldwide_citibank.png';
	$logo['Nordea Bank AB'] = '../images/merch/logo/worldwide_nordea_bank.png';
	$logo['HSBC Singapore'] = '../images/merch/logo/worldwide_hsbc.png';
	$logo['Raiffeisen Krekova Banka d.d.'] = '../images/merch/logo/worldwide_raiffeisenbank.png';
	$logo['Tatra Banka'] = '../images/merch/logo/slovak_republic_tatra_banka.png';
	$logo['Citibank Thailand'] = '../images/merch/logo/worldwide_citibank.png';
	$logo['HSBC Thailand'] = '../images/merch/logo/worldwide_hsbc.png';
	$logo['Garanti Bank'] = '../images/merch/logo/turkey_garanti_bank.png';
	$logo['Bank Sinopac'] = '../images/merch/logo/taiwan_sinopac_bank.png';
	$logo['RZB Ukraine'] = '../images/merch/logo/ukraine_rzb_bank.png';
	$logo['Citibank USA'] = '../images/merch/logo/worldwide_citibank.png';
	$logo['La Salle Bank NA'] = '../images/merch/logo/united_states_la_salle_bank.png';
	$logo['ANZ Vietnam'] = '../images/merch/logo/vietnam_anz_bank.png';
	$logo['Citibank South Africa'] = '../images/merch/logo/worldwide_citibank.png';
	$logo['Absa Bank'] = '../images/merch/logo/south_africa_absa_bank.png';
	$logo['Standard Bank'] = '../images/merch/logo/south_africa_standard_bank.png';

	if($raw_bankname != ''){
		//Match raw bank name with the hashmap we have above
		if($logo[$raw_bankname] != ''){
			$logo_url = $logo[$raw_bankname];
		} else {
			//Default to generic bank logo
			//$logo_url = '../images/merch/logo/generic_bank.png';
		}

		//print 'Raw Bank Name:'.$raw_bankname.'<br>';
		//print 'Logo URL:'.$logo_url.'<br>';
	}
}

?>