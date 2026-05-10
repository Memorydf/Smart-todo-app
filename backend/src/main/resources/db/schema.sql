CREATE DATABASE IF NOT EXISTS todo_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE todo_db;

CREATE TABLE IF NOT EXISTS todo (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  title         VARCHAR(255) NOT NULL,
  content       TEXT,
  priority      VARCHAR(20)  NOT NULL DEFAULT 'medium',
  status        TINYINT      NOT NULL DEFAULT 0 COMMENT '0 未完成 1 已完成',
  deadline      DATE         NULL,
  category      VARCHAR(32)  NOT NULL DEFAULT 'other',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_todo_status_deadline (status, deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ai_chat (
  id          BIGINT   NOT NULL AUTO_INCREMENT,
  question    TEXT     NOT NULL,
  answer      TEXT,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
