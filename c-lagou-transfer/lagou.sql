/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50648
Source Host           : localhost:3306
Source Database       : lagou

Target Server Type    : MYSQL
Target Server Version : 50648
File Encoding         : 65001

Date: 2021-02-16 15:37:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(32) CHARACTER SET utf8mb4 NOT NULL COMMENT '用户姓名',
  `cardNo` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT '卡号',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES ('1', '韩梅梅', '6029621011001', '12100.00');
INSERT INTO `account` VALUES ('2', '李大雷', '6029621011000', '7900.00');

