/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3306
 Source Schema         : stu_org_db

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 30/01/2024 08:15:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `student_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学生学号',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实姓名',
  `username` varchar(256) DEFAULT NULL COMMENT '用户名',
  `password` varchar(512) DEFAULT NULL COMMENT '密码',
  `qq` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'qq号',
  `phone` varchar(128) DEFAULT NULL COMMENT '手机号',
  `major` varchar(256) DEFAULT NULL COMMENT '专业',
  `class_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '班级',
  `wills` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '志愿',
  `role` tinyint(1) DEFAULT NULL COMMENT '角色 0: 学生 1: 管理员',
  `is_dispensing` bit(1) DEFAULT NULL COMMENT '是否同意调剂',
  `deletion_time` tinyint(1) DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_unique_stu_id` (`student_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` (`id`, `student_id`, `name`, `username`, `password`, `qq`, `phone`, `major`, `class_name`, `wills`, `role`, `is_dispensing`, `deletion_time`, `create_time`, `update_time`, `del_flag`) VALUES (1, NULL, NULL, 'admin', 'f51703256a38e6bab3d9410a070c32ea', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2024-01-29 19:59:44', NULL, 0);
INSERT INTO `t_user` (`id`, `student_id`, `name`, `username`, `password`, `qq`, `phone`, `major`, `class_name`, `wills`, `role`, `is_dispensing`, `deletion_time`, `create_time`, `update_time`, `del_flag`) VALUES (2, '20222022', '咩酱修改测试', 'user01', 'f51703256a38e6bab3d9410a070c32ea', '1120222022', '18100000006', '计算机科学与技术', '一班', '[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]', 0, b'1', NULL, '2024-01-29 18:44:53', '2024-01-29 21:32:40', 0);
INSERT INTO `t_user` (`id`, `student_id`, `name`, `username`, `password`, `qq`, `phone`, `major`, `class_name`, `wills`, `role`, `is_dispensing`, `deletion_time`, `create_time`, `update_time`, `del_flag`) VALUES (3, '20222023', '咩酱酱', 'user03', 'f51703256a38e6bab3d9410a070c32ea', '1120222032', '18100010016', '物联网', '一班', '[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]', 0, b'0', NULL, '2024-01-29 18:45:15', '2024-01-30 07:20:03', 0);
INSERT INTO `t_user` (`id`, `student_id`, `name`, `username`, `password`, `qq`, `phone`, `major`, `class_name`, `wills`, `role`, `is_dispensing`, `deletion_time`, `create_time`, `update_time`, `del_flag`) VALUES (4, '20222025', '咩酱酱', 'user02', 'f51703256a38e6bab3d9410a070c32ea', '1120222032', '18100010016', '大数据', '一班', '[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]', 0, b'1', NULL, '2024-01-29 18:45:18', '2024-01-30 07:22:17', 0);
INSERT INTO `t_user` (`id`, `student_id`, `name`, `username`, `password`, `qq`, `phone`, `major`, `class_name`, `wills`, `role`, `is_dispensing`, `deletion_time`, `create_time`, `update_time`, `del_flag`) VALUES (5, '20222024', '咩酱咩', 'user04', 'f51703256a38e6bab3d9410a070c32ea', '1120222032', '18100010016', '中加', '一班', '[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]', 0, b'1', NULL, '2024-01-29 19:57:57', '2024-01-30 07:21:23', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
