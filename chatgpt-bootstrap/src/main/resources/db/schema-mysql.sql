-- 创建 database
CREATE DATABASE IF NOT EXISTS chat DEFAULT CHARACTER SET utf8;

-- 进入 chat 库
USE chat;

-- 聊天室表
CREATE TABLE IF NOT EXISTS chat_room (
    id BIGINT PRIMARY KEY COMMENT '主键',
    ip VARCHAR(255) NULL COMMENT 'ip',
    conversation_id VARCHAR(64) UNIQUE NULL COMMENT '对话 id，唯一',
    first_chat_message_id BIGINT UNIQUE NOT NULL  COMMENT '第一条消息主键',
    first_message_id VARCHAR(64) UNIQUE NOT NULL COMMENT '第一条消息',
    title VARCHAR(255) NOT NULL COMMENT '对话标题，从第一条消息截取',
    api_type VARCHAR(20) NOT NULL COMMENT 'API 类型',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天室表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY COMMENT '主键',
    message_id VARCHAR(64) UNIQUE NOT NULL COMMENT '消息 id',
    parent_message_id VARCHAR(64) COMMENT '父级消息 id',
    parent_answer_message_id VARCHAR(64) COMMENT '父级回答消息 id',
    parent_question_message_id VARCHAR(64) COMMENT '父级问题消息 id',
    context_count BIGINT NOT NULL COMMENT '上下文数量',
    question_context_count BIGINT NOT NULL COMMENT '问题上下文数量',
    model_name VARCHAR(50) NOT NULL COMMENT '模型名称',
    message_type INTEGER NOT NULL COMMENT '消息类型枚举',
    chat_room_id BIGINT NOT NULL COMMENT '聊天室 id',
    conversation_id VARCHAR(255) NULL COMMENT '对话 id',
    api_type VARCHAR(20) NOT NULL COMMENT 'API 类型',
    api_key VARCHAR(255) NULL COMMENT 'ApiKey',
    content VARCHAR(5000) NOT NULL COMMENT '消息内容',
    original_data TEXT COMMENT '消息的原始请求或响应数据',
    response_error_data TEXT COMMENT '错误的响应数据',
    prompt_tokens INTEGER COMMENT '输入消息的 tokens',
    completion_tokens INTEGER COMMENT '输出消息的 tokens',
    total_tokens INTEGER COMMENT '累计 Tokens',
    ip VARCHAR(255) NULL COMMENT 'ip',
    status INTEGER NOT NULL COMMENT '聊天记录状态',
    is_hide TINYINT NOT NULL DEFAULT 0 COMMENT '是否隐藏 0 否 1 是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    word VARCHAR(255) NOT NULL COMMENT '敏感词内容',
    status INTEGER NOT NULL COMMENT '状态 1 启用 2 停用',
    is_deleted INTEGER DEFAULT 0 COMMENT '是否删除 0 否 NULL 是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id VARCHAR(64) UNIQUE NOT NULL COMMENT '用户id',
    user_name VARCHAR(128) NOT NULL COMMENT '用户昵称',
    pet_type VARCHAR(64) NOT NULL COMMENT '宠物类型',
    pet_name VARCHAR(64) NOT NULL COMMENT '宠物昵称',
    pet_avatar VARCHAR(256) NOT NULL COMMENT '宠物头像',
    chat_info VARCHAR(1024) NOT NULL COMMENT '聊天信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE IF NOT EXISTS `pet_user_profile` (
   `id` int(10) unsigned NOT NULL COMMENT 'ID',
   `role_type` tinyint(4) unsigned NOT NULL COMMENT '角色类型',
   `user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
   `profile` varchar(1024) NOT NULL COMMENT '档案',
   `status` tinyint(4) NOT NULL COMMENT '状态',
   `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
   PRIMARY KEY (`id`),
   KEY `user_id` (`user_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

CREATE TABLE IF NOT EXISTS `pet_chat_room` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    `user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
    `chat_user_id` int(10) unsigned NOT NULL COMMENT '聊天对象用户ID',
    `chat_context` varchar(1024) NOT NULL COMMENT '上下文',
    `status` tinyint(4) NOT NULL COMMENT '状态',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    KEY `user_id` (`user_id`,`chat_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物聊天室表';