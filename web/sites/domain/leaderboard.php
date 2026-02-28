<?php
	fast_require("Redis", get_framework_common_directory() . "/redis.php");

	class LeaderboardDomain
	{
		// these flags turns off leaderboard
		const UI_ENABLED = true;
		const WRITES_ENABLED = true;
		const WRITES_ALL_TIME_ENABLED = false;


		/***
		 * DB Connection
		 *
		 **/

		public static $DB_NAME = "slave";


		/***
		 * Incoming parameters valid sets (for validation and translation)
		 *
		 **/

		const TYPE_GLOBAL = 0;
		const TYPE_FRIENDS = 1;

		const SEPERATOR = ':';

		public static $boards = array(
			'profilelikes'   => Redis::KEYSPACE_LB_USER_LIKES,
			'miglevel'       => Redis::KEYSPACE_LB_MIG_LEVEL,
			'referrer'       => Redis::KEYSPACE_LB_REFERRER,
			'giftsender'     => Redis::KEYSPACE_LB_GIFT_SENT,
			'giftreceiver'   => Redis::KEYSPACE_LB_GIFT_RECEIVED,
			'avatarvotes'    => Redis::KEYSPACE_LB_AVATAR_VOTES
		);

		public static $games = array(
			'lowcard' 	 	 => Redis::KEYSPACE_GAME_LOWCARD,
			'dice' 		 	 => Redis::KEYSPACE_GAME_DICE,
			'football' 	 	 => Redis::KEYSPACE_GAME_FOOTBALL,
			'guess' 	 	 => Redis::KEYSPACE_GAME_GUESS,
			'danger' 	 	 => Redis::KEYSPACE_GAME_DANGER,
			'migcricket' 	 => Redis::KEYSPACE_GAME_MIGCRICKET
		);

		public static $games_stats = array (
			'mostwins' 	 	 => Redis::KEYSPACE_LB_MOST_WINS,
			'gamesplayed' 	 => Redis::KEYSPACE_LB_GAMES_PLAYED,
			'avatarvotes'    => Redis::KEYSPACE_LB_AVATAR_VOTES,
			'paintpoints'    => Redis::KEYSPACE_LB_PAINTWARS_POINTS
		);

		public static $times = array(
			'daily'          => Redis::KEYSPACE_LB_DAILY,
			'weekly'         => Redis::KEYSPACE_LB_WEEKLY,
			'previousdaily'  => Redis::KEYSPACE_LB_PREVIOUS_DAILY,
			'previousweekly' => Redis::KEYSPACE_LB_PREVIOUS_WEEKLY,
			'alltime'        => Redis::KEYSPACE_LB_ALL_TIME
		);

		public static $types = array(
			'global'         => self::TYPE_GLOBAL,
			'friends'        => self::TYPE_FRIENDS
		);

		public static $strings = array();


		/***
		 * Memcached expiry
		 *
		 * Format: Day * Hour * Min * Secs
		 *
		 **/

		public static $EXPIRY_FRIENDS_LB       = 3600;  // 1 hour;
		public static $EXPIRY_FRIENDS_EMPTY_LB = 900;   // 15mn;
		public static $EXPIRY_LIST_OF_FRIENDS  = 21600; // 6 hours



		/***
		 * Default board
		 *
		 **/

		public static $DEFAULT_BOARD = array(
			'board' => 'profilelikes',
			'type' => 'friends',
			'time' => 'alltime'
		);

	}

	LeaderboardDomain::$strings = array(
			'global'  => _('All Users'),
			'friends' => _('My Friends'),
			'daily'   => _('Daily'),
			'weekly'  => _('Weekly'),
			'alltime' => _('All Time'),
			'mostwins' => _('Most Wins'),
			'gamesplayed' => _('Games Played'),
			'avatarvotes' => _('Avatar Votes'),
			'paintpoints' => _('Points')
	);
?>