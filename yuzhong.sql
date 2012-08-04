-- phpMyAdmin SQL Dump
-- version 3.3.8.1
-- http://www.phpmyadmin.net
--
-- 主机: w.rdc.sae.sina.com.cn:3307
-- 生成日期: 2012 年 08 月 04 日 12:19
-- 服务器版本: 5.1.47
-- PHP 版本: 5.2.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 数据库: `app_yuzhong`
--

-- --------------------------------------------------------

--
-- 表的结构 `bulletin`
--

CREATE TABLE IF NOT EXISTS `bulletin` (
  `bid` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `content` text NOT NULL,
  `time` int(13) NOT NULL,
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '普通文本(0),活动通知(1)',
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`bid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='存放系统的通知,新闻,活动等数据' AUTO_INCREMENT=2 ;

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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- 表的结构 `user_activity`
--

CREATE TABLE IF NOT EXISTS `user_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bid` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `content` text,
  `want` tinyint(4) NOT NULL COMMENT '有事不去了(-1),我去(1)',
  `done` tinyint(1) NOT NULL DEFAULT '0' COMMENT '最近是否参与了这个活动',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='记录用户想要参与或有事不能参与的活动' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 表的结构 `user_permission`
--

CREATE TABLE IF NOT EXISTS `user_permission` (
  `uid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`pid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

