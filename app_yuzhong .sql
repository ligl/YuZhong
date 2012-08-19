-- phpMyAdmin SQL Dump
-- version 3.3.8.1
-- http://www.phpmyadmin.net
--
-- 主机: w.rdc.sae.sina.com.cn:3307
-- 生成日期: 2012 年 08 月 19 日 17:14
-- 服务器版本: 5.1.47
-- PHP 版本: 5.2.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `app_yuzhong`
--

-- --------------------------------------------------------

--
-- 表的结构 `activity`
--

CREATE TABLE IF NOT EXISTS `activity` (
  `aid` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `signupbegintime` bigint(20) NOT NULL,
  `signupendtime` bigint(20) NOT NULL,
  `atime` bigint(20) NOT NULL,
  `addr` varchar(200) NOT NULL,
  `desc` varchar(500) DEFAULT NULL,
  `photo` varchar(100) DEFAULT NULL,
  `uid` int(11) NOT NULL,
  `regtime` bigint(20) NOT NULL,
  PRIMARY KEY (`aid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='活动表' AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- 表的结构 `bulletin`
--

CREATE TABLE IF NOT EXISTS `bulletin` (
  `bid` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `content` text NOT NULL,
  `time` bigint(13) NOT NULL,
  `type` tinyint(2) NOT NULL DEFAULT '0',
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`bid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='存放系统的通知,新闻,活动等数据' AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- 表的结构 `fund`
--

CREATE TABLE IF NOT EXISTS `fund` (
  `fid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) NOT NULL COMMENT '项目名称',
  `money` float NOT NULL COMMENT '花费',
  `type` tinyint(4) NOT NULL COMMENT '收支（1：收，2支）',
  `memo` text COMMENT '备注',
  `time` bigint(20) NOT NULL COMMENT '收支日期',
  `recordtime` bigint(20) NOT NULL COMMENT '记录日期',
  `recorder` int(11) NOT NULL COMMENT '记录者',
  PRIMARY KEY (`fid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='收支表' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 表的结构 `fund_item`
--

CREATE TABLE IF NOT EXISTS `fund_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '项目名称',
  `money` float NOT NULL COMMENT '花费',
  `fid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='基金支出项目详细' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 表的结构 `geo`
--

CREATE TABLE IF NOT EXISTS `geo` (
  `gid` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `lat` int(11) NOT NULL,
  `lng` int(11) NOT NULL,
  `time` int(13) NOT NULL,
  PRIMARY KEY (`gid`),
  KEY `uid` (`uid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 表的结构 `permission`
--

CREATE TABLE IF NOT EXISTS `permission` (
  `pid` smallint(4) NOT NULL AUTO_INCREMENT,
  `caption` varchar(50) NOT NULL,
  `desc` text,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `caption` (`caption`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1502 ;

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `uid` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL,
  `mobile` varchar(11) NOT NULL,
  `password` varchar(32) NOT NULL,
  `gender` tinyint(1) NOT NULL DEFAULT '-1' COMMENT '0：男，1：女，-1：不晓得',
  `birthday` int(13) DEFAULT NULL,
  `avatar` varchar(100) DEFAULT NULL,
  `regtime` int(13) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '会员状态(0:审核中,1:正常,-1:被禁用)',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `mobile` (`mobile`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- 表的结构 `user_activity`
--

CREATE TABLE IF NOT EXISTS `user_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `content` text,
  `want` tinyint(4) NOT NULL COMMENT '有事不去了(-1),我去(1)',
  `done` tinyint(1) NOT NULL DEFAULT '0' COMMENT '最近是否参与了这个活动',
  `regtime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='记录用户想要参与或有事不能参与的活动' AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- 表的结构 `user_permission`
--

CREATE TABLE IF NOT EXISTS `user_permission` (
  `uid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`pid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
