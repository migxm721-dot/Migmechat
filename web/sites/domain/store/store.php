<?php
	fast_require("AvatarStoreitem", get_domain_directory() . "/store/avatar_storeitem.php");
	fast_require("VirtualGiftStoreitem", get_domain_directory() . "/store/virtual_gift_storeitem.php");
	fast_require("EmoticonPackStoreitem", get_domain_directory() . "/store/emoticon_pack_storeitem.php");
	fast_require("SuperEmoticonPackStoreitem", get_domain_directory() . "/store/super_emoticon_pack_storeitem.php");
	fast_require("ThemeStoreitem", get_domain_directory() . "/store/theme_storeitem.php");
	fast_require("StickerStoreitem", get_domain_directory() . "/store/sticker_storeitem.php");
	fast_require("StoreItem", get_domain_directory() . "/store/store_item.php");

	class Store
	{
		public function __construct()
		{

		}

		public function get_item($data)
		{
			$type = get_value_from_array("Type", $data, "integer", 0);

			if($type == StoreItem::$VIRTUALGIFT_TYPE)
			{
				return new VirtualGiftStoreItem($data);
			}
			else if($type == StoreItem::$AVATAR_TYPE)
			{
				return new AvatarStoreItem($data);
			}
			else if($type == StoreItem::$EMOTICON_TYPE)
			{
				return new EmoticonPackStoreItem($data);
			}
			else if($type == StoreItem::$SUPEREMOTICON_TYPE)
			{
				return new SuperEmoticonPackStoreItem($data);
			}
			else if($type == StoreItem::$THEME_TYPE)
			{
				return new ThemeStoreItem($data);
			}
			else if($type == StoreItem::$STICKER_TYPE)
			{
				return new StickerStoreitem($data);
			}
		}

		public function convert_store_data_fusion_to_sql(&$data)
		{
			$data['ID'] = $data['id'];
			$data['Name'] = $data['name'];
			$data['Description'] = $data['Description'];
			$data['Price'] = $data['localPrice'];
			$data['Currency'] = $data['localCurrency'];
			$data['NumAvailable'] = $data['numAvailable'];
			$data['NumSold'] = $data['numSold'];
			$data['Featured'] = $data['featured'];
			$data['CatalogImage'] = $data['catalogImage'];
			$data['PreviewImage'] = $data['previewImage'];
			$data['ExpiryDate'] = $data['expiryDate'];
			$data['DateListed'] = $data['dateListed'];
			$data['migLevelMin'] = $data['migLevelMin'];
			$data['ReferenceID'] = $data['referenceID'];
			$data['GroupID'] = $data['groupID'];
			$data['GroupName'] = $data['groupName'];
			$data['GroupMemberStatus'] = $data['isGroupMember'];
			
			switch ($data['type']) {
				case 'VIRTUAL_GIFT':
					$data['Type'] = StoreItem::$VIRTUALGIFT_TYPE;
					break;
				
				case 'AVATAR':
					$data['Type'] = StoreItem::$AVATAR_TYPE;
					break;

				case 'EMOTICON':
					$data['Type'] = StoreItem::$EMOTICON_TYPE;
					break;

				case 'SUPEREMOTICON':
				case 'SUPER_EMOTICON':
					$data['Type'] = StoreItem::$SUPEREMOTICON_TYPE;
					break;

				case 'THEME':
					$data['Type'] = StoreItem::$THEME_TYPE;
					break;

				case 'STICKER':
					$data['Type'] = StoreItem::$STICKER_TYPE;
					break;

				default:
					$data['Type'] = $data['type'];
					break;
			}

			switch ($data['status']) {
				case 'ACTIVE':
					$data['Status'] = 1;
					break;

				case 'INACTIVE':
					$data['Status'] = 0;
					break;	
				
				default:
					$data['Status'] = $data['status'];
					break;
			}
		}
	}
?>