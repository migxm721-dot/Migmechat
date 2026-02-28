-- MySQL dump 10.13  Distrib 5.5.31, for Linux (x86_64)
--
-- Host: localhost    Database: fusion
-- ------------------------------------------------------
-- Server version	5.5.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `_backup_emoticon_20140120`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `_backup_emoticon_20140120` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `EmoticonPackID` int(11) NOT NULL,
  `Type` int(11) NOT NULL,
  `Alias` varchar(128) NOT NULL DEFAULT '',
  `Width` int(11) NOT NULL DEFAULT '0',
  `Height` int(11) NOT NULL DEFAULT '0',
  `Location` varchar(128) NOT NULL,
  `LocationPNG` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_emoticon_1` (`EmoticonPackID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentry` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Type` int(11) NOT NULL DEFAULT '0',
  `Reference` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) NOT NULL DEFAULT '',
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `FundedAmount` double(12,2) NOT NULL DEFAULT '0.00',
  `Tax` double(12,2) NOT NULL DEFAULT '0.00',
  `CostOfGoodsSold` double(12,4) NOT NULL DEFAULT '0.0000',
  `CostOfTrial` double(12,4) NOT NULL DEFAULT '0.0000',
  `Currency` varchar(6) NOT NULL,
  `ExchangeRate` double(15,4) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `idx_AccountEntry1` (`Username`,`Type`,`Reference`),
  KEY `idx_AccountEntry2` (`Type`,`Reference`),
  KEY `idx_AccountEntry3` (`Type`,`DateCreated`,`Username`),
  KEY `FK_accountentry_1` (`Username`),
  KEY `FK_accountentry_2` (`Currency`),
  CONSTRAINT `accountentry_ibfk_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `accountentry_ibfk_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=1427282560 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentry_bak`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentry_bak` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Type` int(11) NOT NULL DEFAULT '0',
  `Reference` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) NOT NULL DEFAULT '',
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `FundedAmount` double(12,2) NOT NULL DEFAULT '0.00',
  `Tax` double(12,2) NOT NULL DEFAULT '0.00',
  `CostOfGoodsSold` double(12,4) NOT NULL DEFAULT '0.0000',
  `CostOfTrial` double(12,4) NOT NULL DEFAULT '0.0000',
  `Currency` varchar(6) NOT NULL,
  `ExchangeRate` double(15,4) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_accountentry_1` (`Username`),
  KEY `idx_AccountEntry1` (`Username`,`Type`,`Reference`),
  KEY `idx_AccountEntry2` (`Type`,`Reference`),
  KEY `FK_accountentry_2` (`Currency`),
  KEY `idx_AccountEntry3` (`Type`,`DateCreated`,`Username`),
  CONSTRAINT `FK_accountentry_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_accountentry_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=1427275612 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentry_rename_test_1`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentry_rename_test_1` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Type` int(11) NOT NULL DEFAULT '0',
  `Reference` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) NOT NULL DEFAULT '',
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `FundedAmount` double(12,2) NOT NULL DEFAULT '0.00',
  `Tax` double(12,2) NOT NULL DEFAULT '0.00',
  `CostOfGoodsSold` double(12,4) NOT NULL DEFAULT '0.0000',
  `CostOfTrial` double(12,4) NOT NULL DEFAULT '0.0000',
  `Currency` varchar(6) NOT NULL,
  `ExchangeRate` double(15,4) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `idx_AccountEntry1` (`Username`,`Type`,`Reference`),
  KEY `idx_AccountEntry2` (`Type`,`Reference`),
  KEY `idx_AccountEntry3` (`Type`,`DateCreated`,`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=1427269507 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentryreplayfailed201107`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentryreplayfailed201107` (
  `ID` int(11) DEFAULT NULL,
  `Username` varchar(128) DEFAULT '',
  `DateCreated` datetime DEFAULT '0000-00-00 00:00:00',
  `Type` int(11) DEFAULT '0',
  `Reference` varchar(128) DEFAULT '',
  `Description` varchar(128) DEFAULT '',
  `Amount` double(12,2) DEFAULT '0.00',
  `FundedAmount` double(12,2) DEFAULT '0.00',
  `Tax` double(12,2) DEFAULT '0.00',
  `CostOfGoodsSold` double(12,4) DEFAULT '0.0000',
  `CostOfTrial` double(12,4) DEFAULT '0.0000',
  `Currency` varchar(6) DEFAULT NULL,
  `ExchangeRate` double(15,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentrysource`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentrysource` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `AccountEntryID` bigint(11) NOT NULL,
  `IPAddress` varchar(128) DEFAULT NULL,
  `SessionID` varchar(128) DEFAULT NULL,
  `MobileDevice` varchar(128) DEFAULT NULL,
  `UserAgent` varchar(256) DEFAULT NULL,
  `IMEI` varchar(128) DEFAULT NULL,
  `MerchantUserID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_AccountEntrySource_1` (`UserAgent`(255))
) ENGINE=InnoDB AUTO_INCREMENT=964396650 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentrysource_bak`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentrysource_bak` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AccountEntryID` int(11) NOT NULL,
  `IPAddress` varchar(128) DEFAULT NULL,
  `SessionID` varchar(128) DEFAULT NULL,
  `MobileDevice` varchar(128) DEFAULT NULL,
  `UserAgent` varchar(256) DEFAULT NULL,
  `IMEI` varchar(128) DEFAULT NULL,
  `MerchantUserID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_AccountEntrySource_1` (`AccountEntryID`),
  KEY `FK_AccountEntrySource_2` (`MerchantUserID`),
  KEY `IDX_AccountEntrySource_1` (`UserAgent`(255)),
  CONSTRAINT `FK_AccountEntrySource_2` FOREIGN KEY (`MerchantUserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=964389702 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentrysource_for_test`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentrysource_for_test` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AccountEntryID` bigint(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_accountentry_for_test` (`AccountEntryID`),
  CONSTRAINT `FK_accountentry_for_test` FOREIGN KEY (`AccountEntryID`) REFERENCES `accountentry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `accountentrysource_rename_test_1`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accountentrysource_rename_test_1` (
  `ID` bigint(11) NOT NULL AUTO_INCREMENT,
  `AccountEntryID` bigint(11) NOT NULL,
  `IPAddress` varchar(128) DEFAULT NULL,
  `SessionID` varchar(128) DEFAULT NULL,
  `MobileDevice` varchar(128) DEFAULT NULL,
  `UserAgent` varchar(256) DEFAULT NULL,
  `IMEI` varchar(128) DEFAULT NULL,
  `MerchantUserID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_AccountEntrySource_1` (`UserAgent`(255)),
  KEY `FK_accountentry_for_rename` (`AccountEntryID`),
  CONSTRAINT `FK_accountentry_for_rename` FOREIGN KEY (`AccountEntryID`) REFERENCES `accountentry_rename_test_1` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=964382598 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MobilePhone` varchar(128) NOT NULL DEFAULT '',
  `IPAddress` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Activation_1` (`Username`),
  KEY `IDX_MobilePhone` (`MobilePhone`),
  CONSTRAINT `FK_Activation_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=48751550 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adultword`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adultword` (
  `Word` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`Word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `advancecashreceipt`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `advancecashreceipt` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DateCreated` datetime DEFAULT NULL,
  `EnteredBy` varchar(128) DEFAULT NULL,
  `DateReceived` datetime DEFAULT NULL,
  `AmountSent` decimal(10,2) DEFAULT NULL,
  `AmountCredited` decimal(10,2) DEFAULT NULL,
  `Type` int(11) DEFAULT NULL,
  `SenderUsername` varchar(128) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `ProviderTransactionID` varchar(128) NOT NULL,
  `PaymentDetails` varchar(128) NOT NULL,
  `Comment` text,
  `CashReceiptID` int(11) DEFAULT NULL,
  `ReviewedBy` varchar(128) DEFAULT NULL,
  `DateReviewed` datetime DEFAULT NULL,
  `Bonus` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SenderUsername` (`SenderUsername`),
  KEY `ProviderTransactionID` (`ProviderTransactionID`),
  KEY `FK_CashReceiptID` (`CashReceiptID`),
  CONSTRAINT `advancecashreceipt_ibfk_1` FOREIGN KEY (`SenderUsername`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `affiliate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `affiliate` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Password` varchar(128) NOT NULL DEFAULT '',
  `Code` varchar(128) NOT NULL DEFAULT '',
  `Commission` double(10,4) NOT NULL DEFAULT '0.0000',
  `LastLoginDate` datetime DEFAULT NULL,
  `ReferredBy` varchar(128) DEFAULT NULL,
  `Username` varchar(128) DEFAULT NULL,
  `EmailAddress` varchar(128) NOT NULL,
  `FirstName` varchar(128) NOT NULL,
  `LastName` varchar(128) NOT NULL,
  `AdditionalInfo` text,
  `CountryIdDetected` int(11) DEFAULT NULL,
  `RegistrationIpAddress` varchar(128) DEFAULT NULL,
  `DateRegistered` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Username` (`Username`),
  CONSTRAINT `FK_affiliate` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=482787 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `affiliatereferral`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `affiliatereferral` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AffiliateID` int(11) NOT NULL DEFAULT '0',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MobilePhone` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_AffiliateReferral_1` (`AffiliateID`),
  CONSTRAINT `FK_AffiliateReferral_1` FOREIGN KEY (`AffiliateID`) REFERENCES `affiliate` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alertmessage`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alertmessage` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CountryID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `StartDate` datetime DEFAULT NULL,
  `ExpiryDate` datetime DEFAULT NULL,
  `Type` int(11) NOT NULL,
  `OnceOnly` int(11) NOT NULL DEFAULT '0',
  `Weighting` double(10,2) NOT NULL,
  `MinMidletVersion` int(11) NOT NULL,
  `MaxMidletVersion` int(11) NOT NULL,
  `ContentType` int(11) NOT NULL,
  `Content` text NOT NULL,
  `URL` text,
  `Status` int(11) NOT NULL DEFAULT '0',
  `Category` int(11) DEFAULT NULL,
  `clientType` int(4) NOT NULL DEFAULT '2',
  PRIMARY KEY (`ID`),
  KEY `FK_alertmessage_1` (`CountryID`),
  CONSTRAINT `FK_alertmessage_1` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4291 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alertmessage_archive`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alertmessage_archive` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `CountryID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `StartDate` datetime DEFAULT NULL,
  `ExpiryDate` datetime DEFAULT NULL,
  `Type` int(11) NOT NULL,
  `OnceOnly` int(11) NOT NULL DEFAULT '0',
  `Weighting` double(10,2) NOT NULL,
  `MinMidletVersion` int(11) NOT NULL,
  `MaxMidletVersion` int(11) NOT NULL,
  `ContentType` int(11) NOT NULL,
  `Content` text NOT NULL,
  `URL` text,
  `Status` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alertmessage_orig`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alertmessage_orig` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CountryID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `StartDate` datetime DEFAULT NULL,
  `ExpiryDate` datetime DEFAULT NULL,
  `Type` int(11) NOT NULL,
  `OnceOnly` int(11) NOT NULL DEFAULT '0',
  `Weighting` double(10,2) NOT NULL,
  `MinMidletVersion` int(11) NOT NULL,
  `MaxMidletVersion` int(11) NOT NULL,
  `ContentType` int(11) NOT NULL,
  `Content` text NOT NULL,
  `URL` text,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_alertmessage_1` (`CountryID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alias`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alias` (
  `username` varchar(128) NOT NULL DEFAULT '',
  `alias` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `allowlist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allowlist` (
  `Username` varchar(128) NOT NULL DEFAULT '',
  `AllowUsername` varchar(128) NOT NULL DEFAULT '',
  `PendingApproval` int(11) NOT NULL,
  PRIMARY KEY (`Username`,`AllowUsername`),
  KEY `FK_allowlist_2` (`AllowUsername`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appliedbonus`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appliedbonus` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `BonusTierID` int(11) NOT NULL DEFAULT '0',
  `AccountEntryID` int(11) NOT NULL DEFAULT '0',
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(6) NOT NULL DEFAULT '',
  `ExchangeRate` double(15,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`ID`),
  KEY `FK_AppliedBonus_1` (`Username`),
  KEY `FK_AppliedBonus_2` (`BonusTierID`),
  KEY `FK_AppliedBonus_3` (`AccountEntryID`),
  KEY `FK_AppliedBonus_4` (`Currency`),
  CONSTRAINT `FK_AppliedBonus_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_AppliedBonus_2` FOREIGN KEY (`BonusTierID`) REFERENCES `bonustier` (`ID`),
  CONSTRAINT `FK_AppliedBonus_4` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applieddiscount`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `applieddiscount` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DiscountTierID` int(11) NOT NULL DEFAULT '0',
  `AccountEntryID` int(11) NOT NULL DEFAULT '0',
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(6) NOT NULL DEFAULT '',
  `ExchangeRate` double(15,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`ID`),
  KEY `FK_AppliedDiscount_1` (`Username`),
  KEY `FK_AppliedDiscount_2` (`DiscountTierID`),
  KEY `FK_AppliedDiscount_3` (`AccountEntryID`),
  KEY `FK_AppliedDiscount_4` (`Currency`),
  CONSTRAINT `FK_AppliedDiscount_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_AppliedDiscount_2` FOREIGN KEY (`DiscountTierID`) REFERENCES `discounttier` (`ID`),
  CONSTRAINT `FK_AppliedDiscount_4` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appmenu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appmenu` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ClientType` int(4) NOT NULL,
  `MinVersion` int(11) NOT NULL,
  `MaxVersion` int(11) NOT NULL,
  `VASTrackingId` varchar(20) NOT NULL,
  `Status` int(4) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appmenuoption`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appmenuoption` (
  `ID` int(11) NOT NULL,
  `Position` int(11) NOT NULL,
  `TextId` int(11) NOT NULL,
  `IconURL` varchar(128) NOT NULL,
  `ActionURL` varchar(128) NOT NULL,
  PRIMARY KEY (`ID`,`Position`),
  KEY `ID` (`ID`),
  CONSTRAINT `ID` FOREIGN KEY (`ID`) REFERENCES `appmenu` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `authenticatedaccesscontrol`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authenticatedaccesscontrol` (
  `Name` varchar(128) NOT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `MobileVerifiedAllowed` int(11) NOT NULL DEFAULT '0',
  `EmailVerifiedAllowed` int(11) NOT NULL DEFAULT '0',
  `MobileVerifiedRateLimit` varchar(128) DEFAULT NULL,
  `EmailVerifiedRateLimit` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarbody`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarbody` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) DEFAULT NULL,
  `PreviewImage` varchar(128) NOT NULL,
  `Image` varchar(128) NOT NULL,
  `HeadX` int(11) NOT NULL DEFAULT '0',
  `HeadY` int(11) NOT NULL DEFAULT '0',
  `HeadWidth` int(11) NOT NULL DEFAULT '0',
  `HeadHeight` int(11) NOT NULL DEFAULT '0',
  `Gender` char(1) NOT NULL DEFAULT 'M',
  `Status` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarbodyitem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarbodyitem` (
  `AvatarBodyID` int(11) NOT NULL DEFAULT '0',
  `AvatarItemID` int(11) NOT NULL DEFAULT '0',
  KEY `AvatarBodyID` (`AvatarBodyID`),
  KEY `AvatarItemID` (`AvatarItemID`),
  CONSTRAINT `avatarbodyitem_ibfk_1` FOREIGN KEY (`AvatarBodyID`) REFERENCES `avatarbody` (`id`),
  CONSTRAINT `avatarbodyitem_ibfk_2` FOREIGN KEY (`AvatarItemID`) REFERENCES `avataritem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarcomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarcomment` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AvatarUserID` int(10) unsigned NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Comment` varchar(4000) NOT NULL,
  `NumLikes` int(11) NOT NULL DEFAULT '0',
  `NumDislikes` int(11) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_avatarcomment_avataruserid` (`AvatarUserID`),
  KEY `FK_avatarcomment_userid` (`UserID`),
  CONSTRAINT `FK_avatarcomment_avataruserid` FOREIGN KEY (`AvatarUserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_avatarcomment_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarcommentlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarcommentlike` (
  `AvatarCommentID` int(10) unsigned NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`AvatarCommentID`,`UserID`),
  KEY `FK_avatarcommentlike_userid` (`UserID`),
  CONSTRAINT `FK_avatarcommentlike_avatarcommentid` FOREIGN KEY (`AvatarCommentID`) REFERENCES `avatarcomment` (`ID`),
  CONSTRAINT `FK_avatarcommentlike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avataritem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avataritem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) NOT NULL DEFAULT '',
  `PreviewImage` varchar(128) NOT NULL,
  `Image` varchar(128) NOT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `zOrder` int(11) NOT NULL DEFAULT '0',
  `CategoryID` int(11) NOT NULL DEFAULT '0',
  `UsedOnBody` int(1) NOT NULL DEFAULT '0',
  `Status` int(1) NOT NULL DEFAULT '0',
  `OwnershipRequired` int(1) NOT NULL DEFAULT '1',
  `DateListed` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `CategoryID` (`CategoryID`),
  CONSTRAINT `avataritem_ibfk_1` FOREIGN KEY (`CategoryID`) REFERENCES `avataritemcategory` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3863 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avataritemcategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avataritemcategory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) NOT NULL,
  `AvatarItemCategoryID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Category_ID` (`AvatarItemCategoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarrating`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarrating` (
  `AvatarUserID` int(10) unsigned NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Rating` tinyint(2) NOT NULL,
  PRIMARY KEY (`AvatarUserID`,`UserID`),
  KEY `FK_avatarrating_userid` (`UserID`),
  CONSTRAINT `FK_avatarrating_avataruserid` FOREIGN KEY (`AvatarUserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_avatarrating_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarratingsummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarratingsummary` (
  `AvatarUserID` int(10) unsigned NOT NULL,
  `Average` double(7,5) unsigned NOT NULL DEFAULT '0.00000',
  `Total` int(11) NOT NULL DEFAULT '0',
  `NumRatings` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`AvatarUserID`),
  CONSTRAINT `FK_avatarratingsummary_avataruserid` FOREIGN KEY (`AvatarUserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarset`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarset` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(56) NOT NULL,
  `Description` varchar(128) DEFAULT NULL,
  `OwnershipRequired` int(1) NOT NULL DEFAULT '1',
  `Status` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatarsetitem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avatarsetitem` (
  `AvatarSetID` int(11) NOT NULL,
  `AvatarItemID` int(11) NOT NULL,
  KEY `avatarsetitem_ibfk_2` (`AvatarItemID`),
  KEY `avatarsetitem_ibfk_1` (`AvatarSetID`),
  CONSTRAINT `avatarsetitem_ibfk_1` FOREIGN KEY (`AvatarSetID`) REFERENCES `avatarset` (`ID`),
  CONSTRAINT `avatarsetitem_ibfk_2` FOREIGN KEY (`AvatarItemID`) REFERENCES `avataritem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avataruserbody`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avataruserbody` (
  `UserID` int(10) unsigned NOT NULL DEFAULT '0',
  `AvatarBodyID` int(11) NOT NULL,
  `Used` int(1) NOT NULL DEFAULT '0',
  `BodyUUID` varchar(32) DEFAULT '',
  `HeadUUID` varchar(32) DEFAULT '',
  KEY `AvatarBodyID` (`AvatarBodyID`),
  KEY `UserID` (`UserID`),
  CONSTRAINT `avataruserbody_ibfk_1` FOREIGN KEY (`AvatarBodyID`) REFERENCES `avatarbody` (`id`),
  CONSTRAINT `avataruserbody_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avataruseritem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avataruseritem` (
  `UserID` int(10) unsigned NOT NULL DEFAULT '0',
  `AvatarItemID` int(11) NOT NULL DEFAULT '0',
  `Used` int(1) NOT NULL DEFAULT '0',
  KEY `UserID` (`UserID`),
  KEY `AvatarItemID` (`AvatarItemID`),
  CONSTRAINT `avataruseritem_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `avataruseritem_ibfk_2` FOREIGN KEY (`AvatarItemID`) REFERENCES `avataritem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avataruserset`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `avataruserset` (
  `UserID` int(10) unsigned NOT NULL,
  `AvatarSetID` int(11) NOT NULL,
  KEY `avataruserset_ibfk_1` (`UserID`),
  KEY `avataruserset_ibfk_2` (`AvatarSetID`),
  CONSTRAINT `avataruserset_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `avataruserset_ibfk_2` FOREIGN KEY (`AvatarSetID`) REFERENCES `avatarset` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `badge`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `badge` (
  `ID` int(11) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Description` varchar(128) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `badgereward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `badgereward` (
  `RewardProgramID` int(11) NOT NULL,
  `BadgeID` int(11) NOT NULL,
  PRIMARY KEY (`RewardProgramID`,`BadgeID`),
  KEY `FK_BadgeReward_RewardProgramID` (`RewardProgramID`),
  KEY `FK_BadgeReward_BadgeID` (`BadgeID`),
  CONSTRAINT `FK_BadgeReward_BadgeID` FOREIGN KEY (`BadgeID`) REFERENCES `badge` (`ID`),
  CONSTRAINT `FK_BadgeReward_RewardProgramID` FOREIGN KEY (`RewardProgramID`) REFERENCES `rewardprogram` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `badgerewarded`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `badgerewarded` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RewardProgramCompletedID` int(11) NOT NULL,
  `BadgeID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BadgeRewarded_RewardProgramCompletedID` (`RewardProgramCompletedID`),
  KEY `FK_BadgeRewarded_BadgeID` (`BadgeID`),
  CONSTRAINT `FK_BadgeRewarded_BadgeID` FOREIGN KEY (`BadgeID`) REFERENCES `badge` (`ID`),
  CONSTRAINT `FK_BadgeRewarded_RewardProgramCompletedID` FOREIGN KEY (`RewardProgramCompletedID`) REFERENCES `rewardprogramcompleted` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4781547 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `banktransferintent`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banktransferintent` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL,
  `CountryID` int(11) NOT NULL DEFAULT '0',
  `PaymentProductID` int(11) NOT NULL DEFAULT '0',
  `Surname` varchar(128) DEFAULT NULL,
  `FiscalNumber` varchar(128) DEFAULT NULL,
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(4) NOT NULL,
  `CountryDescription` varchar(128) DEFAULT NULL,
  `StatusID` int(11) DEFAULT NULL,
  `AdditionalReference` varchar(128) DEFAULT NULL,
  `AccountHolder` varchar(128) DEFAULT NULL,
  `BankName` varchar(128) DEFAULT NULL,
  `ExternalReference` varchar(128) DEFAULT NULL,
  `EffortID` int(11) DEFAULT NULL,
  `PaymentReference` varchar(128) DEFAULT NULL,
  `AttemptID` int(11) DEFAULT NULL,
  `MerchantID` int(11) DEFAULT NULL,
  `BankAccountNumber` varchar(128) DEFAULT NULL,
  `StatusDate` varchar(128) DEFAULT NULL,
  `City` varchar(128) DEFAULT NULL,
  `OrderID` int(11) DEFAULT NULL,
  `SpecialID` varchar(128) DEFAULT NULL,
  `SwiftCode` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_banktransferintent_1` (`Username`),
  KEY `FK_banktransferintent_2` (`CountryID`),
  KEY `IDX_banktransferintent_1` (`PaymentReference`),
  KEY `FK_banktransferintent_3` (`Currency`),
  CONSTRAINT `FK_banktransferintent_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_banktransferintent_2` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_banktransferintent_3` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `banktransferreceived`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banktransferreceived` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `BankTransferIntentID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `FileName` varchar(128) NOT NULL,
  `Row` int(11) NOT NULL,
  `PaymentReference` varchar(128) DEFAULT NULL,
  `InvoiceNumber` varchar(128) DEFAULT NULL,
  `CustomerID` varchar(128) DEFAULT NULL,
  `AdditionalReference` varchar(128) DEFAULT NULL,
  `EffortNumber` int(11) DEFAULT NULL,
  `InvoiceCurrencyDeliv` varchar(128) DEFAULT NULL,
  `InvoiceAmountDeliv` double(12,2) DEFAULT NULL,
  `InvoiceCurrencyLocal` varchar(128) DEFAULT NULL,
  `InvoiceAmountLocal` double(12,2) DEFAULT NULL,
  `PaymentMethod` varchar(128) DEFAULT NULL,
  `CreditCardCompany` varchar(128) DEFAULT NULL,
  `UncleanIndicator` varchar(128) DEFAULT NULL,
  `PaymentCurrency` varchar(128) DEFAULT NULL,
  `PaymentAmount` double(12,2) DEFAULT NULL,
  `CurrencyDue` varchar(128) DEFAULT NULL,
  `AmountDue` double(12,2) DEFAULT NULL,
  `DateDue` int(11) DEFAULT NULL,
  `ReversalCurrency` varchar(128) DEFAULT NULL,
  `ReversalAmount` double(12,2) DEFAULT NULL,
  `ReversalReasonID` varchar(128) DEFAULT NULL,
  `ReversalReasonDescription` varchar(128) DEFAULT NULL,
  `Datecollect` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BankTransferReceived_1` (`BankTransferIntentID`),
  KEY `IDX_BankTransferReceived_1` (`PaymentReference`),
  KEY `IDX_BankTransferReceived_2` (`FileName`),
  CONSTRAINT `FK_BankTransferReceived_1` FOREIGN KEY (`BankTransferIntentID`) REFERENCES `banktransferintent` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `banner`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banner` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `StartDate` datetime NOT NULL,
  `EndDate` datetime NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bannerassets`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bannerassets` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `BannerID` int(11) NOT NULL,
  `Platform` varchar(128) NOT NULL,
  `Placement` varchar(128) DEFAULT NULL,
  `Size` varchar(128) DEFAULT NULL,
  `ImageUrl` varchar(255) NOT NULL DEFAULT '',
  `Url` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_bannerassets_banner` (`BannerID`),
  CONSTRAINT `FK_bannerassets_banner` FOREIGN KEY (`BannerID`) REFERENCES `banner` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `blockedregistrationip`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blockedregistrationip` (
  `ip` varchar(128) NOT NULL,
  `Inserted` date DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`ip`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `blockedregistrationpassword`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blockedregistrationpassword` (
  `Password` varchar(128) NOT NULL,
  `Notes` text,
  PRIMARY KEY (`Password`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `blocklist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blocklist` (
  `Username` varchar(128) NOT NULL DEFAULT '',
  `BlockUsername` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`Username`,`BlockUsername`),
  KEY `FK_blocklist_2` (`BlockUsername`),
  CONSTRAINT `FK_blocklist_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_blocklist_2` FOREIGN KEY (`BlockUsername`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonusprogram`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonusprogram` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `InternalName` varchar(128) NOT NULL,
  `DisplayName` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonusprogramtier`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonusprogramtier` (
  `BonusProgramID` int(11) NOT NULL DEFAULT '0',
  `BonusTierID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`BonusProgramID`,`BonusTierID`),
  KEY `FK_BonusProgramTier_2` (`BonusTierID`),
  CONSTRAINT `FK_BonusProgramTier_1` FOREIGN KEY (`BonusProgramID`) REFERENCES `bonusprogram` (`ID`),
  CONSTRAINT `FK_BonusProgramTier_2` FOREIGN KEY (`BonusTierID`) REFERENCES `bonustier` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bonustier`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bonustier` (
  `ID` int(11) NOT NULL,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Type` int(11) NOT NULL DEFAULT '0',
  `ActualMin` double(12,2) NOT NULL DEFAULT '0.00',
  `DisplayMin` double(12,2) NOT NULL DEFAULT '0.00',
  `Max` double(12,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(6) NOT NULL DEFAULT '',
  `PercentageBonus` double(12,2) NOT NULL DEFAULT '0.00',
  `ApplyToCreditCard` int(11) NOT NULL DEFAULT '0',
  `ApplyToBankTransfer` int(11) NOT NULL DEFAULT '0',
  `ApplyToWesternUnion` int(11) NOT NULL DEFAULT '0',
  `ApplyToVoucher` int(11) NOT NULL DEFAULT '0',
  `ApplyToTT` int(11) NOT NULL DEFAULT '0',
  `StartDate` datetime DEFAULT NULL,
  `EndDate` datetime DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_BonusTier_1` (`Currency`),
  CONSTRAINT `FK_BonusTier_1` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bot`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bot` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Game` varchar(20) NOT NULL DEFAULT ' ',
  `DisplayName` varchar(20) NOT NULL,
  `Description` varchar(128) DEFAULT NULL,
  `CommandName` varchar(128) DEFAULT NULL,
  `ExecutableFileName` varchar(128) DEFAULT NULL,
  `LibraryPaths` varchar(400) DEFAULT NULL,
  `Type` int(11) NOT NULL DEFAULT '1',
  `Leaderboards` tinyint(1) DEFAULT '0',
  `EmoticonKeyList` varchar(512) DEFAULT NULL,
  `SortOrder` smallint(3) DEFAULT '0',
  `GroupID` int(11) DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `botconfig`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `botconfig` (
  `BotID` int(11) NOT NULL,
  `PropertyName` varchar(30) DEFAULT NULL,
  `PropertyValue` varchar(128) DEFAULT NULL,
  `Description` varchar(128) DEFAULT NULL,
  KEY `BotID` (`BotID`),
  CONSTRAINT `FK_botconfig_bot` FOREIGN KEY (`BotID`) REFERENCES `bot` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `botmessage`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `botmessage` (
  `BotID` int(11) DEFAULT '0',
  `MessageKey` varchar(30) DEFAULT NULL,
  `LanguageCode` varchar(3) DEFAULT NULL,
  `MessageValue` varchar(256) DEFAULT NULL,
  UNIQUE KEY `BotID` (`BotID`,`MessageKey`,`LanguageCode`),
  KEY `LanguageCode` (`LanguageCode`),
  CONSTRAINT `FK_botmessage_lang` FOREIGN KEY (`LanguageCode`) REFERENCES `language` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bouncedb`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bouncedb` (
  `emailaddress` varchar(255) NOT NULL DEFAULT '',
  `source` varchar(255) DEFAULT NULL,
  `status` varchar(18) DEFAULT NULL,
  `bounceType` varchar(32) DEFAULT NULL,
  `action` varchar(32) DEFAULT NULL,
  `timestamp` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`emailaddress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `broadcastlist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `broadcastlist` (
  `username` varchar(128) NOT NULL DEFAULT '',
  `broadcastUsername` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`username`,`broadcastUsername`) USING BTREE,
  KEY `FK_broadcastlist_2` (`broadcastUsername`),
  CONSTRAINT `FK_broadcastlist_1` FOREIGN KEY (`username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_broadcastlist_2` FOREIGN KEY (`broadcastUsername`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cashreceipt`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cashreceipt` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DateCreated` datetime DEFAULT NULL,
  `EnteredBy` varchar(128) DEFAULT NULL,
  `DateReceived` datetime DEFAULT NULL,
  `AmountSent` decimal(10,2) DEFAULT NULL,
  `AmountReceived` decimal(10,2) DEFAULT NULL,
  `AmountCredited` decimal(10,2) DEFAULT NULL,
  `Type` int(11) DEFAULT NULL,
  `MatchedBy` varchar(128) DEFAULT NULL,
  `DateMatched` datetime DEFAULT NULL,
  `SenderUsername` varchar(128) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `ProviderTransactionID` varchar(128) NOT NULL,
  `PaymentDetails` varchar(128) NOT NULL,
  `MobilePhone` varchar(128) DEFAULT NULL,
  `Comments` text,
  `ReferenceCashReceiptID` int(11) DEFAULT NULL,
  `Bonus` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SenderUsername` (`SenderUsername`),
  KEY `ProviderTransactionID` (`ProviderTransactionID`),
  KEY `IDX_ReferenceCashReceiptID` (`ReferenceCashReceiptID`) USING BTREE,
  CONSTRAINT `cashreceipt_ibfk_1` FOREIGN KEY (`SenderUsername`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cdr`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cdr` (
  `UniqueID` varchar(32) NOT NULL DEFAULT '',
  `UserField` varchar(255) NOT NULL DEFAULT '',
  `AccountCode` varchar(20) NOT NULL DEFAULT '',
  `Src` varchar(80) NOT NULL DEFAULT '',
  `Dst` varchar(80) NOT NULL DEFAULT '',
  `DContext` varchar(80) NOT NULL DEFAULT '',
  `ClID` varchar(80) NOT NULL DEFAULT '',
  `Channel` varchar(80) NOT NULL DEFAULT '',
  `DstChannel` varchar(80) NOT NULL DEFAULT '',
  `LastApp` varchar(80) NOT NULL DEFAULT '',
  `LastData` varchar(80) NOT NULL DEFAULT '',
  `CallDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Duration` int(11) NOT NULL DEFAULT '0',
  `BillSec` int(11) NOT NULL DEFAULT '0',
  `Disposition` varchar(45) NOT NULL DEFAULT '',
  `AMAFlags` int(11) NOT NULL DEFAULT '0',
  KEY `Index_CDR_1` (`UserField`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroom`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroom` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) DEFAULT NULL,
  `Type` int(11) NOT NULL DEFAULT '1',
  `Creator` varchar(128) DEFAULT NULL,
  `ChatRoomCategoryID` int(11) DEFAULT NULL,
  `PrimaryCountryID` int(11) DEFAULT NULL,
  `SecondaryCountryID` int(11) DEFAULT NULL,
  `LocationID` int(11) DEFAULT NULL,
  `GroupID` int(11) DEFAULT NULL,
  `BotID` int(11) DEFAULT NULL,
  `AdultOnly` int(11) NOT NULL DEFAULT '0',
  `MaximumSize` int(11) DEFAULT NULL,
  `UserOwned` tinyint(1) NOT NULL DEFAULT '0',
  `NewOwner` varchar(128) DEFAULT NULL,
  `AllowKicking` tinyint(1) NOT NULL DEFAULT '1',
  `AllowUserKeywords` tinyint(1) NOT NULL DEFAULT '0',
  `AllowBots` tinyint(1) NOT NULL DEFAULT '0',
  `Language` varchar(3) DEFAULT NULL,
  `DateCreated` datetime DEFAULT NULL,
  `DateLastAccessed` datetime DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `ChatRoomThemeID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID` (`ID`),
  UNIQUE KEY `Name` (`Name`),
  KEY `FK_chatroom_1` (`Creator`),
  KEY `Index_Chatroom_1` (`DateLastAccessed`),
  KEY `FK_chatroom_2` (`PrimaryCountryID`),
  KEY `FK_chatroom_3` (`SecondaryCountryID`),
  KEY `FK_chatroom_4` (`GroupID`),
  KEY `FK_chatroom_5` (`LocationID`),
  KEY `FK_ChatRoomCategory_2` (`ChatRoomCategoryID`),
  KEY `Language` (`Language`),
  KEY `NewOwner` (`NewOwner`),
  KEY `FK_chatroom_6` (`BotID`),
  CONSTRAINT `chatroom_ibfk_1` FOREIGN KEY (`Language`) REFERENCES `language` (`Code`),
  CONSTRAINT `chatroom_ibfk_2` FOREIGN KEY (`NewOwner`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_ChatRoomCategory_2` FOREIGN KEY (`ChatRoomCategoryID`) REFERENCES `chatroomcategory` (`ID`),
  CONSTRAINT `FK_chatroom_2` FOREIGN KEY (`PrimaryCountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_chatroom_3` FOREIGN KEY (`SecondaryCountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_chatroom_4` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_chatroom_5` FOREIGN KEY (`LocationID`) REFERENCES `location` (`ID`),
  CONSTRAINT `FK_chatroom_6` FOREIGN KEY (`BotID`) REFERENCES `bot` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7316807 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroombanneduser`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroombanneduser` (
  `ChatRoomID` int(11) unsigned NOT NULL,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime DEFAULT NULL,
  PRIMARY KEY (`ChatRoomID`,`Username`),
  KEY `Username` (`Username`),
  CONSTRAINT `chatroombanneduser_ibfk_1` FOREIGN KEY (`ChatRoomID`) REFERENCES `chatroom` (`ID`),
  CONSTRAINT `chatroombanneduser_ibfk_2` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroombookmark`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroombookmark` (
  `Username` varchar(128) NOT NULL,
  `ChatRoomName` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`Username`,`ChatRoomName`),
  KEY `idx_ChatRoom_1` (`DateCreated`),
  KEY `FK_ChatRoomBookmark_2` (`ChatRoomName`),
  CONSTRAINT `FK_ChatRoomBookmark_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_ChatRoomBookmark_2` FOREIGN KEY (`ChatRoomName`) REFERENCES `chatroom` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomcategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomcategory` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `GroupEventOnly` int(11) NOT NULL DEFAULT '0',
  `ParentChatRoomCategoryID` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_ChatRoomCategory` (`ParentChatRoomCategoryID`),
  CONSTRAINT `FK_ChatRoomCategory` FOREIGN KEY (`ParentChatRoomCategoryID`) REFERENCES `chatroomcategory` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomcategorylist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomcategorylist` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `refreshmethod` int(11) DEFAULT NULL,
  `maxMigLevel` int(11) DEFAULT NULL,
  `initiallycollapsed` int(11) DEFAULT NULL,
  `itemscanbedeleted` int(11) DEFAULT NULL,
  `orderindex` int(11) DEFAULT NULL,
  `refreshdisplaystring` varchar(128) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomemotelog`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomemotelog` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `instigator` varchar(128) NOT NULL,
  `target` varchar(128) DEFAULT NULL,
  `emote` varchar(128) NOT NULL,
  `chatroomid` int(11) NOT NULL,
  `groupid` int(11) DEFAULT NULL,
  `reasoncode` int(4) DEFAULT NULL,
  `datecreated` datetime NOT NULL,
  `parameters` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_datecreated_grp` (`datecreated`,`instigator`,`target`,`chatroomid`),
  KEY `idx_instigator_grp` (`instigator`,`datecreated`,`target`,`chatroomid`)
) ENGINE=InnoDB AUTO_INCREMENT=125091327 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomextradata`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomextradata` (
  `chatroomid` int(11) unsigned NOT NULL,
  `type` int(11) NOT NULL,
  `value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`chatroomid`,`type`),
  CONSTRAINT `FK_chatroomextradata_chatroomid` FOREIGN KEY (`chatroomid`) REFERENCES `chatroom` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomkeyword`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomkeyword` (
  `ChatRoomID` int(11) unsigned NOT NULL,
  `KeywordID` int(11) NOT NULL,
  PRIMARY KEY (`ChatRoomID`,`KeywordID`),
  KEY `FK_ChatRoomKeyword_1` (`KeywordID`),
  CONSTRAINT `FK_ChatRoomKeyword_1` FOREIGN KEY (`KeywordID`) REFERENCES `keyword` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroommoderator`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroommoderator` (
  `ChatRoomID` int(11) unsigned NOT NULL,
  `Username` varchar(128) NOT NULL,
  PRIMARY KEY (`ChatRoomID`,`Username`),
  KEY `Username` (`Username`),
  CONSTRAINT `chatroommoderator_ibfk_1` FOREIGN KEY (`ChatRoomID`) REFERENCES `chatroom` (`ID`),
  CONSTRAINT `chatroommoderator_ibfk_2` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomtheme`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomtheme` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(128) NOT NULL,
  `STATUS` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomthemeattribute`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomthemeattribute` (
  `ChatRoomThemeID` int(11) NOT NULL,
  `AttributeKey` varchar(128) NOT NULL,
  `AttributeValue` varchar(256) NOT NULL,
  PRIMARY KEY (`ChatRoomThemeID`,`AttributeKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chatroomtochatroomcategorylist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatroomtochatroomcategorylist` (
  `chatroomid` int(11) unsigned NOT NULL,
  `chatroomcategorylistid` int(11) unsigned NOT NULL,
  `orderindex` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`chatroomid`,`chatroomcategorylistid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chequepaymentintent`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chequepaymentintent` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `CountryID` int(11) NOT NULL,
  `PaymentProductID` int(11) NOT NULL,
  `Payer` varchar(128) DEFAULT NULL,
  `Amount` double(12,2) NOT NULL,
  `Currency` varchar(4) NOT NULL,
  `CountryDescription` varchar(128) DEFAULT NULL,
  `StatusID` int(11) DEFAULT NULL,
  `AdditionalReference` varchar(128) DEFAULT NULL,
  `ExternalReference` varchar(128) DEFAULT NULL,
  `EffortID` int(11) DEFAULT NULL,
  `ChequeAccountHolder` varchar(128) DEFAULT NULL,
  `PaymentReference` varchar(128) DEFAULT NULL,
  `AttemptID` int(11) DEFAULT NULL,
  `MerchantID` int(11) DEFAULT NULL,
  `StatusDate` varchar(128) DEFAULT NULL,
  `OrderID` int(11) DEFAULT NULL,
  `PostalAddress1` varchar(128) DEFAULT NULL,
  `PostalAddress2` varchar(128) DEFAULT NULL,
  `PostalAddress3` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ChequePaymentIntent_1` (`Username`),
  KEY `FK_ChequePaymentIntent_2` (`CountryID`),
  KEY `FK_ChequePaymentIntent_3` (`Currency`),
  CONSTRAINT `FK_ChequePaymentIntent_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_ChequePaymentIntent_2` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_ChequePaymentIntent_3` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clienttext`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clienttext` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Type` int(11) NOT NULL DEFAULT '0',
  `Description` varchar(128) NOT NULL DEFAULT '',
  `Text` text NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clientversion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clientversion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clientType` int(11) DEFAULT NULL,
  `clientVersion` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueClientVersions` (`clientType`,`clientVersion`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contact`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DisplayName` varchar(256) NOT NULL DEFAULT '',
  `FirstName` varchar(128) DEFAULT NULL,
  `LastName` varchar(128) DEFAULT NULL,
  `FusionUsername` varchar(128) DEFAULT NULL,
  `MSNUsername` varchar(128) DEFAULT NULL,
  `AIMUsername` varchar(128) DEFAULT NULL,
  `YahooUsername` varchar(128) DEFAULT NULL,
  `ICQUsername` varchar(128) DEFAULT NULL,
  `JabberUsername` varchar(128) DEFAULT NULL,
  `EmailAddress` varchar(128) DEFAULT NULL,
  `MobilePhone` varchar(128) DEFAULT NULL,
  `HomePhone` varchar(128) DEFAULT NULL,
  `OfficePhone` varchar(128) DEFAULT NULL,
  `DefaultIM` int(11) DEFAULT NULL,
  `DefaultPhoneNumber` int(11) DEFAULT NULL,
  `ContactGroupID` int(11) DEFAULT NULL,
  `ShareMobilePhone` int(11) DEFAULT NULL,
  `DisplayOnPhone` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_contact_1` (`Username`),
  KEY `FK_contact_2` (`FusionUsername`),
  KEY `FK_contact_3` (`ContactGroupID`),
  CONSTRAINT `FK_contact_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_contact_2` FOREIGN KEY (`FusionUsername`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_contact_3` FOREIGN KEY (`ContactGroupID`) REFERENCES `contactgroup` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1746485733 DEFAULT CHARSET=utf8 COMMENT='InnoDB free: 3072 kB; (`Username`) REFER `fusion/user`(`User';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contactdeleted`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contactdeleted` (
  `ID` int(11) NOT NULL,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DisplayName` varchar(256) NOT NULL DEFAULT '',
  `FirstName` varchar(128) DEFAULT NULL,
  `LastName` varchar(128) DEFAULT NULL,
  `FusionUsername` varchar(128) DEFAULT NULL,
  `MSNUsername` varchar(128) DEFAULT NULL,
  `AIMUsername` varchar(128) DEFAULT NULL,
  `YahooUsername` varchar(128) DEFAULT NULL,
  `ICQUsername` varchar(128) DEFAULT NULL,
  `JabberUsername` varchar(128) DEFAULT NULL,
  `EmailAddress` varchar(128) DEFAULT NULL,
  `MobilePhone` varchar(128) DEFAULT NULL,
  `HomePhone` varchar(128) DEFAULT NULL,
  `OfficePhone` varchar(128) DEFAULT NULL,
  `DefaultIM` int(11) DEFAULT NULL,
  `DefaultPhoneNumber` int(11) DEFAULT NULL,
  `ContactGroupID` int(11) DEFAULT NULL,
  `ShareMobilePhone` int(11) DEFAULT NULL,
  `DisplayOnPhone` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contactgroup`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contactgroup` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `Name` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_group_1` (`Username`),
  CONSTRAINT `FK_group_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=34612424 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contactlistversion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contactlistversion` (
  `UserID` int(10) unsigned NOT NULL,
  `Version` int(11) NOT NULL,
  PRIMARY KEY (`UserID`),
  CONSTRAINT `FK_ContactListVersion_1` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `content`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ContentCategoryID` int(11) DEFAULT NULL,
  `ContentProviderID` int(11) DEFAULT NULL,
  `ContentSupplierID` int(11) DEFAULT NULL,
  `Type` int(11) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Artist` varchar(128) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `Price` double NOT NULL,
  `Currency` varchar(6) NOT NULL,
  `WholesaleCost` double(10,6) NOT NULL,
  `WholesaleCostCurrency` varchar(6) NOT NULL,
  `Preview` varchar(128) DEFAULT NULL,
  `PreviewWidth` int(11) DEFAULT NULL,
  `PreviewHeight` int(11) DEFAULT NULL,
  `Thumbnail` varchar(128) DEFAULT NULL,
  `ProviderID` varchar(128) DEFAULT '',
  `GroupID` int(11) DEFAULT NULL,
  `GroupVIPOnly` tinyint(1) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `ContentCategoryID` (`ContentCategoryID`,`Status`),
  KEY `FK_ContentProviderID` (`ContentProviderID`),
  KEY `GroupID` (`GroupID`),
  KEY `ContentSupplierID` (`ContentSupplierID`),
  CONSTRAINT `content_ibfk_1` FOREIGN KEY (`ContentProviderID`) REFERENCES `contentprovider` (`ID`),
  CONSTRAINT `content_ibfk_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `content_ibfk_3` FOREIGN KEY (`ContentSupplierID`) REFERENCES `contentsupplier` (`ID`),
  CONSTRAINT `FK_content` FOREIGN KEY (`ContentCategoryID`) REFERENCES `contentcategory` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9269 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contentcategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contentcategory` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `ParentContentCategoryID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_contentcategory` (`ParentContentCategoryID`),
  CONSTRAINT `FK_contentcategory` FOREIGN KEY (`ParentContentCategoryID`) REFERENCES `contentcategory` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contentprovider`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contentprovider` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `OrderURL` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contentpurchased`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contentpurchased` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL,
  `MobilePhone` varchar(128) NOT NULL,
  `ContentID` int(11) NOT NULL,
  `ProviderContentID` varchar(128) NOT NULL,
  `ProviderTransactionID` varchar(128) DEFAULT NULL,
  `DownloadURL` varchar(128) DEFAULT NULL,
  `NumDownloads` int(11) NOT NULL DEFAULT '0',
  `Refunded` int(11) NOT NULL DEFAULT '0',
  `RefundReason` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `MobilePhone` (`MobilePhone`,`ProviderContentID`),
  KEY `FK_contentpurchased` (`ContentID`),
  KEY `username_contentid` (`Username`,`ContentID`),
  CONSTRAINT `FK_contentpurchased` FOREIGN KEY (`ContentID`) REFERENCES `content` (`ID`),
  CONSTRAINT `FK_contentpurchased_username` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contentsupplier`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contentsupplier` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `country`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `IDDCode` int(11) NOT NULL DEFAULT '0',
  `Currency` varchar(128) NOT NULL DEFAULT '',
  `CreditCardCurrency` varchar(6) NOT NULL DEFAULT '',
  `BankTransferCurrency` varchar(6) NOT NULL DEFAULT '',
  `WesternUnionCurrency` varchar(6) NOT NULL DEFAULT '',
  `ChequePaymentCurrency` varchar(6) NOT NULL,
  `WebMoneyCurrency` varchar(6) NOT NULL,
  `Tax` double(10,4) NOT NULL,
  `ActivationCredit` double(10,2) NOT NULL DEFAULT '0.00',
  `ReferralCredit` double(10,2) NOT NULL DEFAULT '0.00',
  `MinBalanceAfterTransfer` double(10,2) NOT NULL DEFAULT '0.00',
  `SMSCost` double(10,2) DEFAULT NULL,
  `CallRate` double(10,6) DEFAULT NULL,
  `CallSignallingFee` double(10,6) DEFAULT NULL,
  `MobileRate` double(10,6) DEFAULT NULL,
  `MobileSignallingFee` double(10,6) DEFAULT NULL,
  `CallThroughRate` double(10,6) DEFAULT NULL,
  `CallThroughSignallingFee` double(10,6) DEFAULT NULL,
  `SMSWholesaleCost` double(10,2) DEFAULT NULL,
  `CallWholesaleRate` double(10,6) DEFAULT NULL,
  `MobileWholesaleRate` double(10,6) DEFAULT NULL,
  `PremiumSMSAmount` double(10,2) DEFAULT NULL,
  `PremiumSMSFee` double(10,2) DEFAULT NULL,
  `UserBonusProgramID` int(11) DEFAULT NULL,
  `MerchantBonusProgramID` int(11) DEFAULT NULL,
  `AllowCreditCard` int(11) NOT NULL DEFAULT '0',
  `AllowBankTransfer` int(11) NOT NULL DEFAULT '0',
  `AllowWesternUnion` int(11) NOT NULL DEFAULT '0',
  `AllowChequePayment` int(11) NOT NULL DEFAULT '0',
  `AllowWebMoney` int(11) NOT NULL DEFAULT '0',
  `AllowPhoneCall` int(11) NOT NULL,
  `AllowEmail` int(11) NOT NULL DEFAULT '0',
  `AllowUserTransferToOtherCountry` int(11) NOT NULL DEFAULT '1',
  `AllowUserTransferWithinCountry` int(11) NOT NULL DEFAULT '1',
  `AllowUserTransferFromOtherCountry` int(11) NOT NULL DEFAULT '1',
  `Latitude` double(10,4) DEFAULT NULL,
  `Longitude` double(10,4) DEFAULT NULL,
  `ISOCountryCode` varchar(2) DEFAULT NULL,
  `ISOLanguageCode` varchar(2) DEFAULT NULL,
  `AllowZeroAfterIDDCode` int(11) NOT NULL DEFAULT '0',
  `Population` int(11) DEFAULT NULL,
  `LowASRDestination` int(11) NOT NULL DEFAULT '0',
  `CallRetries` int(11) NOT NULL DEFAULT '0',
  `SMSLookoutCost` double(10,2) DEFAULT NULL,
  `SMSBuzzCost` double(10,2) DEFAULT NULL,
  `SMSEmailAlertCost` double(10,2) DEFAULT NULL,
  `MinNonTopMerchantTagAmount` double(10,2) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_country_1` (`Currency`),
  KEY `FK_country_2` (`UserBonusProgramID`),
  KEY `FK_country_3` (`MerchantBonusProgramID`),
  KEY `FK_country_4` (`CreditCardCurrency`),
  KEY `FK_country_5` (`BankTransferCurrency`),
  KEY `FK_country_6` (`WesternUnionCurrency`),
  CONSTRAINT `FK_country_1` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_country_2` FOREIGN KEY (`UserBonusProgramID`) REFERENCES `bonusprogram` (`ID`),
  CONSTRAINT `FK_country_3` FOREIGN KEY (`MerchantBonusProgramID`) REFERENCES `bonusprogram` (`ID`),
  CONSTRAINT `FK_country_4` FOREIGN KEY (`CreditCardCurrency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_country_5` FOREIGN KEY (`BankTransferCurrency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_country_6` FOREIGN KEY (`WesternUnionCurrency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=249 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `countrysupportedhashtag`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `countrysupportedhashtag` (
  `countryid` int(11) NOT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`countryid`),
  CONSTRAINT `FK_countrysupportedhashtag_1` FOREIGN KEY (`countryid`) REFERENCES `country` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `countrytoip`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `countrytoip` (
  `BeginIPNum` double NOT NULL,
  `EndIPNum` double NOT NULL,
  `ISOCountryCode` varchar(128) DEFAULT NULL,
  `BeginIP` varchar(128) DEFAULT NULL,
  `EndIP` varchar(128) DEFAULT NULL,
  `NetMask` int(11) DEFAULT NULL,
  PRIMARY KEY (`BeginIPNum`,`EndIPNum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `credential`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credential` (
  `userid` int(10) unsigned NOT NULL,
  `username` varchar(128) NOT NULL,
  `Password` varchar(1000) NOT NULL,
  `passwordType` tinyint(3) unsigned NOT NULL,
  `version` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expires` datetime DEFAULT NULL,
  PRIMARY KEY (`userid`,`passwordType`),
  KEY `username` (`username`),
  KEY `passwordType` (`passwordType`),
  CONSTRAINT `FK_credential_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creditcardfraudcheck`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditcardfraudcheck` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CreditCardPaymentID` int(11) NOT NULL DEFAULT '0',
  `CountryMatch` varchar(128) DEFAULT '',
  `CountryCode` varchar(128) DEFAULT '',
  `HighRiskCountry` varchar(128) DEFAULT '',
  `Distance` int(11) DEFAULT '0',
  `IP_Address` varchar(128) NOT NULL,
  `IP_Region` varchar(128) DEFAULT '',
  `IP_City` varchar(128) DEFAULT '',
  `IP_Latitude` decimal(10,4) DEFAULT '0.0000',
  `IP_Longitude` decimal(10,4) DEFAULT '0.0000',
  `IP_ISP` varchar(128) DEFAULT '',
  `IP_Org` varchar(128) DEFAULT '',
  `AnonymousProxy` varchar(128) DEFAULT '',
  `ProxyScore` decimal(10,4) DEFAULT '0.0000',
  `BinMatch` varchar(128) DEFAULT '',
  `BinCountry` varchar(128) DEFAULT '',
  `BinName` varchar(128) DEFAULT '',
  `BinPhone` varchar(128) DEFAULT '',
  `Score` decimal(10,4) DEFAULT '0.0000',
  `MaxMindID` varchar(128) DEFAULT '',
  `Err` varchar(128) DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_CreditCardFraudCheck_1` (`CreditCardPaymentID`),
  CONSTRAINT `FK_CreditCardFraudCheck_1` FOREIGN KEY (`CreditCardPaymentID`) REFERENCES `creditcardpayment` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creditcardpayment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditcardpayment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Source` int(11) DEFAULT NULL,
  `IPAddress` varchar(128) DEFAULT NULL,
  `CardType` int(11) DEFAULT NULL,
  `CardNumber` varchar(128) DEFAULT NULL,
  `EncryptedCardNumber` varchar(256) DEFAULT NULL,
  `CheckNumber` varchar(256) DEFAULT NULL,
  `CardHolder` varchar(192) DEFAULT NULL,
  `CardExpiryDate` varchar(128) DEFAULT NULL,
  `CardVerificationNumber` varchar(128) DEFAULT NULL,
  `Amount` double(10,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(6) NOT NULL DEFAULT '',
  `ExchangeRate` double(15,4) NOT NULL DEFAULT '0.0000',
  `ProviderTransactionID` varchar(128) DEFAULT NULL,
  `ResponseCode` varchar(128) DEFAULT NULL,
  `ChargeBackDate` datetime DEFAULT NULL,
  `ChargeBackReasonCode` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `AutoApprove` int(11) DEFAULT '0',
  `details` blob,
  PRIMARY KEY (`ID`),
  KEY `FK_creditcardpayment_1` (`Username`),
  KEY `FK_creditcardpayment_2` (`Currency`),
  CONSTRAINT `FK_creditcardpayment_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_creditcardpayment_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=433153 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creditcardpaymentold`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditcardpaymentold` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Source` int(11) DEFAULT NULL,
  `IPAddress` varchar(128) DEFAULT NULL,
  `CardType` int(11) DEFAULT NULL,
  `CardNumber` varchar(128) DEFAULT NULL,
  `CardHolder` varchar(128) DEFAULT NULL,
  `CardExpiryDate` varchar(128) DEFAULT NULL,
  `CardVerificationNumber` varchar(128) DEFAULT NULL,
  `Amount` double(10,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(6) NOT NULL DEFAULT '',
  `ExchangeRate` double(15,4) NOT NULL DEFAULT '0.0000',
  `ProviderTransactionID` varchar(128) DEFAULT NULL,
  `ResponseCode` varchar(128) DEFAULT NULL,
  `ChargeBackDate` datetime DEFAULT NULL,
  `ChargeBackReasonCode` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `creditcardwhitelist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creditcardwhitelist` (
  `Username` varchar(128) NOT NULL,
  `CheckNumber` varchar(256) DEFAULT NULL,
  `Notes` text NOT NULL,
  PRIMARY KEY (`Username`),
  CONSTRAINT `FK_CreditCardWhiteList_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `currency`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency` (
  `Code` varchar(6) NOT NULL,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Symbol` varchar(6) DEFAULT NULL,
  `ExchangeRate` double(15,4) NOT NULL DEFAULT '0.0000',
  `LastUpdated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dailyleveldistribution`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dailyleveldistribution` (
  `runDate` datetime NOT NULL,
  `l1` int(10) unsigned NOT NULL DEFAULT '0',
  `l2` int(10) unsigned NOT NULL DEFAULT '0',
  `l3` int(10) unsigned NOT NULL DEFAULT '0',
  `l4` int(10) unsigned NOT NULL DEFAULT '0',
  `l5` int(10) unsigned NOT NULL DEFAULT '0',
  `l6` int(10) unsigned NOT NULL DEFAULT '0',
  `l7` int(10) unsigned NOT NULL DEFAULT '0',
  `l8` int(10) unsigned NOT NULL DEFAULT '0',
  `l9` int(10) unsigned NOT NULL DEFAULT '0',
  `l10` int(10) unsigned NOT NULL DEFAULT '0',
  `l11` int(10) unsigned NOT NULL DEFAULT '0',
  `l12` int(10) unsigned NOT NULL DEFAULT '0',
  `l13` int(10) unsigned NOT NULL DEFAULT '0',
  `l14` int(10) unsigned NOT NULL DEFAULT '0',
  `l15` int(10) unsigned NOT NULL DEFAULT '0',
  `l16` int(10) unsigned NOT NULL DEFAULT '0',
  `l17` int(10) unsigned NOT NULL DEFAULT '0',
  `l18` int(10) unsigned NOT NULL DEFAULT '0',
  `l19` int(10) unsigned NOT NULL DEFAULT '0',
  `l20` int(10) unsigned NOT NULL DEFAULT '0',
  `l21` int(10) unsigned NOT NULL DEFAULT '0',
  `l22` int(10) unsigned NOT NULL DEFAULT '0',
  `l23` int(10) unsigned NOT NULL DEFAULT '0',
  `l24` int(10) unsigned NOT NULL DEFAULT '0',
  `l25` int(10) unsigned NOT NULL DEFAULT '0',
  `l26` int(10) unsigned NOT NULL DEFAULT '0',
  `l27` int(10) unsigned NOT NULL DEFAULT '0',
  `l28` int(10) unsigned NOT NULL DEFAULT '0',
  `l29` int(10) unsigned NOT NULL DEFAULT '0',
  `l30` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`runDate`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dailyscoredistribution`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dailyscoredistribution` (
  `runDate` datetime NOT NULL,
  `s0` int(10) unsigned NOT NULL,
  `s20` int(10) unsigned NOT NULL,
  `s40` int(10) unsigned NOT NULL,
  `s60` int(10) unsigned NOT NULL,
  `s80` int(10) unsigned NOT NULL,
  `s100` int(10) unsigned NOT NULL,
  `s120` int(10) unsigned NOT NULL,
  `s140` int(10) unsigned NOT NULL,
  `s160` int(10) unsigned NOT NULL,
  `s180` int(10) unsigned NOT NULL,
  `s200` int(10) unsigned NOT NULL,
  `s220` int(10) unsigned NOT NULL,
  `s240` int(10) unsigned NOT NULL,
  `s260` int(10) unsigned NOT NULL,
  `s280` int(10) unsigned NOT NULL,
  `s300` int(10) unsigned NOT NULL,
  PRIMARY KEY (`runDate`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `didnumber`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `didnumber` (
  `CountryID` int(10) unsigned NOT NULL,
  `Number` varchar(128) NOT NULL,
  `DialNumber` varchar(128) DEFAULT NULL,
  `Status` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `didtestcall`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `didtestcall` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CountryID` int(10) unsigned NOT NULL,
  `Number` varchar(128) NOT NULL,
  `DialNumber` varchar(128) DEFAULT NULL,
  `Destination` varchar(128) NOT NULL,
  `Gateway` int(10) unsigned NOT NULL,
  `Provider` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `DateCalled` datetime DEFAULT NULL,
  `DateReplied` datetime DEFAULT NULL,
  `Status` int(10) unsigned NOT NULL,
  `Reason` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `didteststatus`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `didteststatus` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `LastStamp` datetime NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discounttier`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `discounttier` (
  `ID` int(11) NOT NULL,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Type` int(11) NOT NULL DEFAULT '0',
  `ActualMin` double(12,2) NOT NULL DEFAULT '0.00',
  `DisplayMin` double(12,2) NOT NULL DEFAULT '0.00',
  `Max` double(12,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(6) NOT NULL DEFAULT '',
  `PercentageDiscount` double(12,2) NOT NULL DEFAULT '0.00',
  `ApplyToCreditCard` int(11) NOT NULL DEFAULT '0',
  `ApplyToBankTransfer` int(11) NOT NULL DEFAULT '0',
  `ApplyToWesternUnion` int(11) NOT NULL DEFAULT '0',
  `ApplyToVoucher` int(11) NOT NULL DEFAULT '0',
  `ApplyToTT` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_DiscountTier_1` (`Currency`),
  CONSTRAINT `FK_DiscountTier_1` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `distributionlist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distributionlist` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `Name` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_distributionlist_1` (`Username`),
  CONSTRAINT `FK_distributionlist_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `distributionlistmember`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distributionlistmember` (
  `ContactID` int(11) NOT NULL DEFAULT '0',
  `DistributionListID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ContactID`,`DistributionListID`),
  KEY `FK_distributionlistmember_1` (`DistributionListID`),
  CONSTRAINT `FK_distributionlistmember_1` FOREIGN KEY (`DistributionListID`) REFERENCES `distributionlist` (`ID`),
  CONSTRAINT `FK_distributionlistmember_2` FOREIGN KEY (`ContactID`) REFERENCES `contact` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `duh`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `duh` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `number` int(11) NOT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emailtemplate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emailtemplate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `templateType` int(11) NOT NULL,
  `subjectTemplate` varchar(512) NOT NULL,
  `bodyTemplate` text NOT NULL,
  `mimeType` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emailtemplate_20140527`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emailtemplate_20140527` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) CHARACTER SET utf8 NOT NULL,
  `templateType` int(11) NOT NULL,
  `subjectTemplate` varchar(512) CHARACTER SET utf8 NOT NULL,
  `bodyTemplate` text CHARACTER SET utf8 NOT NULL,
  `mimeType` varchar(50) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emailtemplate_bak`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emailtemplate_bak` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `templateType` int(11) NOT NULL,
  `subjectTemplate` varchar(512) NOT NULL,
  `bodyTemplate` text NOT NULL,
  `mimeType` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emailtemplatepart`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emailtemplatepart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `templateid` int(11) NOT NULL,
  `sequence` smallint(6) NOT NULL,
  `contentTemplate` text NOT NULL,
  `mimeType` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_emailtemplatepart_templateid` (`templateid`),
  CONSTRAINT `emailtemplatepart_ibfk_1` FOREIGN KEY (`templateid`) REFERENCES `emailtemplate` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emote`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emote` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Command` varchar(128) NOT NULL DEFAULT '',
  `Action` varchar(128) NOT NULL DEFAULT '',
  `ActionWithTarget` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=728 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emotecommand`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emotecommand` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ParentID` int(11) DEFAULT NULL,
  `CommandName` varchar(128) NOT NULL,
  `CommandStateName` varchar(128) DEFAULT NULL,
  `Description` varchar(128) DEFAULT NULL,
  `HandlerClassName` varchar(128) DEFAULT NULL,
  `SupportedChatTypes` bit(8) NOT NULL DEFAULT b'0',
  `MessageColour` varchar(6) DEFAULT NULL,
  `MessageText` varchar(512) DEFAULT NULL,
  `EmoticonKeyList` varchar(128) DEFAULT NULL,
  `Price` double DEFAULT NULL,
  `Currency` varchar(6) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `OfflineEnabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `U_CommandName` (`CommandName`),
  KEY `FK_ParentID` (`ParentID`),
  CONSTRAINT `FK_ParentID` FOREIGN KEY (`ParentID`) REFERENCES `emotecommand` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emoticon`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emoticon` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `EmoticonPackID` int(11) NOT NULL,
  `Type` int(11) NOT NULL,
  `Alias` varchar(128) NOT NULL DEFAULT '',
  `Width` int(11) NOT NULL DEFAULT '0',
  `Height` int(11) NOT NULL DEFAULT '0',
  `Location` varchar(128) NOT NULL,
  `LocationPNG` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_emoticon_1` (`EmoticonPackID`),
  CONSTRAINT `FK_emoticon_1` FOREIGN KEY (`EmoticonPackID`) REFERENCES `emoticonpack` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4704 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emoticonhotkey`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emoticonhotkey` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `EmoticonID` int(11) NOT NULL DEFAULT '0',
  `Type` int(11) NOT NULL DEFAULT '0',
  `HotKey` varchar(128) CHARACTER SET latin1 NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_EmoticonHotKey_1` (`EmoticonID`),
  CONSTRAINT `FK_EmoticonHotKey_1` FOREIGN KEY (`EmoticonID`) REFERENCES `emoticon` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7048 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emoticonpack`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emoticonpack` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Type` int(11) NOT NULL DEFAULT '0',
  `Name` varchar(128) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `Description` varchar(128) DEFAULT NULL,
  `Price` double(12,2) NOT NULL DEFAULT '0.00',
  `ServiceID` int(11) DEFAULT NULL,
  `GroupID` int(11) DEFAULT NULL,
  `GroupVIPOnly` tinyint(1) DEFAULT NULL,
  `SortOrder` int(11) DEFAULT NULL,
  `ForSale` int(11) NOT NULL DEFAULT '1',
  `Status` int(11) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `idx_type` (`Type`),
  KEY `GroupID` (`GroupID`),
  KEY `FK_emoticonpack_1` (`ServiceID`),
  CONSTRAINT `emoticonpack_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_emoticonpack_1` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emoticonpackowner`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `emoticonpackowner` (
  `Username` varchar(128) NOT NULL DEFAULT '',
  `EmoticonPackID` int(11) NOT NULL DEFAULT '0',
  `Status` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`Username`,`EmoticonPackID`),
  KEY `FK_EmoticonPackOwner_2` (`EmoticonPackID`),
  CONSTRAINT `FK_EmoticonPackOwner_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_EmoticonPackOwner_2` FOREIGN KEY (`EmoticonPackID`) REFERENCES `emoticonpack` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exchangeratehistory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exchangeratehistory` (
  `Code` varchar(6) NOT NULL DEFAULT '',
  `DateCreated` date NOT NULL DEFAULT '0000-00-00',
  `ExchangeRate` double(15,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`Code`,`DateCreated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `externaldownloadlink`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `externaldownloadlink` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `URL` varchar(128) NOT NULL,
  `HitRate` int(11) NOT NULL,
  `LastUpdated` datetime NOT NULL,
  `Version` varchar(128) NOT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `extrabasicoutcomerewarded`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `extrabasicoutcomerewarded` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RewardProgramCompletedID` bigint(20) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `ScoreReward` int(11) NOT NULL,
  `LevelReward` int(11) NOT NULL,
  `MigCreditReward` double(15,4) NOT NULL,
  `MigCreditRewardCurrency` varchar(6) NOT NULL,
  `BaseExchRate` double(15,4) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_extrabasicoutcomerewarded_MigCreditRewardCurrency` (`MigCreditRewardCurrency`),
  KEY `IX_extrabasicoutcomerewarded_rewardprogramcompleted` (`RewardProgramCompletedID`),
  CONSTRAINT `FK_extrabasicoutcomerewarded_MigCreditRewardCurrency` FOREIGN KEY (`MigCreditRewardCurrency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file` (
  `ID` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MIMEType` varchar(128) NOT NULL,
  `Size` int(11) NOT NULL,
  `Width` int(11) DEFAULT '0',
  `Height` int(11) DEFAULT '0',
  `Length` int(11) DEFAULT '0',
  `UploadedBy` varchar(128) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_file_1` (`UploadedBy`),
  CONSTRAINT `FK_file_1` FOREIGN KEY (`UploadedBy`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fixedcallrate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fixedcallrate` (
  `SourceCountryID` int(11) NOT NULL,
  `DestinationCountryID` int(11) NOT NULL,
  `LandlineToLandline` double(10,6) DEFAULT NULL,
  `LandlineToLandlineSignallingFee` double(10,6) DEFAULT NULL,
  `LandlineToMobile` double(10,6) DEFAULT NULL,
  `LandlineToMobileSignallingFee` double(10,6) DEFAULT NULL,
  `MobileToLandline` double(10,6) DEFAULT NULL,
  `MobileToLandlineSignallingFee` double(10,6) DEFAULT NULL,
  `MobileToMobile` double(10,6) DEFAULT NULL,
  `MobileToMobileSignallingFee` double(10,6) DEFAULT NULL,
  `CallThroughToLandline` double(10,6) DEFAULT NULL,
  `CallThroughToLandlineSignallingFee` double(10,6) DEFAULT NULL,
  `CallThroughToMobile` double(10,6) DEFAULT NULL,
  `CallThroughToMobileSignallingFee` double(10,6) DEFAULT NULL,
  `Currency` varchar(6) NOT NULL,
  PRIMARY KEY (`SourceCountryID`,`DestinationCountryID`),
  KEY `FK_fixedcallrate_1` (`SourceCountryID`),
  KEY `FK_fixedcallrate_2` (`Currency`),
  KEY `FK_fixedcallrate_3` (`DestinationCountryID`),
  CONSTRAINT `fixedcallrate_ibfk_1` FOREIGN KEY (`DestinationCountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_fixedcallrate_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fixedsmscost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fixedsmscost` (
  `CountryID` int(11) NOT NULL,
  `Cost` double(10,6) NOT NULL,
  `Currency` varchar(6) NOT NULL,
  PRIMARY KEY (`CountryID`),
  KEY `FK_fixedsmscost_1` (`CountryID`),
  KEY `FK_fixedsmscost_2` (`Currency`),
  CONSTRAINT `FK_fixedsmscost_1` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_fixedsmscost_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupannouncement`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupannouncement` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `CreatedBy` varchar(128) NOT NULL,
  `Title` varchar(128) NOT NULL,
  `Text` text NOT NULL,
  `SMSText` text,
  `LastModifiedDate` datetime NOT NULL,
  `LastModifiedBy` varchar(128) NOT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_GroupAnnouncement_1` (`GroupID`),
  KEY `FK_GroupAnnouncement_2` (`CreatedBy`),
  KEY `FK_GroupAnnouncement_3` (`LastModifiedBy`),
  CONSTRAINT `FK_GroupAnnouncement_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_GroupAnnouncement_2` FOREIGN KEY (`CreatedBy`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_GroupAnnouncement_3` FOREIGN KEY (`LastModifiedBy`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupblacklist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupblacklist` (
  `GroupID` int(11) NOT NULL,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime DEFAULT NULL,
  PRIMARY KEY (`GroupID`,`Username`),
  KEY `Username` (`Username`),
  CONSTRAINT `groupblacklist_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `groupblacklist_ibfk_2` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupcategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupcategory` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `SortOrder` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupevent`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupevent` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `Description` varchar(128) NOT NULL,
  `StartTime` datetime NOT NULL,
  `DurationMinutes` int(11) DEFAULT NULL,
  `ChatRoomName` varchar(128) DEFAULT NULL,
  `ChatRoomCategoryID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `AlertSent` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupID` (`GroupID`),
  KEY `ChatRoomName` (`ChatRoomName`),
  KEY `ChatRoomCategoryID` (`ChatRoomCategoryID`),
  CONSTRAINT `groupevent_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `groupevent_ibfk_2` FOREIGN KEY (`ChatRoomName`) REFERENCES `chatroom` (`Name`),
  CONSTRAINT `groupevent_ibfk_3` FOREIGN KEY (`ChatRoomCategoryID`) REFERENCES `chatroomcategory` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupforum`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupforum` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL DEFAULT '0',
  `NumPosts` int(7) NOT NULL DEFAULT '0',
  `NumComments` int(7) NOT NULL DEFAULT '0',
  `NumLikes` int(7) NOT NULL DEFAULT '0',
  `NumDislikes` int(7) NOT NULL DEFAULT '0',
  `LastUpdated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupID` (`GroupID`),
  KEY `LastUpdated` (`LastUpdated`)
) ENGINE=InnoDB AUTO_INCREMENT=392842 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupforumpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupforumpost` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ForumID` int(11) NOT NULL DEFAULT '0',
  `Title` varchar(150) NOT NULL DEFAULT '',
  `Body` text NOT NULL,
  `NumComments` int(7) NOT NULL DEFAULT '0',
  `NumLikes` int(7) NOT NULL DEFAULT '0',
  `NumDislikes` int(7) NOT NULL DEFAULT '0',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `LastUpdated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `AuthorID` int(10) unsigned NOT NULL DEFAULT '0',
  `FileID` int(7) DEFAULT NULL,
  `IsLocked` int(1) NOT NULL DEFAULT '0',
  `IsSticked` int(1) NOT NULL DEFAULT '0',
  `Status` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `DateCreated` (`DateCreated`),
  KEY `FK_groupforumpost_authorid` (`AuthorID`),
  KEY `ForumID` (`ForumID`,`LastUpdated`),
  CONSTRAINT `FK_groupforumpost_authorid` FOREIGN KEY (`AuthorID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_groupforumpost_forumid` FOREIGN KEY (`ForumID`) REFERENCES `groupforum` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=944896 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupforumpostcomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupforumpostcomment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PostID` int(11) NOT NULL DEFAULT '0',
  `UserID` int(11) NOT NULL DEFAULT '0',
  `Comment` varchar(4000) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Status` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupforumpostlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupforumpostlike` (
  `GroupForumPostID` int(11) NOT NULL DEFAULT '0',
  `UserID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` int(1) NOT NULL,
  PRIMARY KEY (`GroupForumPostID`,`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupinvitation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupinvitation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `GroupID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Inviter` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_GroupInvitation_1` (`Username`),
  KEY `FK_GroupInvitation_2` (`GroupID`),
  KEY `FK_GroupInvitation_3` (`Inviter`),
  CONSTRAINT `FK_GroupInvitation_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_GroupInvitation_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_GroupInvitation_3` FOREIGN KEY (`Inviter`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=54606322 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupjoinrequest`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupjoinrequest` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `RequesterID` int(11) NOT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_GroupInvitation_2` (`GroupID`),
  KEY `RequesterID` (`RequesterID`),
  CONSTRAINT `groupjoinrequest_ibfk_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=161460 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouplike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouplike` (
  `GroupID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`GroupID`,`UserID`),
  KEY `FK_grouplike_userid` (`UserID`),
  CONSTRAINT `FK_grouplike_groupid` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_grouplike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouplikesummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouplikesummary` (
  `GroupID` int(11) NOT NULL,
  `NumLikes` int(11) NOT NULL DEFAULT '0',
  `NumDislikes` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`GroupID`),
  CONSTRAINT `FK_grouplikesummary_groupid` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouplinks`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouplinks` (
  `GroupID` int(11) NOT NULL DEFAULT '0',
  `LinkedGroupID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`GroupID`,`LinkedGroupID`),
  KEY `LinkedGroupID` (`LinkedGroupID`),
  CONSTRAINT `grouplinks_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `grouplinks_ibfk_2` FOREIGN KEY (`LinkedGroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupmember`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupmember` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `GroupID` int(11) NOT NULL,
  `LocationID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` int(11) NOT NULL,
  `DateLeft` datetime DEFAULT NULL,
  `ExpirationDate` datetime DEFAULT NULL,
  `SMSNotification` int(11) NOT NULL,
  `EmailNotification` int(11) NOT NULL,
  `EventNotification` int(11) NOT NULL,
  `SMSGroupEventNotification` int(11) NOT NULL DEFAULT '0',
  `EmailThreadUpdateNotification` int(11) NOT NULL DEFAULT '0',
  `EventThreadUpdateNotification` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Index_GroupMember_1` (`Username`,`GroupID`),
  KEY `FK_GroupMember_3` (`LocationID`),
  KEY `index_groupid_status_type` (`GroupID`,`Status`,`Type`),
  CONSTRAINT `FK_GroupMember_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_GroupMember_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_GroupMember_3` FOREIGN KEY (`LocationID`) REFERENCES `location` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=72189412 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupmembershipcost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupmembershipcost` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `Description` varchar(128) NOT NULL,
  `Cost` double DEFAULT NULL,
  `Currency` varchar(6) DEFAULT NULL,
  `DurationDays` int(11) NOT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_GroupID` (`GroupID`),
  KEY `FK_Currency` (`Currency`),
  CONSTRAINT `Currency` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `GroupID` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupmembershipreward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupmembershipreward` (
  `RewardProgramID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  PRIMARY KEY (`RewardProgramID`,`GroupID`),
  KEY `FK_GroupMembershipReward_2` (`GroupID`),
  CONSTRAINT `FK_GroupMembershipReward_1` FOREIGN KEY (`RewardProgramID`) REFERENCES `rewardprogram` (`ID`),
  CONSTRAINT `FK_GroupMembershipReward_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupmembershiprewarded`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupmembershiprewarded` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RewardProgramCompletedID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_GroupMembershipRewarded_1` (`RewardProgramCompletedID`),
  KEY `FK_GroupMembershipRewarded_2` (`GroupID`),
  CONSTRAINT `FK_GroupMembershipRewarded_1` FOREIGN KEY (`RewardProgramCompletedID`) REFERENCES `rewardprogramcompleted` (`ID`),
  CONSTRAINT `FK_GroupMembershipRewarded_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupmodule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupmodule` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `Title` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `CreatedBy` varchar(128) NOT NULL,
  `LastModifiedDate` datetime DEFAULT NULL,
  `LastModifiedBy` varchar(128) DEFAULT NULL,
  `Position` int(11) DEFAULT NULL,
  `Type` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupID` (`GroupID`),
  KEY `CreatedBy` (`CreatedBy`),
  CONSTRAINT `groupmodule_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `groupmodule_ibfk_2` FOREIGN KEY (`CreatedBy`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupphoto`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupphoto` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupPhotoAlbumID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `FileID` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Description` varchar(1024) DEFAULT NULL,
  `NumComments` int(7) NOT NULL DEFAULT '0',
  `NumLikes` int(7) NOT NULL DEFAULT '0',
  `NumDislikes` int(7) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_groupphoto_1` (`GroupPhotoAlbumID`),
  KEY `FK_groupphoto_2` (`GroupID`),
  KEY `FK_groupphoto_3` (`UserID`),
  KEY `FK_groupphoto_4` (`FileID`),
  CONSTRAINT `FK_groupphoto_1` FOREIGN KEY (`GroupPhotoAlbumID`) REFERENCES `groupphotoalbum` (`ID`),
  CONSTRAINT `FK_groupphoto_2` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_groupphoto_3` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_groupphoto_4` FOREIGN KEY (`FileID`) REFERENCES `file` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupphotoalbum`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupphotoalbum` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Description` varchar(1024) DEFAULT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CoverPhotoID` int(11) DEFAULT NULL,
  `NumPhotos` int(7) NOT NULL DEFAULT '0',
  `Size` int(11) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_groupphotoalbum_1` (`GroupID`),
  KEY `FK_groupphotoalbum_2` (`UserID`),
  KEY `FK_groupphotoalbum_3` (`CoverPhotoID`),
  CONSTRAINT `FK_groupphotoalbum_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_groupphotoalbum_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_groupphotoalbum_3` FOREIGN KEY (`CoverPhotoID`) REFERENCES `groupphoto` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupphotocomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupphotocomment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupPhotoID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Comment` varchar(4000) NOT NULL,
  `NumLikes` int(11) NOT NULL DEFAULT '0',
  `NumDislikes` int(11) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_groupphotocomment_groupphotoid` (`GroupPhotoID`),
  KEY `FK_groupphotocomment_userid` (`UserID`),
  CONSTRAINT `FK_groupphotocomment_groupphotoid` FOREIGN KEY (`GroupPhotoID`) REFERENCES `groupphoto` (`ID`),
  CONSTRAINT `FK_groupphotocomment_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupphotocommentlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupphotocommentlike` (
  `GroupPhotoCommentID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`GroupPhotoCommentID`,`UserID`),
  KEY `FK_groupphotocommentlike_userid` (`UserID`),
  CONSTRAINT `FK_groupphotocommentlike_groupphotocommentid` FOREIGN KEY (`GroupPhotoCommentID`) REFERENCES `groupphotocomment` (`ID`),
  CONSTRAINT `FK_groupphotocommentlike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupphotolike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupphotolike` (
  `GroupPhotoID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`GroupPhotoID`,`UserID`),
  KEY `FK_groupphotolike_userid` (`UserID`),
  CONSTRAINT `FK_groupphotolike_groupphotoid` FOREIGN KEY (`GroupPhotoID`) REFERENCES `groupphoto` (`ID`),
  CONSTRAINT `FK_groupphotolike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouppoll`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouppoll` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Description` varchar(1024) DEFAULT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DateExpiry` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `TotalVotes` int(7) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_grouppoll_1` (`GroupID`),
  KEY `FK_grouppoll_2` (`UserID`),
  CONSTRAINT `FK_grouppoll_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_grouppoll_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouppolloption`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouppolloption` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupPollID` int(11) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `NumVotes` int(7) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_grouppolloption_grouppollid` (`GroupPollID`),
  CONSTRAINT `FK_grouppolloption_grouppollid` FOREIGN KEY (`GroupPollID`) REFERENCES `grouppoll` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouppollvote`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouppollvote` (
  `GroupPollOptionID` int(11) NOT NULL,
  `GroupPollID` int(11) NOT NULL,
  `GroupID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`GroupPollOptionID`,`UserID`),
  KEY `FK_grouppollvote_grouppollid` (`GroupPollID`),
  KEY `FK_grouppollvote_groupid` (`GroupID`),
  KEY `FK_grouppollvote_userid` (`UserID`),
  CONSTRAINT `FK_grouppollvote_groupid` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `FK_grouppollvote_grouppollid` FOREIGN KEY (`GroupPollID`) REFERENCES `grouppoll` (`ID`),
  CONSTRAINT `FK_grouppollvote_grouppolloptionid` FOREIGN KEY (`GroupPollOptionID`) REFERENCES `grouppolloption` (`ID`),
  CONSTRAINT `FK_grouppollvote_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouppost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouppost` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupModuleID` int(11) NOT NULL,
  `Teaser` varchar(1024) NOT NULL,
  `Body` text,
  `DateCreated` datetime NOT NULL,
  `CreatedBy` varchar(128) NOT NULL,
  `LastModifiedDate` datetime DEFAULT NULL,
  `LastModifiedBy` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupModuleID` (`GroupModuleID`),
  KEY `CreatedBy` (`CreatedBy`),
  KEY `LastModifiedBy` (`LastModifiedBy`),
  CONSTRAINT `grouppost_ibfk_1` FOREIGN KEY (`GroupModuleID`) REFERENCES `groupmodule` (`ID`),
  CONSTRAINT `grouppost_ibfk_2` FOREIGN KEY (`CreatedBy`) REFERENCES `user` (`Username`),
  CONSTRAINT `grouppost_ibfk_3` FOREIGN KEY (`LastModifiedBy`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grouprss`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grouprss` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GroupID` int(11) NOT NULL,
  `Name` varchar(64) NOT NULL DEFAULT '',
  `Url` varchar(128) NOT NULL,
  `LastUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupID` (`GroupID`),
  CONSTRAINT `grouprss_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `CountryID` int(11) DEFAULT NULL,
  `Name` varchar(128) NOT NULL,
  `Description` varchar(128) NOT NULL,
  `About` text NOT NULL,
  `DateCreated` datetime NOT NULL,
  `CreatedBy` varchar(128) NOT NULL,
  `Picture` varchar(128) DEFAULT NULL,
  `EmailAddress` varchar(128) DEFAULT NULL,
  `Premium` int(11) NOT NULL DEFAULT '0',
  `ReferralSMS` varchar(160) DEFAULT NULL,
  `GroupCategoryID` int(11) DEFAULT NULL,
  `VIPServiceID` int(11) DEFAULT NULL,
  `SortOrder` int(11) DEFAULT NULL,
  `NumMembers` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL,
  `AllowNonMembersToJoinRooms` tinyint(1) DEFAULT NULL,
  `Featured` tinyint(4) NOT NULL DEFAULT '0',
  `Official` tinyint(4) NOT NULL DEFAULT '0',
  `NumPhotos` int(11) NOT NULL DEFAULT '0',
  `NumForumPosts` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `idx_Groups_1` (`Name`),
  KEY `FK_Groups_1` (`CreatedBy`),
  KEY `FK_Groups_2` (`CountryID`),
  KEY `FK_GroupCategoryID` (`GroupCategoryID`),
  KEY `FK_Groups_3` (`VIPServiceID`),
  CONSTRAINT `FK_GroupCategoryID` FOREIGN KEY (`GroupCategoryID`) REFERENCES `groupcategory` (`ID`),
  CONSTRAINT `FK_Groups_1` FOREIGN KEY (`CreatedBy`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_Groups_2` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_Groups_3` FOREIGN KEY (`VIPServiceID`) REFERENCES `service` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=400455 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupuserpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupuserpost` (
  `GroupID` int(11) NOT NULL,
  `UserPostID` int(11) NOT NULL,
  PRIMARY KEY (`GroupID`,`UserPostID`),
  KEY `UserPostID` (`UserPostID`),
  CONSTRAINT `groupuserpost_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `groupuserpost_ibfk_2` FOREIGN KEY (`UserPostID`) REFERENCES `userpost` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupwallpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupwallpost` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `groupid` int(11) NOT NULL,
  `authoruserid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `body` varchar(4000) NOT NULL,
  `numcomments` int(11) NOT NULL DEFAULT '0',
  `numlikes` int(11) NOT NULL DEFAULT '0',
  `numdislikes` int(11) NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_groupwallpost_groupid` (`groupid`),
  KEY `FK_groupwallpost_authoruserid` (`authoruserid`),
  CONSTRAINT `FK_groupwallpost_authoruserid` FOREIGN KEY (`authoruserid`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_groupwallpost_groupid` FOREIGN KEY (`groupid`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupwallpostcomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupwallpostcomment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `groupwallpostid` int(10) unsigned NOT NULL,
  `userid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `comment` varchar(4000) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_groupwallpostcomment_groupwallpostid` (`groupwallpostid`),
  KEY `FK_groupwallpostcomment_userid` (`userid`),
  CONSTRAINT `FK_groupwallpostcomment_groupwallpostid` FOREIGN KEY (`groupwallpostid`) REFERENCES `groupwallpost` (`id`),
  CONSTRAINT `FK_groupwallpostcomment_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupwallpostlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupwallpostlike` (
  `groupwallpostid` int(10) unsigned NOT NULL,
  `userid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`groupwallpostid`,`userid`),
  KEY `FK_groupwallpostlike_userid` (`userid`),
  CONSTRAINT `FK_groupwallpostlike_groupwallpostid` FOREIGN KEY (`groupwallpostid`) REFERENCES `groupwallpost` (`id`),
  CONSTRAINT `FK_groupwallpostlike_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guardcapability`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guardcapability` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guardset`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guardset` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guardsetcapability`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guardsetcapability` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GuardCapabilityID` int(11) NOT NULL,
  `GuardSetID` int(11) NOT NULL,
  `CapabilityType` tinyint(4) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `unique_GuardCapabilityID_GuardSetID` (`GuardCapabilityID`,`GuardSetID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guardsetmember`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guardsetmember` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GuardSetID` int(11) NOT NULL,
  `MemberID` int(11) NOT NULL,
  `MemberType` tinyint(4) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `idx_CapabilityGroiupID` (`GuardSetID`),
  KEY `idx_MemberID` (`MemberID`)
) ENGINE=InnoDB AUTO_INCREMENT=89281 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `handsetinstructions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `handsetinstructions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `InstructionText` text,
  `Description` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `handsets`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `handsets` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Vendor` varchar(128) DEFAULT NULL,
  `PhoneModel` varchar(128) DEFAULT NULL,
  `InstructionId` int(11) DEFAULT NULL,
  `MidletVersion` varchar(128) NOT NULL,
  `MidletAcceptType` int(11) NOT NULL,
  `MIDP` varchar(128) NOT NULL,
  `CLDC` varchar(128) NOT NULL,
  `CameraSupport` int(1) NOT NULL,
  `FileSystemSupport` int(1) NOT NULL,
  `pngSupport` int(1) NOT NULL,
  `gifSupport` int(1) NOT NULL,
  `signedMidletSupport` int(1) NOT NULL,
  `jpegSupport` int(1) NOT NULL,
  `ScreenWidth` int(11) DEFAULT NULL,
  `ScreenHeight` int(11) DEFAULT NULL,
  `ApplicationIconSize` int(11) NOT NULL,
  `Comments` text,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Vendor` (`Vendor`,`PhoneModel`),
  KEY `InstructionId` (`InstructionId`),
  CONSTRAINT `handsets_ibfk_1` FOREIGN KEY (`InstructionId`) REFERENCES `handsetinstructions` (`ID`),
  CONSTRAINT `handsets_ibfk_2` FOREIGN KEY (`Vendor`) REFERENCES `handsetvendorprefixes` (`Vendor`)
) ENGINE=InnoDB AUTO_INCREMENT=729 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `handsetvendorprefixes`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `handsetvendorprefixes` (
  `Vendor` varchar(128) NOT NULL,
  `Prefix` varchar(128) NOT NULL,
  PRIMARY KEY (`Vendor`,`Prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hashtag`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hashtag` (
  `hashtag` varchar(128) NOT NULL,
  `countryid` int(11) NOT NULL DEFAULT '-1',
  `description` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`hashtag`,`countryid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inviterUserId` int(10) unsigned NOT NULL,
  `type` tinyint(4) NOT NULL COMMENT 'join invitationType for more details',
  `channel` tinyint(4) NOT NULL COMMENT 'join invitationChannel for more details',
  `destination` varchar(128) NOT NULL COMMENT 'where you want to send the invitation, could be phone num/email/facebook',
  `createdTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expireTime` datetime DEFAULT NULL COMMENT 'will expire if its not null',
  `status` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=578 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitationparameters`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitationparameters` (
  `invitationId` int(10) unsigned NOT NULL COMMENT 'lookup key to invitation table',
  `type` tinyint(4) NOT NULL,
  `value` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitationresponse`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitationresponse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invitationId` int(10) unsigned NOT NULL COMMENT 'lookup key to invitation table',
  `responseType` tinyint(4) DEFAULT NULL,
  `responseTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `username` varchar(128) NOT NULL DEFAULT 'invitee username',
  PRIMARY KEY (`id`),
  KEY `IDX_INVITATION_RESPONSE_RESPTYPE_UNAME` (`responseType`,`username`) USING HASH,
  KEY `IDX_INVITATION_INVITATION_ID` (`invitationId`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keyword`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keyword` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Keyword` varchar(64) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `idx_Keyword1` (`Keyword`)
) ENGINE=InnoDB AUTO_INCREMENT=8858410 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `language`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `Code` varchar(3) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_giftreceiveralltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_giftreceiveralltime` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_giftreceiverweekly`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_giftreceiverweekly` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_giftsenderalltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_giftsenderalltime` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_giftsenderweekly`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_giftsenderweekly` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_miglevelalltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_miglevelalltime` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_profilelikesalltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_profilelikesalltime` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_referreralltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_referreralltime` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lb_referrerweekly`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lb_referrerweekly` (
  `UserID` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `UserID` (`UserID`),
  KEY `Value` (`Value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `leaderboardgrouplikesalltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leaderboardgrouplikesalltime` (
  `Id` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `Value` (`Value`),
  KEY `Id` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `leaderboardgroupphotosalltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leaderboardgroupphotosalltime` (
  `Id` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `Value` (`Value`),
  KEY `Id` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `leaderboardgrouptopicsalltime`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `leaderboardgrouptopicsalltime` (
  `Id` int(11) unsigned NOT NULL DEFAULT '0',
  `Value` int(11) NOT NULL DEFAULT '0',
  `Rank` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Rank`),
  KEY `Value` (`Value`),
  KEY `Id` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liveidcredential`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `liveidcredential` (
  `username` varchar(128) NOT NULL DEFAULT '',
  `liveid` varchar(128) NOT NULL DEFAULT '',
  `password` varchar(128) NOT NULL DEFAULT '',
  `dateRegistered` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`username`) USING BTREE,
  UNIQUE KEY `UQ_liveIdCredential_1` (`liveid`),
  CONSTRAINT `FK_liveIdCredential_1` FOREIGN KEY (`username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ParentLocationID` int(11) DEFAULT NULL,
  `CountryID` int(11) NOT NULL,
  `Name` varchar(128) NOT NULL,
  `Level` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_CountryID_Level` (`CountryID`,`Level`),
  KEY `FK_Location_1` (`ParentLocationID`),
  KEY `FK_Location_2` (`CountryID`),
  CONSTRAINT `FK_Location_1` FOREIGN KEY (`ParentLocationID`) REFERENCES `location` (`ID`),
  CONSTRAINT `FK_Reseller_2` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=38885 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lookout`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lookout` (
  `Username` varchar(128) NOT NULL DEFAULT '',
  `ContactUsername` varchar(128) NOT NULL DEFAULT '',
  `DateLastSent` datetime DEFAULT NULL,
  PRIMARY KEY (`Username`,`ContactUsername`),
  KEY `index_1` (`ContactUsername`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `maildb`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maildb` (
  `username` varchar(128) NOT NULL DEFAULT '',
  `quota` varchar(20) DEFAULT '',
  `mailmask` varchar(18) NOT NULL DEFAULT '0.0.0.0',
  `maildrop` varchar(255) DEFAULT NULL,
  `domain` varchar(128) DEFAULT '',
  `created` varchar(20) DEFAULT '',
  `full_name` varchar(128) DEFAULT '',
  `phone` varchar(128) DEFAULT '',
  `groups` varchar(255) DEFAULT '',
  `smsto` varchar(128) DEFAULT '',
  `mailaccess` varchar(255) DEFAULT '',
  `mailstatus` varchar(128) DEFAULT '',
  `spf_block` varchar(20) DEFAULT '',
  `disabled` varchar(20) DEFAULT '',
  `alias_quota` varchar(20) DEFAULT '',
  `list_quota` varchar(20) DEFAULT '',
  `user_access` varchar(255) DEFAULT '',
  `send_limit` varchar(20) DEFAULT '',
  `tohost` varchar(255) DEFAULT '',
  `realuser` varchar(255) DEFAULT '',
  `allow` varchar(255) DEFAULT '',
  `friends` varchar(20) DEFAULT '',
  `enotify` varchar(255) DEFAULT '',
  `ddpriv` varchar(128) DEFAULT '',
  `ddfrom` varchar(128) DEFAULT '',
  `ccname` varchar(128) DEFAULT '',
  `ccnumber` varchar(128) DEFAULT '',
  `ccexpires` varchar(20) DEFAULT '',
  `ccciv` varchar(128) DEFAULT '',
  `cctype` varchar(128) DEFAULT '',
  `fwd` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `idx_tohost` (`tohost`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mailinglist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mailinglist` (
  `Email` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Type` int(11) NOT NULL DEFAULT '0',
  `Position` int(11) NOT NULL DEFAULT '0',
  `Title` varchar(128) NOT NULL DEFAULT '',
  `URL` varchar(128) NOT NULL DEFAULT '',
  `CountryID` int(11) DEFAULT NULL,
  `MinVersion` int(11) NOT NULL,
  `MaxVersion` int(11) NOT NULL,
  `ClientType` int(4) NOT NULL DEFAULT '2',
  `Location` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_menu_1` (`CountryID`),
  CONSTRAINT `FK_menu_1` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menuentry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menuentry` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `Label` varchar(128) NOT NULL,
  `IconURL` varchar(255) DEFAULT NULL,
  `ActionURL` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantdetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantdetails` (
  `ID` int(10) NOT NULL COMMENT 'userID of merchant',
  `logincount` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'number of times a merchant has logged into  \nthe merchant system',
  `mentor` varchar(50) DEFAULT NULL,
  `referrer` varchar(128) DEFAULT NULL,
  `username_color_type` int(10) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `idx_Mentor` (`mentor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantlocation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantlocation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `LocationID` int(11) NOT NULL,
  `Username` varchar(128) DEFAULT NULL,
  `Name` varchar(128) NOT NULL,
  `Address` varchar(128) DEFAULT NULL,
  `PhoneNumber` varchar(128) DEFAULT NULL,
  `EmailAddress` varchar(128) DEFAULT NULL,
  `Notes` varchar(256) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_MerchantLocation_1` (`LocationID`),
  CONSTRAINT `FK_MerchantLocation_1` FOREIGN KEY (`LocationID`) REFERENCES `location` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=14542 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantpin`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantpin` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `AuthToken` varchar(64) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL,
  `SecretQuestion` varchar(100) NOT NULL,
  `SecretAnswer` varchar(100) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `Status` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `UserID` (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=82337 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantpointslog`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantpointslog` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATECREATED` datetime DEFAULT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `Points` int(11) NOT NULL DEFAULT '0',
  `Type` smallint(6) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `merchantpointslog_USERID_Type` (`UserID`,`Type`),
  KEY `merchantpointslog_USERID` (`UserID`),
  CONSTRAINT `FK_merchantpointslog_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantpointsreward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantpointsreward` (
  `rewardprogramid` int(11) NOT NULL,
  `points` int(11) NOT NULL,
  PRIMARY KEY (`rewardprogramid`),
  CONSTRAINT `FK_merchantpointsreward_rewardprogramid` FOREIGN KEY (`rewardprogramid`) REFERENCES `rewardprogram` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantpointsrewarded`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantpointsrewarded` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATECREATED` datetime NOT NULL,
  `rewardprogramcompletedid` bigint(20) NOT NULL,
  `merchantpointslogID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_merchantpointsrewarded_rewardprogramcompletedid` (`rewardprogramcompletedid`),
  KEY `IDX_merchantpointsrewarded_merchantpointslogID` (`merchantpointslogID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchantrewardpoint`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchantrewardpoint` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DateCreated` datetime DEFAULT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `RewardPoint` double(10,2) NOT NULL DEFAULT '0.00',
  `Status` int(11) NOT NULL DEFAULT '0',
  `DateRedeemed` datetime DEFAULT NULL,
  `Notes` text,
  PRIMARY KEY (`ID`),
  KEY `UserID` (`UserID`),
  CONSTRAINT `FK_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchanttag`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchanttag` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(10) unsigned NOT NULL,
  `MerchantUserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `LastSalesDate` datetime NOT NULL,
  `Status` int(11) NOT NULL,
  `AccountentryID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MerchantTag_1` (`UserID`),
  KEY `FK_MerchantTag_2` (`MerchantUserID`),
  KEY `IDX_MerchantTag_1` (`Status`,`LastSalesDate`),
  KEY `FK_accountentryid` (`AccountentryID`),
  CONSTRAINT `FK_MerchantTag_1` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_MerchantTag_2` FOREIGN KEY (`MerchantUserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1994769 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchanttagstat`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchanttagstat` (
  `Date` date NOT NULL,
  `Created` int(5) NOT NULL DEFAULT '0',
  `Transfered` int(5) NOT NULL DEFAULT '0',
  `Expired` int(5) NOT NULL DEFAULT '0',
  `Failed` int(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `merchanttaguseractivitystat`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchanttaguseractivitystat` (
  `userID` int(11) NOT NULL,
  `currMonthActivity` int(11) NOT NULL,
  `prevMonthActivity` int(11) NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Type` int(11) NOT NULL DEFAULT '0',
  `MessageText` text NOT NULL,
  `SendReceive` int(11) NOT NULL DEFAULT '0',
  `SourceContactID` int(11) DEFAULT NULL,
  `Source` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_message_1` (`Username`),
  KEY `idx_Message_1` (`DateCreated`,`Type`,`SendReceive`),
  CONSTRAINT `FK_message_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messagedestination`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messagedestination` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `MessageID` int(11) NOT NULL DEFAULT '0',
  `ContactID` int(11) DEFAULT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `Destination` varchar(128) NOT NULL DEFAULT '',
  `IDDCode` int(11) DEFAULT NULL,
  `Cost` double(10,2) NOT NULL DEFAULT '0.00',
  `Gateway` int(11) DEFAULT NULL,
  `DateDispatched` datetime DEFAULT NULL,
  `ProviderTransactionID` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_messagedestination_1` (`MessageID`),
  KEY `FK_messagedestination_2` (`ContactID`),
  CONSTRAINT `FK_messagedestination_1` FOREIGN KEY (`MessageID`) REFERENCES `message` (`ID`),
  CONSTRAINT `FK_messagedestination_2` FOREIGN KEY (`ContactID`) REFERENCES `contact` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='InnoDB free: 11264 kB; (`MessageID`) REFER `fusion/message`(';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messagestats`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messagestats` (
  `StatsDate` date NOT NULL,
  `CountryID` int(11) NOT NULL,
  `Private` int(11) NOT NULL,
  `GroupChatSent` int(11) NOT NULL,
  `GroupChatReceived` int(11) NOT NULL,
  `ChatRoomSent` int(11) NOT NULL,
  `ChatRoomReceived` int(11) NOT NULL,
  `SMS` int(11) NOT NULL,
  `MSNSent` int(11) NOT NULL,
  `MSNReceived` int(11) NOT NULL,
  `YahooSent` int(11) NOT NULL,
  `YahooReceived` int(11) NOT NULL,
  `AIMSent` int(11) NOT NULL DEFAULT '0',
  `AIMReceived` int(11) NOT NULL DEFAULT '0',
  `GTalkSent` int(11) NOT NULL DEFAULT '0',
  `GTalkReceived` int(11) NOT NULL DEFAULT '0',
  `FacebookSent` int(11) NOT NULL DEFAULT '0',
  `FacebookReceived` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`StatsDate`,`CountryID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `misaccess`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `misaccess` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mislog`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mislog` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DateCreated` datetime NOT NULL,
  `StaffId` int(11) NOT NULL DEFAULT '0',
  `Section` varchar(128) NOT NULL DEFAULT '',
  `ObjectID` varchar(128) DEFAULT NULL,
  `Comment` varchar(256) NOT NULL DEFAULT '',
  `Action` varchar(30) NOT NULL DEFAULT '',
  `Description` text,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5741619 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mobileoriginatedsms`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mobileoriginatedsms` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DateCreated` datetime NOT NULL,
  `Type` int(11) NOT NULL,
  `Receiver` varchar(128) DEFAULT NULL,
  `Sender` varchar(128) DEFAULT NULL,
  `Text` varchar(160) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `Index_2` (`DateCreated`),
  KEY `Index_3` (`Sender`,`DateCreated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mobileprefix`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mobileprefix` (
  `IDDCode` int(11) NOT NULL DEFAULT '0',
  `Prefix` int(11) NOT NULL DEFAULT '0',
  `MinLength` int(11) NOT NULL DEFAULT '0',
  `MaxLength` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IDDCode`,`Prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `moneytransfer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `moneytransfer` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `ReceiptNumber` varchar(128) NOT NULL,
  `FullName` varchar(128) NOT NULL,
  `Amount` double(10,2) NOT NULL,
  `Type` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MoneyTransfer_1` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth2_apps`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth2_apps` (
  `name` varchar(64) NOT NULL,
  `clientid` varchar(32) NOT NULL,
  `clientsecret` varchar(64) NOT NULL,
  `homepageurl` varchar(128) NOT NULL,
  `callbackurl` varchar(128) NOT NULL,
  `description` varchar(256) NOT NULL,
  `permissions` varchar(256) NOT NULL DEFAULT '',
  `scopes` varchar(128) NOT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `loginurl` varchar(128) NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `clientid` (`clientid`),
  KEY `clientid_2` (`clientid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth2_authcodes`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth2_authcodes` (
  `code` varchar(64) NOT NULL,
  `userid` int(10) unsigned DEFAULT NULL,
  `scope` varchar(128) NOT NULL,
  `expires` bigint(20) unsigned NOT NULL,
  `clientid` varchar(32) NOT NULL,
  PRIMARY KEY (`code`),
  KEY `oauth2_authcodes_ibfk_clientid` (`clientid`),
  CONSTRAINT `oauth2_authcodes_ibfk_clientid` FOREIGN KEY (`clientid`) REFERENCES `oauth2_apps` (`clientid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth2_scopes`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth2_scopes` (
  `resource` varchar(128) NOT NULL DEFAULT '',
  `scope` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`resource`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth2_tokens`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth2_tokens` (
  `accesstoken` varchar(64) NOT NULL,
  `userid` int(10) unsigned NOT NULL,
  `scope` varchar(128) NOT NULL,
  `expires` bigint(20) unsigned NOT NULL,
  `clientid` varchar(32) NOT NULL,
  PRIMARY KEY (`accesstoken`),
  KEY `oauth2_tokens_ibfk_clientid` (`clientid`),
  CONSTRAINT `oauth2_tokens_ibfk_clientid` FOREIGN KEY (`clientid`) REFERENCES `oauth2_apps` (`clientid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_consumer_registry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_consumer_registry` (
  `ocr_id` int(11) NOT NULL AUTO_INCREMENT,
  `ocr_usa_id_ref` int(11) DEFAULT NULL,
  `ocr_consumer_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ocr_consumer_secret` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ocr_signature_methods` varchar(255) NOT NULL DEFAULT 'HMAC-SHA1,PLAINTEXT',
  `ocr_server_uri` varchar(255) NOT NULL,
  `ocr_server_uri_host` varchar(128) NOT NULL,
  `ocr_server_uri_path` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ocr_request_token_uri` varchar(255) NOT NULL,
  `ocr_authorize_uri` varchar(255) NOT NULL,
  `ocr_access_token_uri` varchar(255) NOT NULL,
  `ocr_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ocr_id`),
  UNIQUE KEY `ocr_consumer_key` (`ocr_consumer_key`,`ocr_usa_id_ref`,`ocr_server_uri`),
  KEY `ocr_server_uri` (`ocr_server_uri`),
  KEY `ocr_server_uri_host` (`ocr_server_uri_host`,`ocr_server_uri_path`),
  KEY `ocr_usa_id_ref` (`ocr_usa_id_ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_consumer_token`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_consumer_token` (
  `oct_id` int(11) NOT NULL AUTO_INCREMENT,
  `oct_ocr_id_ref` int(11) NOT NULL,
  `oct_usa_id_ref` int(11) NOT NULL,
  `oct_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `oct_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `oct_token_secret` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `oct_token_type` enum('request','authorized','access') DEFAULT NULL,
  `oct_token_ttl` datetime NOT NULL DEFAULT '9999-12-31 00:00:00',
  `oct_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oct_id`),
  UNIQUE KEY `oct_ocr_id_ref` (`oct_ocr_id_ref`,`oct_token`),
  UNIQUE KEY `oct_usa_id_ref` (`oct_usa_id_ref`,`oct_ocr_id_ref`,`oct_token_type`,`oct_name`),
  KEY `oct_token_ttl` (`oct_token_ttl`),
  CONSTRAINT `oauth_consumer_token_ibfk_1` FOREIGN KEY (`oct_ocr_id_ref`) REFERENCES `oauth_consumer_registry` (`ocr_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_log` (
  `olg_id` int(11) NOT NULL AUTO_INCREMENT,
  `olg_osr_consumer_key` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `olg_ost_token` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `olg_ocr_consumer_key` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `olg_oct_token` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `olg_usa_id_ref` int(11) DEFAULT NULL,
  `olg_received` text NOT NULL,
  `olg_sent` text NOT NULL,
  `olg_base_string` text NOT NULL,
  `olg_notes` text NOT NULL,
  `olg_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `olg_remote_ip` bigint(20) NOT NULL,
  PRIMARY KEY (`olg_id`),
  KEY `olg_osr_consumer_key` (`olg_osr_consumer_key`,`olg_id`),
  KEY `olg_ost_token` (`olg_ost_token`,`olg_id`),
  KEY `olg_ocr_consumer_key` (`olg_ocr_consumer_key`,`olg_id`),
  KEY `olg_oct_token` (`olg_oct_token`,`olg_id`),
  KEY `olg_usa_id_ref` (`olg_usa_id_ref`,`olg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_server_nonce`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_server_nonce` (
  `osn_id` int(11) NOT NULL AUTO_INCREMENT,
  `osn_consumer_key` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `osn_token` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `osn_timestamp` bigint(20) NOT NULL,
  `osn_nonce` varchar(80) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`osn_id`),
  UNIQUE KEY `osn_consumer_key` (`osn_consumer_key`,`osn_token`,`osn_timestamp`,`osn_nonce`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_server_registry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_server_registry` (
  `osr_id` int(11) NOT NULL AUTO_INCREMENT,
  `osr_usa_id_ref` int(11) DEFAULT NULL,
  `osr_consumer_key` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `osr_consumer_secret` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `osr_enabled` tinyint(1) NOT NULL DEFAULT '1',
  `osr_status` varchar(16) NOT NULL,
  `osr_requester_name` varchar(64) NOT NULL,
  `osr_requester_email` varchar(64) NOT NULL,
  `osr_callback_uri` varchar(255) NOT NULL,
  `osr_application_uri` varchar(255) NOT NULL,
  `osr_application_title` varchar(80) NOT NULL,
  `osr_application_descr` text NOT NULL,
  `osr_application_notes` text NOT NULL,
  `osr_application_type` varchar(20) NOT NULL,
  `osr_application_commercial` tinyint(1) NOT NULL DEFAULT '0',
  `osr_issue_date` datetime NOT NULL,
  `osr_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`osr_id`),
  UNIQUE KEY `osr_consumer_key` (`osr_consumer_key`),
  KEY `osr_usa_id_ref` (`osr_usa_id_ref`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_server_token`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_server_token` (
  `ost_id` int(11) NOT NULL AUTO_INCREMENT,
  `ost_osr_id_ref` int(11) NOT NULL,
  `ost_usa_id_ref` int(11) NOT NULL,
  `ost_token` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ost_token_secret` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ost_token_type` enum('request','access') DEFAULT NULL,
  `ost_authorized` tinyint(1) NOT NULL DEFAULT '0',
  `ost_referrer_host` varchar(128) NOT NULL DEFAULT '',
  `ost_token_ttl` datetime NOT NULL DEFAULT '9999-12-31 00:00:00',
  `ost_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ost_verifier` char(10) DEFAULT NULL,
  `ost_callback_url` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`ost_id`),
  UNIQUE KEY `ost_token` (`ost_token`),
  KEY `ost_osr_id_ref` (`ost_osr_id_ref`),
  KEY `ost_token_ttl` (`ost_token_ttl`),
  CONSTRAINT `oauth_server_token_ibfk_1` FOREIGN KEY (`ost_osr_id_ref`) REFERENCES `oauth_server_registry` (`osr_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `paidemotesent`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `paidemotesent` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `EmoteID` int(11) NOT NULL,
  `PurchaseLocation` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_EmoteID` (`EmoteID`),
  KEY `FK_Username` (`Username`),
  CONSTRAINT `FK_EmoteID` FOREIGN KEY (`EmoteID`) REFERENCES `emotecommand` (`ID`),
  CONSTRAINT `FK_Username` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=90585 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `paintwarsitem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `paintwarsitem` (
  `ID` int(11) DEFAULT NULL,
  `Name` varchar(128) DEFAULT NULL,
  `Description` varchar(256) DEFAULT NULL,
  `Currency` varchar(6) DEFAULT NULL,
  `Price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partner`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partner` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL,
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partneractiveuser`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partneractiveuser` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `UserID` (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partneragreement`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partneragreement` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PartnerID` int(11) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `FinderFee` double NOT NULL,
  `RevenueShare` double NOT NULL,
  `ProductSMS` double NOT NULL DEFAULT '0',
  `ProductVoice` double NOT NULL DEFAULT '0',
  `ProductGames` double NOT NULL DEFAULT '0',
  `ProductVG` double NOT NULL DEFAULT '0',
  `ProductOthers` double NOT NULL DEFAULT '0',
  `StartDate` date NOT NULL,
  `EndDate` date NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '2010-10-10 00:00:00',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partneragreementbuild`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partneragreementbuild` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PartnerAgreementID` int(11) NOT NULL,
  `PartnerBuildID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partnerbuild`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partnerbuild` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PartnerID` int(11) NOT NULL,
  `UserAgent` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL,
  `DownloadUrl` varchar(1024) DEFAULT NULL,
  `SmsMsg` varchar(160) DEFAULT NULL,
  `Platform` varchar(64) DEFAULT NULL,
  `Version` varchar(64) DEFAULT NULL,
  `Status` int(11) DEFAULT '0',
  `MainMenuVersion` int(11) DEFAULT '1',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partnerbuildmenuentry`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partnerbuildmenuentry` (
  `BuildID` int(11) NOT NULL,
  `MenuType` int(11) unsigned NOT NULL,
  `EntryID` int(11) unsigned NOT NULL,
  `Position` int(11) NOT NULL,
  PRIMARY KEY (`BuildID`,`MenuType`,`EntryID`),
  UNIQUE KEY `bme_unique_position` (`BuildID`,`MenuType`,`Position`),
  KEY `bme_entry_id` (`EntryID`),
  KEY `bme_build_id` (`BuildID`),
  CONSTRAINT `pbme_build_id` FOREIGN KEY (`BuildID`) REFERENCES `partnerbuild` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pbme_entry_id` FOREIGN KEY (`EntryID`) REFERENCES `menuentry` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A table representing the menu entries a given build has';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partnerbuildoption`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partnerbuildoption` (
  `BuildID` int(11) NOT NULL,
  `OptionID` int(11) unsigned NOT NULL,
  `Value` text NOT NULL,
  PRIMARY KEY (`BuildID`,`OptionID`),
  KEY `bo_build_id` (`BuildID`),
  KEY `bo_option_id` (`OptionID`),
  CONSTRAINT `bo_build_id` FOREIGN KEY (`BuildID`) REFERENCES `partnerbuild` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bo_option_id` FOREIGN KEY (`OptionID`) REFERENCES `partnerbuildoptiondefinition` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partnerbuildoptiondefinition`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partnerbuildoptiondefinition` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `Name` varchar(60) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partnerstat`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partnerstat` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PartnerBuildID` int(11) NOT NULL,
  `UniqueUsers` int(11) NOT NULL DEFAULT '0',
  `Registration` int(11) NOT NULL DEFAULT '0',
  `Authentication` int(11) NOT NULL DEFAULT '0',
  `ActiveUsers` int(11) NOT NULL DEFAULT '0',
  `CreditSpending` double NOT NULL DEFAULT '0',
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PartnerBuildID` (`PartnerBuildID`,`DateCreated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partneruser`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partneruser` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PartnerID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Membership` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `partnervasworld`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partnervasworld` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) DEFAULT NULL,
  `Content` text,
  `Status` int(1) NOT NULL DEFAULT '0',
  `Remarks` text,
  `DateCreated` datetime NOT NULL,
  `DateUpdated` datetime NOT NULL,
  `PartnerAgreementID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `paymentmetadetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `paymentmetadetails` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `detail` varchar(128) NOT NULL DEFAULT '',
  `type` tinyint(2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_unq_detail` (`detail`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payments`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payments` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `datecreated` datetime NOT NULL,
  `vendortransactionid` varchar(128) NOT NULL DEFAULT '',
  `userid` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `dateupdated` datetime DEFAULT NULL,
  `type` tinyint(2) NOT NULL,
  `description` blob,
  `amount` double NOT NULL,
  `currency` varchar(128) NOT NULL DEFAULT '',
  `exchangerateUSD` double(15,8) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_userid` (`userid`),
  KEY `id_pymnt_type` (`type`),
  KEY `idx_pymnt_d8updated` (`dateupdated`)
) ENGINE=InnoDB AUTO_INCREMENT=35145 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `paymenttopaymentmetadetails`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `paymenttopaymentmetadetails` (
  `paymentid` int(11) unsigned NOT NULL,
  `paymentmetadetailsid` int(11) NOT NULL,
  PRIMARY KEY (`paymentid`,`paymentmetadetailsid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pendingcontact`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pendingcontact` (
  `username` varchar(128) NOT NULL DEFAULT '',
  `pendingContact` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`username`,`pendingContact`) USING BTREE,
  KEY `FK_pendingContact_2` (`pendingContact`),
  CONSTRAINT `FK_pendingContact_1` FOREIGN KEY (`username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_pendingContact_2` FOREIGN KEY (`pendingContact`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `phonecall`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phonecall` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `ContactID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Source` varchar(128) NOT NULL DEFAULT '',
  `SourceType` int(11) NOT NULL,
  `SourceIDDCode` int(11) DEFAULT NULL,
  `Destination` varchar(128) NOT NULL DEFAULT '',
  `DestinationType` int(11) NOT NULL,
  `DestinationIDDCode` int(11) DEFAULT NULL,
  `MakeReceive` int(11) NOT NULL DEFAULT '0',
  `InitialLeg` int(11) NOT NULL DEFAULT '0',
  `SourceDuration` bigint(11) DEFAULT NULL,
  `DestinationDuration` bigint(11) DEFAULT NULL,
  `BilledDuration` bigint(11) DEFAULT NULL,
  `SignallingFee` double(10,6) DEFAULT NULL,
  `Rate` double(10,6) DEFAULT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `Claimable` int(11) NOT NULL DEFAULT '0',
  `Gateway` int(11) DEFAULT NULL,
  `SourceProvider` int(11) DEFAULT NULL,
  `DestinationProvider` int(11) DEFAULT NULL,
  `FailReasonCode` int(11) DEFAULT NULL,
  `FailReason` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_call_1` (`Username`),
  KEY `FK_call_2` (`ContactID`),
  KEY `idx_Status` (`Status`),
  KEY `idx_DateCreated` (`DateCreated`),
  KEY `idx_SourceIDDCode` (`SourceIDDCode`,`DateCreated`),
  KEY `idx_DestinationIDDCode` (`DestinationIDDCode`,`DateCreated`),
  CONSTRAINT `FK_call_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_call_2` FOREIGN KEY (`ContactID`) REFERENCES `contact` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pot`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pot` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `BotID` int(11) NOT NULL,
  `BotInstanceID` varchar(128) NOT NULL,
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DatePaidOut` timestamp NULL DEFAULT NULL,
  `RakeAmount` double(12,2) DEFAULT NULL,
  `RakeFundedAmount` double(12,2) DEFAULT NULL,
  `RakePercent` double(5,2) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `BotID` (`BotID`),
  KEY `Status` (`Status`),
  CONSTRAINT `pot_ibfk_1` FOREIGN KEY (`BotID`) REFERENCES `bot` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=57572911 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `potstake`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `potstake` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PotID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Amount` double(12,2) DEFAULT NULL,
  `FundedAmount` double(12,2) DEFAULT NULL,
  `Currency` varchar(3) DEFAULT NULL,
  `ExchangeRate` double(15,4) DEFAULT NULL,
  `Eligible` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `PotID` (`PotID`),
  KEY `UserID` (`UserID`),
  CONSTRAINT `potstake_ibfk_1` FOREIGN KEY (`PotID`) REFERENCES `pot` (`ID`),
  CONSTRAINT `potstake_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=326307754 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `premiumsmspayment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `premiumsmspayment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `SystemSMSID` int(11) NOT NULL DEFAULT '0',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Amount` double(10,2) NOT NULL DEFAULT '0.00',
  `Fee` double(10,2) NOT NULL DEFAULT '0.00',
  `ResponseCode` varchar(128) DEFAULT '',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_PremiumSMSPayment_1` (`Username`),
  KEY `FK_PremiumSMSPayment_2` (`SystemSMSID`),
  CONSTRAINT `FK_PremiumSMSPayment_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_PremiumSMSPayment_2` FOREIGN KEY (`SystemSMSID`) REFERENCES `systemsms` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prepaidcard`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prepaidcard` (
  `CardNumber` int(11) NOT NULL DEFAULT '0',
  `PIN` int(11) NOT NULL DEFAULT '0',
  `Value` double(10,2) NOT NULL DEFAULT '0.00',
  `Type` int(11) NOT NULL DEFAULT '0',
  `Used` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`CardNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promotedpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promotedpost` (
  `url` varchar(128) NOT NULL,
  `slot` int(11) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `startdate` datetime NOT NULL,
  `enddate` datetime NOT NULL,
  `countryid` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`url`),
  KEY `idx_promotedpost1` (`startdate`),
  KEY `idx_promotedpost2` (`enddate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_blob_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_blob_triggers` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `quartz_blob_triggers_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `quartz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_calendars`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_calendars` (
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_cron_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_cron_triggers` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `quartz_cron_triggers_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `quartz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_fired_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_fired_triggers` (
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_STATEFUL` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_job_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_job_details` (
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `IS_STATEFUL` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_job_listeners`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_job_listeners` (
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `JOB_LISTENER` varchar(200) NOT NULL,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `quartz_job_listeners_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `quartz_job_details` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_locks`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_locks` (
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_paused_trigger_grps`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_paused_trigger_grps` (
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_scheduler_state`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_scheduler_state` (
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_simple_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_simple_triggers` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(7) NOT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `quartz_simple_triggers_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `quartz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_trigger_listeners`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_trigger_listeners` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `TRIGGER_LISTENER` varchar(200) NOT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `quartz_trigger_listeners_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `quartz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quartz_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quartz_triggers` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `quartz_triggers_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `quartz_job_details` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recentchatrooms`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recentchatrooms` (
  `Username` varchar(255) NOT NULL DEFAULT '',
  `ChatRoomNames` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`Username`),
  CONSTRAINT `FK_RecentChatRooms_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommendationtransform`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommendationtransform` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `hivescript` text,
  `hive_precompile_script` text,
  `hive_retrieval_query` text,
  `hivescripturl` varchar(256) DEFAULT NULL,
  `inithivescript` text,
  `inithivescripturl` varchar(256) DEFAULT NULL,
  `runinterval` bigint(20) NOT NULL,
  `reuseinstance` tinyint(1) DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `expiry` bigint(20) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `sub_type` varchar(10) DEFAULT NULL,
  `domain` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`(255))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommendationtransformglobalparam`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommendationtransformglobalparam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `beanshell` text,
  `python` text,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommendationtransformparam`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommendationtransformparam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transformid` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `value` varchar(256) DEFAULT NULL,
  `globalparamid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`(255)),
  KEY `transformid` (`transformid`),
  KEY `globalparamid` (`globalparamid`),
  CONSTRAINT `recommendationtransformparam_ibfk_1` FOREIGN KEY (`transformid`) REFERENCES `recommendationtransform` (`id`),
  CONSTRAINT `recommendationtransformparam_ibfk_2` FOREIGN KEY (`globalparamid`) REFERENCES `recommendationtransformglobalparam` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `referralsource`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `referralsource` (
  `ID` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `Code` varchar(16) NOT NULL,
  `Description` varchar(128) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `idx_code` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `registrationcontext`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registrationcontext` (
  `userid` int(10) unsigned NOT NULL,
  `type` int(11) NOT NULL,
  `value` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`userid`,`type`),
  CONSTRAINT `FK_registrationcontext_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `registrationdevice`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registrationdevice` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(10) unsigned DEFAULT NULL,
  `MobileDevice` varchar(128) DEFAULT NULL,
  `UserAgent` varchar(256) DEFAULT NULL,
  `IMEI` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_RegistrationDevice_1` (`UserID`),
  KEY `IDX_RegistrationDevice_1` (`UserAgent`(255)),
  CONSTRAINT `FK_RegistrationDevice_1` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27514126 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reportschedule`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reportschedule` (
  `id` int(3) unsigned NOT NULL AUTO_INCREMENT,
  `query` text NOT NULL,
  `database` enum('olap','ods','hadoop') NOT NULL DEFAULT 'olap',
  `minute` smallint(2) unsigned NOT NULL,
  `hour` smallint(2) unsigned NOT NULL,
  `date` smallint(2) unsigned DEFAULT NULL,
  `day` tinyint(1) unsigned DEFAULT NULL,
  `month` smallint(2) unsigned DEFAULT NULL,
  `title` varchar(255) NOT NULL DEFAULT '',
  `message` text,
  `emailaddress` text,
  `createdby` varchar(255) DEFAULT NULL,
  `datecreated` timestamp NULL DEFAULT NULL,
  `dateupdated` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `title` (`title`),
  KEY `createdby` (`createdby`),
  CONSTRAINT `reportschedule_ibfk_1` FOREIGN KEY (`createdby`) REFERENCES `staff` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reputationlevelpermission`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reputationlevelpermission` (
  `id` int(10) unsigned NOT NULL,
  `action` varchar(128) NOT NULL DEFAULT '',
  `baseMigLevel` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reputationscoretolevel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reputationscoretolevel` (
  `Score` int(11) NOT NULL,
  `Level` int(11) NOT NULL,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Image` varchar(256) NOT NULL DEFAULT '',
  `CreateChatRoom` int(11) NOT NULL,
  `ChatRoomSize` int(11) NOT NULL,
  `CreateGroup` int(11) NOT NULL,
  `GroupSize` int(11) NOT NULL,
  `NumGroupModerators` int(11) NOT NULL,
  `GroupStorageSize` int(11) NOT NULL DEFAULT '0',
  `NumGroupChatRooms` int(11) NOT NULL,
  `PublishPhoto` int(11) NOT NULL,
  `PostCommentLikeUserWall` int(11) NOT NULL,
  `AddToPhotoWall` int(11) NOT NULL,
  `EnterPot` int(11) NOT NULL,
  `UseDisplayPicture` int(11) NOT NULL,
  `PlayMigWars` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reputationscoretolevel_orig`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reputationscoretolevel_orig` (
  `score` int(10) unsigned NOT NULL,
  `level` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `image` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reseller`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reseller` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CountryID` int(11) NOT NULL DEFAULT '0',
  `State` varchar(128) NOT NULL,
  `City` varchar(128) NOT NULL DEFAULT '',
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Address` varchar(128) DEFAULT NULL,
  `PhoneNumber` varchar(128) DEFAULT NULL,
  `PhoneNumberToDisplay` varchar(128) DEFAULT NULL,
  `PhoneNumber2` varchar(128) DEFAULT NULL,
  `PhoneNumber2ToDisplay` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_Reseller_1` (`CountryID`),
  CONSTRAINT `FK_Reseller_1` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardcriteria`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardcriteria` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RewardProgramID` int(11) NOT NULL,
  `Type` int(11) NOT NULL,
  `RewardFrequency` int(11) NOT NULL,
  `QuantityRequired` int(11) NOT NULL,
  `AmountRequired` double(12,2) NOT NULL,
  `AmountRequiredCurrency` varchar(6) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_RewardCriteria_RewardProgramID` (`RewardProgramID`),
  CONSTRAINT `FK_RewardCriteria_RewardProgramID` FOREIGN KEY (`RewardProgramID`) REFERENCES `rewardprogram` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogram`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogram` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `Description` varchar(128) NOT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `Type` int(11) NOT NULL,
  `RewardFrequency` int(11) NOT NULL,
  `QuantityRequired` int(11) NOT NULL,
  `AmountRequired` double(12,2) NOT NULL,
  `AmountRequiredCurrency` varchar(6) NOT NULL,
  `ScoreReward` int(11) NOT NULL,
  `LevelReward` int(11) NOT NULL,
  `MigCreditReward` double(12,2) NOT NULL,
  `MigCreditRewardCurrency` varchar(6) NOT NULL,
  `IMNotification` text,
  `EmailNotification` text,
  `SMSNotification` text,
  `StartDate` datetime NOT NULL,
  `endDate` datetime DEFAULT NULL,
  `Status` int(11) NOT NULL,
  `ItemRewardType` int(11) NOT NULL DEFAULT '1',
  `MaxMigLevel` int(11) DEFAULT NULL,
  `MinMigLevel` int(11) DEFAULT NULL,
  `userType` int(11) DEFAULT NULL,
  `MaxCompletionRate` varchar(10) DEFAULT NULL,
  `Category` int(11) DEFAULT '0',
  `emailtemplateid` int(11) DEFAULT NULL,
  `emailtemplatedataprovider` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_RewardProgram_1` (`CountryID`),
  KEY `FK_RewardProgram_2` (`AmountRequiredCurrency`),
  KEY `FK_RewardProgram_3` (`MigCreditRewardCurrency`),
  KEY `emailtemplateid` (`emailtemplateid`),
  KEY `index_status_enddate` (`Status`,`endDate`),
  KEY `index_status_startdate_enddate` (`Status`,`StartDate`,`endDate`),
  CONSTRAINT `FK_RewardProgram_1` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_RewardProgram_2` FOREIGN KEY (`AmountRequiredCurrency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_RewardProgram_3` FOREIGN KEY (`MigCreditRewardCurrency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `rewardprogram_ibfk_1` FOREIGN KEY (`emailtemplateid`) REFERENCES `emailtemplate` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1095 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogramcompleted`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogramcompleted` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RewardProgramID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ScoreReward` int(11) NOT NULL,
  `LevelReward` int(11) NOT NULL,
  `MigCreditReward` double(12,2) NOT NULL,
  `MigCreditRewardCurrency` varchar(6) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_RewardProgramCompleted_1` (`RewardProgramID`),
  KEY `FK_RewardProgramCompleted_2` (`UserID`),
  KEY `FK_RewardProgramCompleted_3` (`MigCreditRewardCurrency`),
  CONSTRAINT `FK_RewardProgramCompleted_1` FOREIGN KEY (`RewardProgramID`) REFERENCES `rewardprogram` (`ID`),
  CONSTRAINT `FK_RewardProgramCompleted_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_RewardProgramCompleted_3` FOREIGN KEY (`MigCreditRewardCurrency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=191949583 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogramoutcomeprocessors`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogramoutcomeprocessors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rewardprogramid` int(11) NOT NULL,
  `sequence` smallint(6) NOT NULL DEFAULT '1',
  `classname` varchar(512) NOT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  KEY `rewardprogramid_idx` (`rewardprogramid`),
  CONSTRAINT `` FOREIGN KEY (`rewardprogramid`) REFERENCES `rewardprogram` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogramparameters`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogramparameters` (
  `rewardprogramID` int(11) NOT NULL,
  `ParamName` varchar(30) NOT NULL DEFAULT '',
  `ParamValue` text,
  PRIMARY KEY (`rewardprogramID`,`ParamName`),
  CONSTRAINT `FK_rewardprogramparameters_rewardprogram` FOREIGN KEY (`rewardprogramID`) REFERENCES `rewardprogram` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogramprocessormapping`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogramprocessormapping` (
  `programType` int(11) NOT NULL,
  `sequence` int(11) NOT NULL,
  `processorID` int(11) NOT NULL,
  PRIMARY KEY (`programType`,`processorID`),
  KEY `FK_rewardprogramprocessormapping_rewardprogramprocessor` (`processorID`),
  CONSTRAINT `FK_rewardprogramprocessormapping_rewardprogramprocessor` FOREIGN KEY (`processorID`) REFERENCES `rewardprogramprocessors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogramprocessors`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogramprocessors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shortName` varchar(128) NOT NULL,
  `className` varchar(128) NOT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardprogramstatehandler`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardprogramstatehandler` (
  `rewardprogramid` int(11) NOT NULL,
  `rewardprogramstatehandlerclassname` varchar(512) NOT NULL,
  PRIMARY KEY (`rewardprogramid`),
  CONSTRAINT `FK_rewardprogramstatehandler_rewardprogramid` FOREIGN KEY (`rewardprogramid`) REFERENCES `rewardprogram` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rewardscorecap`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rewardscorecap` (
  `level` int(11) NOT NULL,
  `category` int(11) NOT NULL,
  `scorecap` int(11) DEFAULT '0',
  PRIMARY KEY (`level`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `score`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `score` (
  `userid` int(10) unsigned NOT NULL,
  `score` int(11) NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`),
  CONSTRAINT `FK_score_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `score_orig`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `score_orig` (
  `userid` int(10) unsigned NOT NULL,
  `score` int(11) NOT NULL,
  `lastUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scrapbook`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scrapbook` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `FileID` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ReceivedFrom` varchar(128) NOT NULL,
  `Description` varchar(1024) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_Scrapbook_1` (`FileID`),
  KEY `FK_scrapbook_2` (`Username`),
  KEY `FK_scrapbook_3` (`ReceivedFrom`),
  CONSTRAINT `FK_Scrapbook_1` FOREIGN KEY (`FileID`) REFERENCES `file` (`ID`),
  CONSTRAINT `FK_scrapbook_2` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_scrapbook_3` FOREIGN KEY (`ReceivedFrom`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=290989788 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scrapbooklike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scrapbooklike` (
  `ScrapbookID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`ScrapbookID`,`UserID`),
  KEY `FK_scrapbooklike_userid` (`UserID`),
  CONSTRAINT `FK_scrapbooklike_scrapbookid` FOREIGN KEY (`ScrapbookID`) REFERENCES `scrapbook` (`ID`),
  CONSTRAINT `FK_scrapbooklike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `scrapbooklikesummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scrapbooklikesummary` (
  `ScrapbookID` int(11) NOT NULL,
  `NumLikes` int(11) NOT NULL DEFAULT '0',
  `NumDislikes` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ScrapbookID`),
  CONSTRAINT `FK_scrapbooklikesummary_scrapbookid` FOREIGN KEY (`ScrapbookID`) REFERENCES `scrapbook` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `securityquestion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `securityquestion` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `question` varchar(128) DEFAULT NULL,
  `type` tinyint(2) DEFAULT '1',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `Description` varchar(128) NOT NULL,
  `FreeTrialDays` int(11) NOT NULL,
  `DurationDays` int(11) NOT NULL,
  `AwardedCredit` double(12,2) NOT NULL,
  `AwardedCreditCurrency` varchar(6) NOT NULL,
  `BillingMethod` int(11) NOT NULL,
  `Cost` double(12,2) NOT NULL,
  `CostCurrency` varchar(6) NOT NULL,
  `BillingConfirmationSMS` varchar(160) DEFAULT NULL,
  `ExpiryReminderSMS` varchar(160) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Service_1` (`AwardedCreditCurrency`),
  KEY `FK_Service_2` (`CostCurrency`),
  CONSTRAINT `FK_Service_1` FOREIGN KEY (`AwardedCreditCurrency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_Service_2` FOREIGN KEY (`CostCurrency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sessionhistorysummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessionhistorysummary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `summarydate` date NOT NULL,
  `countryID` int(11) NOT NULL,
  `uniqueAuth1` int(11) NOT NULL,
  `uniqueNonAuth1` int(11) NOT NULL,
  `uniqueAuth7` int(11) NOT NULL,
  `uniqueNonAuth7` int(11) NOT NULL,
  `uniqueAuth30` int(11) NOT NULL,
  `uniqueNonAuth30` int(11) NOT NULL,
  `totalAuth` int(11) NOT NULL,
  `totalNonAuth` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UniqueDateCountry` (`summarydate`,`countryID`),
  KEY `FK_SessionHistorySummaryCountry` (`countryID`),
  CONSTRAINT `FK_SessionHistorySummaryCountry` FOREIGN KEY (`countryID`) REFERENCES `country` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=559732 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `smsgateway`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smsgateway` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Type` int(11) NOT NULL,
  `URL` varchar(128) NOT NULL,
  `Port` int(11) NOT NULL,
  `Method` int(11) NOT NULL,
  `IDDPrefix` varchar(128) DEFAULT NULL,
  `Authorization` varchar(128) DEFAULT NULL,
  `UsernameParam` varchar(128) DEFAULT NULL,
  `PasswordParam` varchar(128) DEFAULT NULL,
  `SourceParam` varchar(128) DEFAULT NULL,
  `DestinationParam` varchar(128) DEFAULT NULL,
  `MessageParam` varchar(128) DEFAULT NULL,
  `UnicodeMessageParam` varchar(128) DEFAULT NULL,
  `UnicodeParam` varchar(128) DEFAULT NULL,
  `ExtraParam` varchar(128) DEFAULT NULL,
  `UnicodeCharset` varchar(128) DEFAULT NULL,
  `SuccessPattern` varchar(128) DEFAULT NULL,
  `ErrorPattern` varchar(128) DEFAULT NULL,
  `DeliveryReporting` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `smsretrievepwstatus`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smsretrievepwstatus` (
  `username` varchar(128) NOT NULL,
  `retrieveTimes` int(11) NOT NULL DEFAULT '1',
  `retrieveDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`username`),
  CONSTRAINT `FK_SMSRetrievePWStatus_username` FOREIGN KEY (`username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `smsroute`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smsroute` (
  `IDDCode` int(11) NOT NULL DEFAULT '0',
  `AreaCode` varchar(128) NOT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `GatewayID` int(11) NOT NULL DEFAULT '0',
  `Priority` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IDDCode`,`AreaCode`,`Type`,`GatewayID`,`Priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `smswholesalecost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smswholesalecost` (
  `IDDCode` int(11) NOT NULL,
  `AreaCode` varchar(128) NOT NULL,
  `GatewayID` int(11) NOT NULL,
  `Cost` double(10,6) NOT NULL,
  `Currency` varchar(6) NOT NULL DEFAULT '',
  PRIMARY KEY (`IDDCode`,`AreaCode`,`GatewayID`),
  KEY `FK_SMSWholesaleCost_1` (`GatewayID`),
  KEY `FK_SMSWholesaleCost_2` (`Currency`),
  CONSTRAINT `FK_SMSWholesaleCost_1` FOREIGN KEY (`GatewayID`) REFERENCES `smsgateway` (`ID`),
  CONSTRAINT `FK_SMSWholesaleCost_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `Password` varchar(128) NOT NULL DEFAULT '',
  `Status` int(11) NOT NULL DEFAULT '0',
  `AccessLevel` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `idx_Username` (`Username`),
  KEY `idx_Status` (`Status`)
) ENGINE=InnoDB AUTO_INCREMENT=330 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff_08132010`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staff_08132010` (
  `Username` varchar(128) NOT NULL DEFAULT '',
  `Password` varchar(128) NOT NULL DEFAULT '',
  `AccessLevel` int(11) NOT NULL,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staffaccess`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `staffaccess` (
  `StaffId` int(11) NOT NULL DEFAULT '0',
  `MisAccessId` int(11) NOT NULL DEFAULT '0',
  KEY `idx_staff_username` (`StaffId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stafflogin`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stafflogin` (
  `staffusername` varchar(128) NOT NULL,
  `datecreated` datetime NOT NULL,
  `ipaddress` varchar(128) NOT NULL,
  `loginsuccessful` int(11) NOT NULL,
  PRIMARY KEY (`staffusername`,`datecreated`),
  CONSTRAINT `stafflogin_ibfk_1` FOREIGN KEY (`staffusername`) REFERENCES `staff` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storecategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storecategory` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `ParentStoreCategoryID` int(11) DEFAULT NULL,
  `SortOrder` int(11) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_storecategory` (`ParentStoreCategoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitem`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitem` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Type` int(11) NOT NULL,
  `ReferenceID` int(11) DEFAULT NULL,
  `Name` varchar(100) NOT NULL,
  `Description` varchar(2000) DEFAULT NULL,
  `Price` double NOT NULL DEFAULT '0',
  `Currency` varchar(128) NOT NULL,
  `Status` int(11) DEFAULT '1',
  `NumAvailable` int(11) DEFAULT NULL,
  `NumSold` int(11) NOT NULL DEFAULT '0',
  `Featured` int(11) NOT NULL DEFAULT '0',
  `CatalogImage` varchar(2000) NOT NULL,
  `PreviewImage` varchar(2000) NOT NULL,
  `ForSale` int(11) DEFAULT '1',
  `SortOrder` int(11) DEFAULT '0',
  `ExpiryDate` datetime DEFAULT NULL,
  `DateListed` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `migLevelMin` int(11) DEFAULT '1',
  `GroupID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_storeitem_groupid` (`GroupID`),
  KEY `IDX_StoreItem_1` (`Type`,`ReferenceID`),
  KEY `Name` (`Name`),
  KEY `Description` (`Description`(255)),
  CONSTRAINT `FK_storeitem_groupid` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7928 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitemcategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitemcategory` (
  `StoreItemID` int(11) NOT NULL,
  `StoreCategoryID` int(11) NOT NULL,
  PRIMARY KEY (`StoreItemID`,`StoreCategoryID`),
  KEY `FK_StoreCategory_2` (`StoreCategoryID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeiteminventory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeiteminventory` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `StoreItemID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `Location` int(10) unsigned NOT NULL DEFAULT '1',
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_storeiteminventory_userid` (`UserID`),
  KEY `FK_storeiteminventory_storeitemid` (`StoreItemID`),
  CONSTRAINT `FK_storeiteminventory_storeitemid` FOREIGN KEY (`StoreItemID`) REFERENCES `storeitem` (`ID`),
  CONSTRAINT `FK_storeiteminventory_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=505 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeiteminventoryreceived`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeiteminventoryreceived` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `StoreItemInventoryID` bigint(20) NOT NULL,
  `ReceiverUserID` int(10) unsigned NOT NULL,
  `ReferenceID` int(11) DEFAULT NULL,
  `ReferenceType` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_storeiteminventoryreceived_storeiteminventoryid` (`StoreItemInventoryID`),
  KEY `FK_storeiteminventoryreceived_receiveruserid` (`ReceiverUserID`),
  CONSTRAINT `FK_storeiteminventoryreceived_receiveruserid` FOREIGN KEY (`ReceiverUserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_storeiteminventoryreceived_storeiteminventoryid` FOREIGN KEY (`StoreItemInventoryID`) REFERENCES `storeiteminventory` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitemrating`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitemrating` (
  `StoreItemID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Rating` tinyint(2) NOT NULL,
  PRIMARY KEY (`StoreItemID`,`UserID`),
  KEY `FK_storeitemrating_userid` (`UserID`),
  CONSTRAINT `FK_storeitemrating_storeitemid` FOREIGN KEY (`StoreItemID`) REFERENCES `storeitem` (`ID`),
  CONSTRAINT `FK_storeitemrating_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitemratingsummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitemratingsummary` (
  `StoreItemID` int(11) NOT NULL,
  `Average` double(7,5) unsigned NOT NULL DEFAULT '0.00000',
  `Total` int(11) NOT NULL DEFAULT '0',
  `NumRatings` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`StoreItemID`),
  CONSTRAINT `FK_storeitemratingsummary_storeitemid` FOREIGN KEY (`StoreItemID`) REFERENCES `storeitem` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitemrevenueshare`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitemrevenueshare` (
  `storeItemID` int(11) NOT NULL,
  `revenueShare` decimal(5,2) unsigned NOT NULL,
  PRIMARY KEY (`storeItemID`),
  CONSTRAINT `FK_storeitemrevenueshare_storeitemid` FOREIGN KEY (`storeItemID`) REFERENCES `storeitem` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitemreward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitemreward` (
  `RewardProgramID` int(11) NOT NULL,
  `StoreItemID` int(11) NOT NULL,
  PRIMARY KEY (`RewardProgramID`,`StoreItemID`),
  KEY `FK_StoreItemReward_2` (`StoreItemID`),
  CONSTRAINT `FK_StoreItemReward_1` FOREIGN KEY (`RewardProgramID`) REFERENCES `rewardprogram` (`ID`),
  CONSTRAINT `FK_StoreItemReward_2` FOREIGN KEY (`StoreItemID`) REFERENCES `storeitem` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storeitemrewarded`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storeitemrewarded` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RewardProgramCompletedID` int(11) NOT NULL,
  `StoreItemID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_StoreItemRewarded_1` (`RewardProgramCompletedID`),
  KEY `FK_StoreItemRewarded_2` (`StoreItemID`),
  CONSTRAINT `FK_StoreItemRewarded_1` FOREIGN KEY (`RewardProgramCompletedID`) REFERENCES `rewardprogramcompleted` (`ID`),
  CONSTRAINT `FK_StoreItemRewarded_2` FOREIGN KEY (`StoreItemID`) REFERENCES `storeitem` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscription`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscription` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `ServiceID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` int(11) NOT NULL,
  `IPAddress` varchar(128) DEFAULT NULL,
  `MobilePhone` varchar(128) DEFAULT NULL,
  `ExpiryDate` datetime NOT NULL,
  `ExpiryReminderSent` int(11) NOT NULL,
  `CancellationDate` datetime DEFAULT NULL,
  `BillingAttempts` int(11) NOT NULL,
  `LastBillingAttempt` datetime DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Subscription_1` (`Username`),
  KEY `FK_Subscription_2` (`ServiceID`),
  KEY `IDX_Subscription_1` (`MobilePhone`),
  CONSTRAINT `FK_Subscription_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_Subscription_2` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sweepstakescode`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sweepstakescode` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `userReferralID` int(11) NOT NULL,
  `code` varchar(10) NOT NULL DEFAULT 'X',
  `dateCreated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `username` varchar(128) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Index_code_1` (`code`),
  KEY `FK_SweepsCode_2` (`username`),
  KEY `FK_SweepsCode_1` (`userReferralID`),
  CONSTRAINT `FK_SweepsCode_1` FOREIGN KEY (`userReferralID`) REFERENCES `userreferral` (`ID`),
  CONSTRAINT `FK_SweepsCode_2` FOREIGN KEY (`username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system` (
  `PropertyName` varchar(128) NOT NULL DEFAULT '',
  `PropertyValue` text NOT NULL,
  PRIMARY KEY (`PropertyName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `systemsms`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `systemsms` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) DEFAULT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Type` int(11) NOT NULL DEFAULT '0',
  `SubType` int(11) NOT NULL,
  `Source` varchar(128) NOT NULL DEFAULT '',
  `Destination` varchar(128) NOT NULL DEFAULT '',
  `IDDCode` int(11) NOT NULL DEFAULT '0',
  `MessageText` text NOT NULL,
  `Gateway` int(11) DEFAULT NULL,
  `DateDispatched` datetime DEFAULT NULL,
  `ProviderTransactionID` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_systemsms_1` (`Username`),
  KEY `Index_SystemSMS_1` (`Destination`,`DateCreated`),
  KEY `Index_SystemSMS_2` (`Status`),
  KEY `Index_SystemSMS_3` (`DateCreated`),
  CONSTRAINT `FK_systemsms_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=217050098 DEFAULT CHARSET=utf8 COMMENT='InnoDB free: 10240 kB; (`Username`) REFER `fusion/user`(`Use';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test_emoticon`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_emoticon` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `EmoticonPackID` int(11) NOT NULL,
  `Type` int(11) NOT NULL,
  `Alias` varchar(128) NOT NULL DEFAULT '',
  `Width` int(11) NOT NULL DEFAULT '0',
  `Height` int(11) NOT NULL DEFAULT '0',
  `Location` varchar(128) NOT NULL,
  `LocationPNG` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `FK_emoticon_1` (`EmoticonPackID`)
) ENGINE=InnoDB AUTO_INCREMENT=4062 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test_virtualgift`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_virtualgift` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) DEFAULT NULL,
  `HotKey` varchar(128) DEFAULT NULL,
  `Price` double NOT NULL,
  `Currency` varchar(6) NOT NULL,
  `NumAvailable` int(11) DEFAULT NULL,
  `NumSold` int(11) NOT NULL DEFAULT '0',
  `SortOrder` int(11) DEFAULT NULL,
  `GroupID` int(11) DEFAULT NULL,
  `GroupVIPOnly` tinyint(1) DEFAULT NULL,
  `Location12x12GIF` varchar(128) DEFAULT NULL,
  `Location12x12PNG` varchar(128) DEFAULT NULL,
  `Location14x14GIF` varchar(128) DEFAULT NULL,
  `Location14x14PNG` varchar(128) DEFAULT NULL,
  `Location16x16GIF` varchar(128) DEFAULT NULL,
  `Location16x16PNG` varchar(128) DEFAULT NULL,
  `Location64x64PNG` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  `GiftAllMessage` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupID` (`GroupID`),
  KEY `Name` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=3855 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `testpart`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `testpart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`,`createdate`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1
/*!50100 PARTITION BY RANGE (unix_timestamp(createdate))
(PARTITION p0 VALUES LESS THAN (1397520000) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (1397692800) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (1397779200) ENGINE = InnoDB) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `theme`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `theme` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Description` varchar(128) NOT NULL DEFAULT '',
  `Location` varchar(128) NOT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thirdpartyapplication`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thirdpartyapplication` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL,
  `DisplayName` varchar(128) NOT NULL,
  `CompanyName` varchar(128) NOT NULL,
  `URL` varchar(128) NOT NULL DEFAULT '',
  `Secret` varchar(40) DEFAULT NULL,
  `IPWhiteList` text,
  `Type` enum('GAME') DEFAULT 'GAME',
  `DateCreated` datetime NOT NULL,
  `CreatedBy` varchar(128) DEFAULT NULL,
  `DateLastModified` datetime DEFAULT NULL,
  `LastModifiedBy` varchar(128) DEFAULT NULL,
  `Notes` text,
  `oauth_consumer_key` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `Status` tinyint(1) NOT NULL DEFAULT '0',
  `Description` text NOT NULL,
  `GroupID` int(11) DEFAULT NULL,
  `PlayLinkText` varchar(128) DEFAULT NULL,
  `MinMigLevel` tinyint(4) DEFAULT NULL,
  `ReleaseDate` datetime DEFAULT NULL,
  `SortOrder` int(11) DEFAULT '0',
  `AuthorizeCallbackUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `K_NAME` (`Name`),
  UNIQUE KEY `unq_GroupID` (`GroupID`),
  KEY `oauth_consumer_key` (`oauth_consumer_key`),
  CONSTRAINT `fk_GroupID` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`),
  CONSTRAINT `thirdpartyapplication_ibfk_1` FOREIGN KEY (`oauth_consumer_key`) REFERENCES `oauth_server_registry` (`osr_consumer_key`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thirdpartyapplicationinvitation`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thirdpartyapplicationinvitation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ThirdPartyApplicationID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `InviterUserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `DateAccepted` datetime DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ThirdPartyApplicationID`,`UserID`),
  UNIQUE KEY `ID` (`ID`),
  KEY `FK_ThirdPartyApplicationInvitation_2` (`UserID`),
  KEY `FK_ThirdPartyApplicationInvitation_3` (`InviterUserID`),
  CONSTRAINT `FK_ThirdPartyApplicationInvitation_1` FOREIGN KEY (`ThirdPartyApplicationID`) REFERENCES `thirdpartyapplication` (`ID`),
  CONSTRAINT `FK_ThirdPartyApplicationInvitation_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_ThirdPartyApplicationInvitation_3` FOREIGN KEY (`InviterUserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thirdpartyapplicationview`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thirdpartyapplicationview` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ThirdPartyApplicationID` int(11) NOT NULL,
  `View` enum('midlet','wap','ajax','touch','json','blackberry') NOT NULL DEFAULT 'midlet',
  `URL` varchar(255) NOT NULL DEFAULT '',
  `Status` tinyint(4) NOT NULL DEFAULT '0',
  `PlayLinkText` varchar(128) DEFAULT 'Play Now',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `tpav_one_view_per_app` (`ThirdPartyApplicationID`,`View`),
  UNIQUE KEY `tpav_all_urls_unique` (`URL`),
  KEY `tpav_app_id` (`ThirdPartyApplicationID`),
  CONSTRAINT `tpav_app_id` FOREIGN KEY (`ThirdPartyApplicationID`) REFERENCES `thirdpartyapplication` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unlockedstoreitemreward`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unlockedstoreitemreward` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `rewardprogramid` int(11) NOT NULL,
  `storeitemid` int(11) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_storeiteminventoryreward_rewardprogramid` (`rewardprogramid`),
  KEY `FK_storeiteminventoryreward_storeitemid` (`storeitemid`),
  CONSTRAINT `FK_storeiteminventoryreward_rewardprogramid` FOREIGN KEY (`rewardprogramid`) REFERENCES `rewardprogram` (`ID`),
  CONSTRAINT `FK_storeiteminventoryreward_storeitemid` FOREIGN KEY (`storeitemid`) REFERENCES `storeitem` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unlockedstoreitemrewarded`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unlockedstoreitemrewarded` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `rewardprogramcompletedid` bigint(20) NOT NULL,
  `StoreItemInventoryID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDX_storeiteminventoryrewarded_rewardprogramcompletedid` (`rewardprogramcompletedid`),
  KEY `IDX_storeiteminventoryrewarded_storeiteminventoryid` (`StoreItemInventoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=483 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateRegistered` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Password` varchar(128) NOT NULL DEFAULT '',
  `DisplayName` varchar(128) DEFAULT NULL,
  `DisplayPicture` varchar(128) DEFAULT NULL,
  `StatusMessage` varchar(128) DEFAULT NULL,
  `StatusTimeStamp` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CountryID` int(11) DEFAULT NULL,
  `Level` int(11) NOT NULL DEFAULT '1',
  `Language` varchar(3) NOT NULL DEFAULT 'ENG',
  `EmailAddress` varchar(128) DEFAULT NULL,
  `OnMailingList` int(11) DEFAULT NULL,
  `ChatRoomAdmin` int(11) NOT NULL DEFAULT '0',
  `ChatRoomBans` int(11) NOT NULL DEFAULT '0',
  `ContactListVersion` int(11) NOT NULL DEFAULT '0',
  `MSNUsername` varchar(128) DEFAULT NULL,
  `MSNPassword` varchar(128) DEFAULT NULL,
  `YahooUsername` varchar(128) DEFAULT NULL,
  `YahooPassword` varchar(128) DEFAULT NULL,
  `AIMUsername` varchar(128) DEFAULT NULL,
  `AIMPassword` varchar(128) DEFAULT NULL,
  `GTalkUsername` varchar(128) DEFAULT NULL,
  `GTalkPassword` varchar(128) DEFAULT NULL,
  `RegistrationIPAddress` varchar(128) DEFAULT NULL,
  `RegistrationDevice` varchar(128) DEFAULT NULL,
  `FirstLoginDate` datetime DEFAULT NULL,
  `LastLoginDate` datetime DEFAULT NULL,
  `FailedLoginAttempts` int(11) NOT NULL DEFAULT '0',
  `FailedActivationAttempts` int(11) NOT NULL DEFAULT '0',
  `MobilePhone` varchar(128) DEFAULT NULL,
  `MobileDevice` varchar(128) DEFAULT NULL,
  `UserAgent` varchar(128) DEFAULT NULL,
  `MobileVerified` int(11) DEFAULT NULL,
  `VerificationCode` varchar(128) NOT NULL DEFAULT '',
  `EmailActivated` int(11) NOT NULL DEFAULT '0',
  `EmailActivationDate` datetime DEFAULT NULL,
  `AllowBuzz` int(11) NOT NULL DEFAULT '1',
  `EmailAlert` int(11) NOT NULL DEFAULT '0',
  `EmailAlertSent` int(11) NOT NULL DEFAULT '0',
  `UTCOffset` double(2,1) DEFAULT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `AffiliateID` int(11) DEFAULT NULL,
  `MerchantCreated` varchar(128) DEFAULT NULL,
  `ReferredBy` varchar(128) DEFAULT NULL,
  `ReferralLevel` int(11) DEFAULT NULL,
  `BonusProgramID` int(11) DEFAULT NULL,
  `Balance` double(12,2) NOT NULL DEFAULT '0.00',
  `FundedBalance` double(12,2) NOT NULL DEFAULT '0.00',
  `FailedVoucherRecharges` int(11) NOT NULL DEFAULT '0',
  `LastFailedVoucherRecharge` datetime DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `Notes` text,
  `Currency` varchar(6) NOT NULL,
  PRIMARY KEY (`Username`),
  UNIQUE KEY `ID` (`ID`),
  KEY `FK_user_1` (`CountryID`),
  KEY `FK_user_3` (`ReferredBy`),
  KEY `FK_user_2` (`AffiliateID`),
  KEY `Index_5` (`MobilePhone`),
  KEY `FK_user_4` (`Currency`),
  KEY `FK_user_5` (`DisplayPicture`),
  KEY `idx_CountryID_DisplayPic` (`CountryID`,`DisplayPicture`),
  KEY `FK_user_6` (`BonusProgramID`),
  KEY `idx_MerchantCreated` (`MerchantCreated`),
  KEY `idx_LastLoginDate` (`LastLoginDate`),
  CONSTRAINT `FK_user_1` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_user_2` FOREIGN KEY (`AffiliateID`) REFERENCES `affiliate` (`ID`),
  CONSTRAINT `FK_user_3` FOREIGN KEY (`ReferredBy`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_user_4` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_user_5` FOREIGN KEY (`DisplayPicture`) REFERENCES `file` (`ID`),
  CONSTRAINT `FK_user_6` FOREIGN KEY (`BonusProgramID`) REFERENCES `bonusprogram` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=272180913 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `useralias`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `useralias` (
  `username` varchar(128) NOT NULL,
  `alias` varchar(128) NOT NULL,
  `DateUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `alias` (`alias`),
  CONSTRAINT `FK_useralias_username` FOREIGN KEY (`username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userblogpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userblogpost` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AuthorUserID` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Title` varchar(100) NOT NULL,
  `Body` varchar(5000) NOT NULL,
  `NumComments` int(11) DEFAULT '0',
  `NumLikes` int(11) DEFAULT '0',
  `NumDislikes` int(11) DEFAULT '0',
  `Privacy` int(11) DEFAULT '1',
  `Status` int(11) DEFAULT '1',
  `AllowComments` int(11) DEFAULT '1',
  `LastUpdated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `FileID` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_authoruserid` (`AuthorUserID`)
) ENGINE=MyISAM AUTO_INCREMENT=146306 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userblogpostcomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userblogpostcomment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserBlogPostID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Comment` varchar(4000) DEFAULT NULL,
  `Status` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_userblogpostid` (`UserBlogPostID`),
  KEY `FK_userid` (`UserID`)
) ENGINE=MyISAM AUTO_INCREMENT=47161 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userblogpostlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userblogpostlike` (
  `UserBlogPostID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Type` tinyint(4) NOT NULL,
  PRIMARY KEY (`UserBlogPostID`,`UserID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usercategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usercategory` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `parent` int(11) DEFAULT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '1: user, 2: entity',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `useremailaddress`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `useremailaddress` (
  `userid` int(10) unsigned NOT NULL,
  `emailaddress` varchar(255) NOT NULL DEFAULT '',
  `verified` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '1' COMMENT '1: primary',
  `dateverified` datetime DEFAULT NULL,
  PRIMARY KEY (`userid`,`emailaddress`),
  KEY `idx_emailaddress` (`emailaddress`),
  KEY `type` (`type`,`verified`),
  CONSTRAINT `fk10_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userid`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userid` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  CONSTRAINT `FK_userid_username` FOREIGN KEY (`username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=195715548 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userlabel`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userlabel` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userid` int(10) unsigned NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_userlabel_userid_type` (`userid`,`type`),
  CONSTRAINT `FK_userlabel_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userlike` (
  `likeduserid` int(10) unsigned NOT NULL,
  `likinguserid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`likeduserid`,`likinguserid`),
  KEY `FK_userlike_likinguserid` (`likinguserid`),
  CONSTRAINT `FK_userlike_likeduserid` FOREIGN KEY (`likeduserid`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_userlike_likinguserid` FOREIGN KEY (`likinguserid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userlikesummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userlikesummary` (
  `userid` int(10) unsigned NOT NULL,
  `numlikes` int(11) NOT NULL DEFAULT '0',
  `numdislikes` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userid`),
  CONSTRAINT `FK_userlikesummary_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userpost` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `Body` text,
  `DateCreated` datetime NOT NULL,
  `ParentUserPostID` int(11) DEFAULT NULL,
  `NumReplies` int(11) NOT NULL DEFAULT '0',
  `LastReplyDate` datetime NOT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Username` (`Username`),
  KEY `ParentUserPostID` (`ParentUserPostID`),
  KEY `ID` (`ID`,`ParentUserPostID`,`LastReplyDate`),
  CONSTRAINT `userpost_ibfk_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `userpost_ibfk_2` FOREIGN KEY (`ParentUserPostID`) REFERENCES `userpost` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userprofile`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userprofile` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `FirstName` varchar(128) DEFAULT NULL,
  `LastName` varchar(128) DEFAULT NULL,
  `HomeTown` varchar(128) DEFAULT NULL,
  `City` varchar(128) DEFAULT NULL,
  `State` varchar(128) DEFAULT NULL,
  `DateOfBirth` date DEFAULT NULL,
  `Gender` varchar(1) DEFAULT NULL,
  `Jobs` text,
  `Schools` text,
  `Hobbies` text,
  `Likes` text,
  `Dislikes` text,
  `AboutMe` text,
  `RelationshipStatus` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_UserProfile_1` (`Username`),
  KEY `idx_UserProfile1` (`HomeTown`),
  KEY `idx_UserProfile2` (`DateOfBirth`),
  CONSTRAINT `FK_UserProfile_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=68506552 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userprofilekeyword`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userprofilekeyword` (
  `UserProfileID` int(11) NOT NULL DEFAULT '0',
  `KeywordID` int(11) NOT NULL DEFAULT '0',
  `Type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`UserProfileID`,`KeywordID`,`Type`),
  KEY `FK_UserProfileKeyword_2` (`KeywordID`),
  CONSTRAINT `FK_UserProfileKeyword_1` FOREIGN KEY (`UserProfileID`) REFERENCES `userprofile` (`ID`),
  CONSTRAINT `FK_UserProfileKeyword_2` FOREIGN KEY (`KeywordID`) REFERENCES `keyword` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userreferral`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userreferral` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `ReferrerName` varchar(128) DEFAULT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `MobilePhone` varchar(128) NOT NULL DEFAULT '',
  `Amount` double(10,2) NOT NULL DEFAULT '0.00',
  `Paid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Index_3` (`MobilePhone`,`Username`),
  KEY `FK_Referral_1` (`Username`),
  CONSTRAINT `FK_Referral_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=74426997 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userreferralsource`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userreferralsource` (
  `UserID` int(10) unsigned NOT NULL,
  `ReferralSourceID` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`UserID`,`ReferralSourceID`),
  KEY `ReferralSourceID` (`ReferralSourceID`),
  CONSTRAINT `userreferralsource_ibfk_1` FOREIGN KEY (`ReferralSourceID`) REFERENCES `referralsource` (`ID`),
  CONSTRAINT `userreferralsource_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usersetting`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usersetting` (
  `Username` varchar(128) NOT NULL,
  `Type` int(11) NOT NULL,
  `Value` int(11) NOT NULL,
  PRIMARY KEY (`Username`,`Type`),
  CONSTRAINT `FK_UserSetting_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usertousercategory`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usertousercategory` (
  `userid` int(11) NOT NULL,
  `usercategoryid` int(11) NOT NULL,
  KEY `IDX_userCategoryID` (`usercategoryid`),
  KEY `idx_userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userverified`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userverified` (
  `userid` int(10) unsigned NOT NULL,
  `verified` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '1' COMMENT '1: user; 2: entity',
  `dateModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text NOT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userwallpost`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userwallpost` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userid` int(10) unsigned NOT NULL,
  `authoruserid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `body` varchar(4000) NOT NULL,
  `numcomments` int(11) NOT NULL DEFAULT '0',
  `numlikes` int(11) NOT NULL DEFAULT '0',
  `numdislikes` int(11) NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_userwallpost_userid` (`userid`),
  KEY `FK_userwallpost_authoruserid` (`authoruserid`),
  CONSTRAINT `FK_userwallpost_authoruserid` FOREIGN KEY (`authoruserid`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_userwallpost_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userwallpostcomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userwallpostcomment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userwallpostid` int(10) unsigned NOT NULL,
  `userid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `comment` varchar(4000) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_userwallpostcomment_userwallpostid` (`userwallpostid`),
  KEY `FK_userwallpostcomment_userid` (`userid`),
  CONSTRAINT `FK_userwallpostcomment_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_userwallpostcomment_userwallpostid` FOREIGN KEY (`userwallpostid`) REFERENCES `userwallpost` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userwallpostlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userwallpostlike` (
  `userwallpostid` int(10) unsigned NOT NULL,
  `userid` int(10) unsigned NOT NULL,
  `datecreated` datetime NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`userwallpostid`,`userid`),
  KEY `FK_userwallpostlike_userid` (`userid`),
  CONSTRAINT `FK_userwallpostlike_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_userwallpostlike_userwallpostid` FOREIGN KEY (`userwallpostid`) REFERENCES `userwallpost` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ussdpartner`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ussdpartner` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `userid` int(10) unsigned NOT NULL,
  `ipaddress` tinytext NOT NULL COMMENT 'ip address that wil be delimited by '',''',
  `appkey` varchar(64) NOT NULL DEFAULT '',
  `secretkey` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_name` (`name`),
  UNIQUE KEY `idx_appkey` (`appkey`),
  KEY `FK_userid_id` (`userid`),
  CONSTRAINT `FK_userid_id` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ussdpartnermobiletransfer`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ussdpartnermobiletransfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `originalOwner` varchar(128) NOT NULL,
  `newOwner` varchar(128) NOT NULL,
  `originalNumber` varchar(128) NOT NULL,
  `dateCreated` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `originalOwner` (`originalOwner`),
  CONSTRAINT `fk_originalOwner` FOREIGN KEY (`originalOwner`) REFERENCES `user` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ussdpartneruser`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ussdpartneruser` (
  `userid` int(10) unsigned NOT NULL,
  `ussdpartnerid` int(11) unsigned NOT NULL,
  `datecreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `unq_ussd_userid` (`userid`),
  KEY `FK_ussdpartner_id` (`ussdpartnerid`),
  CONSTRAINT `FK_ussdpartner_id` FOREIGN KEY (`ussdpartnerid`) REFERENCES `ussdpartner` (`id`),
  CONSTRAINT `unq_ussd_userid` FOREIGN KEY (`userid`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vendorvoucher`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vendorvoucher` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `paymentid` int(11) unsigned NOT NULL,
  `vouchercode` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_vendorvoucher_1` (`paymentid`),
  KEY `idx_vendorvoucher_1` (`vouchercode`) USING HASH,
  CONSTRAINT `fk_vendorvoucher_1` FOREIGN KEY (`paymentid`) REFERENCES `payments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualgift`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualgift` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) DEFAULT NULL,
  `HotKey` varchar(128) DEFAULT NULL,
  `Price` double NOT NULL,
  `Currency` varchar(6) NOT NULL,
  `NumAvailable` int(11) DEFAULT NULL,
  `NumSold` int(11) NOT NULL DEFAULT '0',
  `SortOrder` int(11) DEFAULT NULL,
  `GroupID` int(11) DEFAULT NULL,
  `GroupVIPOnly` tinyint(1) DEFAULT NULL,
  `Location12x12GIF` varchar(128) DEFAULT NULL,
  `Location12x12PNG` varchar(128) DEFAULT NULL,
  `Location14x14GIF` varchar(128) DEFAULT NULL,
  `Location14x14PNG` varchar(128) DEFAULT NULL,
  `Location16x16GIF` varchar(128) DEFAULT NULL,
  `Location16x16PNG` varchar(128) DEFAULT NULL,
  `Location64x64PNG` varchar(128) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  `GiftAllMessage` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `GroupID` (`GroupID`),
  KEY `Name` (`Name`),
  CONSTRAINT `virtualgift_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3887 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualgiftreceived`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualgiftreceived` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `PurchaseLocation` int(11) DEFAULT NULL,
  `VirtualGiftID` int(11) NOT NULL,
  `Sender` varchar(128) NOT NULL,
  `Private` int(11) NOT NULL DEFAULT '0',
  `Removed` int(11) NOT NULL DEFAULT '0',
  `Message` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_VirtualGiftID` (`VirtualGiftID`),
  KEY `FK_Username` (`Username`),
  KEY `FK_Sender` (`Sender`),
  KEY `DateCreated` (`DateCreated`),
  CONSTRAINT `VirtualGiftID` FOREIGN KEY (`VirtualGiftID`) REFERENCES `virtualgift` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=516681085 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualgiftreceivedcomment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualgiftreceivedcomment` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `VirtualGiftReceivedID` int(11) NOT NULL,
  `VirtualGiftReceivedUserID` int(10) unsigned NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Comment` varchar(4000) NOT NULL,
  `NumLikes` int(11) NOT NULL DEFAULT '0',
  `NumDislikes` int(11) NOT NULL DEFAULT '0',
  `Status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_virtualgiftreceivedcomment_virtualgiftreceivedid` (`VirtualGiftReceivedID`),
  KEY `FK_virtualgiftreceivedcomment_virtualgiftreceiveduserid` (`VirtualGiftReceivedUserID`),
  KEY `FK_virtualgiftreceivedcomment_userid` (`UserID`),
  CONSTRAINT `FK_virtualgiftreceivedcomment_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_virtualgiftreceivedcomment_virtualgiftreceivedid` FOREIGN KEY (`VirtualGiftReceivedID`) REFERENCES `virtualgiftreceived` (`ID`),
  CONSTRAINT `FK_virtualgiftreceivedcomment_virtualgiftreceiveduserid` FOREIGN KEY (`VirtualGiftReceivedUserID`) REFERENCES `userid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualgiftreceivedcommentlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualgiftreceivedcommentlike` (
  `VirtualGiftReceivedCommentID` int(10) unsigned NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`VirtualGiftReceivedCommentID`,`UserID`),
  KEY `FK_virtualgiftreceivedcommentlike_userid` (`UserID`),
  CONSTRAINT `FK_virtualgiftreceivedcommentlike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_virtualgiftreceivedcommentlike_virtualgiftreceivedcommentid` FOREIGN KEY (`VirtualGiftReceivedCommentID`) REFERENCES `virtualgiftreceivedcomment` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualgiftreceivedlike`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualgiftreceivedlike` (
  `VirtualGiftReceivedID` int(11) NOT NULL,
  `UserID` int(10) unsigned NOT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` tinyint(2) NOT NULL,
  PRIMARY KEY (`VirtualGiftReceivedID`,`UserID`),
  KEY `FK_virtualgiftreceivedlike_userid` (`UserID`),
  CONSTRAINT `FK_virtualgiftreceivedlike_userid` FOREIGN KEY (`UserID`) REFERENCES `userid` (`id`),
  CONSTRAINT `FK_virtualgiftreceivedlike_virtualgiftreceivedid` FOREIGN KEY (`VirtualGiftReceivedID`) REFERENCES `virtualgiftreceived` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `virtualgiftreceivedlikesummary`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `virtualgiftreceivedlikesummary` (
  `VirtualGiftReceivedID` int(11) NOT NULL,
  `NumLikes` int(11) NOT NULL DEFAULT '0',
  `NumDislikes` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`VirtualGiftReceivedID`),
  CONSTRAINT `FK_virtualgiftreceivedlikesummary_virtualgiftreceivedid` FOREIGN KEY (`VirtualGiftReceivedID`) REFERENCES `virtualgiftreceived` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voicegateway`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voicegateway` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `Server` varchar(128) NOT NULL DEFAULT '',
  `Port` int(11) NOT NULL DEFAULT '0',
  `Username` varchar(128) NOT NULL DEFAULT '',
  `Password` varchar(128) NOT NULL DEFAULT '',
  `CallbackContext` varchar(128) NOT NULL DEFAULT '',
  `CallbackExtension` varchar(128) NOT NULL DEFAULT '',
  `ConnectionTimeout` int(11) NOT NULL DEFAULT '0',
  `TimeoutWarning` int(11) NOT NULL DEFAULT '0',
  `TimeoutWarningRepeat` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voiceprovider`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voiceprovider` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(128) NOT NULL DEFAULT '',
  `DialCommand` varchar(128) NOT NULL DEFAULT '',
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voiceroute`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voiceroute` (
  `IDDCode` int(11) NOT NULL DEFAULT '0',
  `AreaCode` varchar(128) NOT NULL,
  `GatewayID` int(11) NOT NULL DEFAULT '0',
  `ProviderID` int(11) NOT NULL DEFAULT '0',
  `Priority` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IDDCode`,`AreaCode`,`GatewayID`,`ProviderID`,`Priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voiceroutewhitelist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voiceroutewhitelist` (
  `sourceidd` int(11) NOT NULL,
  `destinationidd` int(11) NOT NULL,
  UNIQUE KEY `sourceidd` (`sourceidd`,`destinationidd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voicewholesalerate`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voicewholesalerate` (
  `IDDCode` int(11) NOT NULL,
  `AreaCode` varchar(128) NOT NULL,
  `ProviderID` int(11) NOT NULL,
  `Rate` double(10,6) NOT NULL,
  `Currency` varchar(6) NOT NULL DEFAULT '',
  PRIMARY KEY (`IDDCode`,`AreaCode`,`ProviderID`),
  KEY `FK_VoiceWholesaleRate_1` (`ProviderID`),
  KEY `FK_VoiceWholesaleRate_2` (`Currency`),
  CONSTRAINT `FK_VoiceWholesaleRate_1` FOREIGN KEY (`ProviderID`) REFERENCES `voiceprovider` (`ID`),
  CONSTRAINT `FK_VoiceWholesaleRate_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voucher`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voucher` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `VoucherBatchID` int(10) unsigned NOT NULL DEFAULT '0',
  `Number` varchar(10) NOT NULL DEFAULT '0',
  `LastUpdated` datetime DEFAULT NULL,
  `Status` int(1) unsigned NOT NULL DEFAULT '1',
  `Notes` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Number` (`Number`),
  KEY `FK_Voucher_1` (`VoucherBatchID`),
  CONSTRAINT `FK_Voucher_1` FOREIGN KEY (`VoucherBatchID`) REFERENCES `voucherbatch` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=405551 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `voucherbatch`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `voucherbatch` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ExpiryDate` datetime DEFAULT NULL,
  `Currency` varchar(6) NOT NULL,
  `Amount` double(10,2) NOT NULL DEFAULT '0.00',
  `NumVoucher` int(10) unsigned NOT NULL DEFAULT '0',
  `Notes` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_VoucherBatch_1` (`Username`),
  KEY `FK_voucherbatch_2` (`Currency`),
  CONSTRAINT `FK_VoucherBatch_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_voucherbatch_2` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=149138 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_resources`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_resources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `path` varchar(255) NOT NULL DEFAULT '',
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `bcontent` longblob NOT NULL,
  `tstamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `status` (`status`),
  KEY `tstamp` (`tstamp`)
) ENGINE=InnoDB AUTO_INCREMENT=79670 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_resources_copy1`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_resources_copy1` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `path` varchar(255) NOT NULL DEFAULT '',
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `bcontent` longblob NOT NULL,
  `tstamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `status` (`status`),
  KEY `tstamp` (`tstamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `web_resources_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `web_resources_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` varchar(10) NOT NULL DEFAULT 'release',
  `status` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `datetime` (`datetime`),
  KEY `type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `webmoneypayment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `webmoneypayment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL,
  `DateCreated` datetime NOT NULL,
  `CountryID` int(11) NOT NULL,
  `PaymentProductID` int(11) NOT NULL,
  `ReturnMac` varchar(128) DEFAULT NULL,
  `StatusID` int(11) DEFAULT NULL,
  `AdditionalReference` varchar(128) DEFAULT NULL,
  `Ref` varchar(128) DEFAULT NULL,
  `FormMethod` varchar(128) DEFAULT NULL,
  `ExternalReference` varchar(128) DEFAULT NULL,
  `EffortID` int(11) DEFAULT NULL,
  `MAC` varchar(128) DEFAULT NULL,
  `PaymentReference` varchar(128) DEFAULT NULL,
  `FormAction` varchar(128) DEFAULT NULL,
  `AttemptID` int(11) DEFAULT NULL,
  `MerchantID` int(11) DEFAULT NULL,
  `StatusDate` varchar(128) DEFAULT NULL,
  `OrderID` int(11) DEFAULT NULL,
  `IntendedAmount` double(12,2) NOT NULL,
  `IntendedCurrency` varchar(4) NOT NULL,
  `Amount` double(12,2) DEFAULT NULL,
  `Currency` varchar(4) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_WebMoneyPayment_1` (`Username`),
  KEY `FK_WebMoneyPayment_2` (`CountryID`),
  KEY `FK_WebMoneyPayment_3` (`IntendedCurrency`),
  KEY `FK_WebMoneyPayment_4` (`Currency`),
  CONSTRAINT `FK_WebMoneyPayment_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_WebMoneyPayment_2` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_WebMoneyPayment_3` FOREIGN KEY (`IntendedCurrency`) REFERENCES `currency` (`Code`),
  CONSTRAINT `FK_WebMoneyPayment_4` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `westernunionintent`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `westernunionintent` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(128) NOT NULL DEFAULT '',
  `DateCreated` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `CountryID` int(11) NOT NULL DEFAULT '0',
  `PaymentProductID` int(11) NOT NULL DEFAULT '0',
  `Surname` varchar(128) DEFAULT NULL,
  `Amount` double(12,2) NOT NULL DEFAULT '0.00',
  `Currency` varchar(4) NOT NULL DEFAULT '',
  `ReturnMAC` varchar(128) DEFAULT NULL,
  `StatusID` int(11) DEFAULT NULL,
  `AdditionalReference` varchar(128) DEFAULT NULL,
  `Ref` varchar(128) DEFAULT NULL,
  `FormMethod` varchar(128) DEFAULT NULL,
  `ExternalReference` varchar(128) DEFAULT NULL,
  `EffortID` int(11) DEFAULT NULL,
  `MAC` varchar(128) DEFAULT NULL,
  `PaymentReference` varchar(128) DEFAULT NULL,
  `FormAction` varchar(256) DEFAULT NULL,
  `AttemptID` int(11) DEFAULT NULL,
  `MerchantID` int(11) DEFAULT NULL,
  `StatusDate` varchar(128) DEFAULT NULL,
  `OrderID` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_WesternUnionIntent_1` (`Username`),
  KEY `FK_WesternUnionIntent_2` (`CountryID`),
  KEY `FK_WesternUnionIntent_3` (`Currency`),
  CONSTRAINT `FK_WesternUnionIntent_1` FOREIGN KEY (`Username`) REFERENCES `user` (`Username`),
  CONSTRAINT `FK_WesternUnionIntent_2` FOREIGN KEY (`CountryID`) REFERENCES `country` (`ID`),
  CONSTRAINT `FK_WesternUnionIntent_3` FOREIGN KEY (`Currency`) REFERENCES `currency` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `westernunionreceived`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `westernunionreceived` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `WesternUnionIntentID` int(11) DEFAULT NULL,
  `DateCreated` datetime NOT NULL,
  `Type` int(11) NOT NULL DEFAULT '0',
  `FileName` varchar(128) NOT NULL,
  `Row` int(11) NOT NULL,
  `PaymentReference` varchar(128) DEFAULT NULL,
  `InvoiceNumber` varchar(128) DEFAULT NULL,
  `CustomerID` varchar(128) DEFAULT NULL,
  `AdditionalReference` varchar(128) DEFAULT NULL,
  `EffortNumber` int(11) DEFAULT NULL,
  `InvoiceCurrencyDeliv` varchar(128) DEFAULT NULL,
  `InvoiceAmountDeliv` double(12,2) DEFAULT NULL,
  `InvoiceCurrencyLocal` varchar(128) DEFAULT NULL,
  `InvoiceAmountLocal` double(12,2) DEFAULT NULL,
  `PaymentMethod` varchar(128) DEFAULT NULL,
  `CreditCardCompany` varchar(128) DEFAULT NULL,
  `UncleanIndicator` varchar(128) DEFAULT NULL,
  `PaymentCurrency` varchar(128) DEFAULT NULL,
  `PaymentAmount` double(12,2) DEFAULT NULL,
  `CurrencyDue` varchar(128) DEFAULT NULL,
  `AmountDue` double(12,2) DEFAULT NULL,
  `DateDue` int(11) DEFAULT NULL,
  `ReversalCurrency` varchar(128) DEFAULT NULL,
  `ReversalAmount` double(12,2) DEFAULT NULL,
  `ReversalReasonID` varchar(128) DEFAULT NULL,
  `ReversalReasonDescription` varchar(128) DEFAULT NULL,
  `Datecollect` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_WesternUnionReceived_1` (`WesternUnionIntentID`),
  KEY `IDX_WesternUnionReceived_1` (`PaymentReference`),
  KEY `IDX_WesternUnionReceived_2` (`FileName`),
  CONSTRAINT `FK_WesternUnionReceived_1` FOREIGN KEY (`WesternUnionIntentID`) REFERENCES `westernunionintent` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wurflobjectcache`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wurflobjectcache` (
  `key` varchar(255) NOT NULL,
  `value` mediumblob NOT NULL,
  `ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-18  9:48:20
