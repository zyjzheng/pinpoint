

CREATE TABLE `user_group` (
    `number` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户组id',
	`id` VARCHAR(30) NOT NULL COMMENT '用户组标识',
	PRIMARY KEY (`number`)
) COMMENT '用户组';
ALTER TABLE user_group ADD UNIQUE KEY id_idx (id);

CREATE TABLE `user_group_member` (
  `number` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户组与用户映射关系id', 
  `user_group_id` VARCHAR(30) NOT NULL COMMENT '用户组标识',
  `member_id` VARCHAR(30) NOT NULL COMMENT '用户标识',
  PRIMARY KEY (`number`)
) COMMENT '用户与用户组映射关系表';
ALTER TABLE user_group_member ADD UNIQUE KEY user_group_id_member_id_idx (`user_group_id`,`member_id`);

CREATE TABLE `puser` (
  `number` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键', 
  `user_id` VARCHAR(30) NOT NULL COMMENT '用户标识',
  `name` VARCHAR(150) NOT NULL COMMENT '用户名',
  `department` VARCHAR(150) NOT NULL COMMENT '部门',
  `phonenumber` VARCHAR(100) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT 'email',
  PRIMARY KEY (`number`)
) COMMENT 'pinpoint 用户表';
ALTER TABLE puser ADD UNIQUE KEY user_id_idx (`user_id`);

CREATE TABLE `alarm_rule` (
  `rule_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `application_id` VARCHAR(30) NOT NULL COMMENT '应用标识',
  `service_type` VARCHAR(30) NOT NULL COMMENT '应用类型',
  `checker_name` VARCHAR(50) NOT NULL COMMENT '检查器名称',
  `threshold` INT(10) DEFAULT NULL COMMENT '检测阈值',
  `user_group_id` VARCHAR(30) NOT NULL COMMENT '告警应用用户组',
  `sms_send` CHAR(1) DEFAULT NULL COMMENT '是否发送短信告警',
  `email_send` CHAR(1) DEFAULT NULL COMMENT '是否发送邮件告警',
  `notes` VARCHAR(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rule_id`)
) COMMENT '告警规则';
ALTER TABLE alarm_rule ADD UNIQUE KEY application_id_checker_name_user_group_id_idx (application_id, user_group_id, checker_name);

CREATE TABLE `alarm_history` (
  `history_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `application_id` VARCHAR(30) NOT NULL COMMENT '应用标识',
  `checker_name` VARCHAR(50) NOT NULL COMMENT '告警检查器名称',
  `detected` CHAR(1) DEFAULT NULL COMMENT '是否检测到',
  `sequence_count` INT(10) COMMENT '告警系列号',
  `timing_count` INT(10) COMMENT '告警数量',
  PRIMARY KEY (`history_id`)
) COMMENT '告警历史记录表';
ALTER TABLE alarm_history ADD UNIQUE KEY application_id_checker_name_idx (application_id, checker_name);
       
