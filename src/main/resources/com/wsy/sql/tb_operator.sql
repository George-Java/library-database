USE library;

-- 操作员信息表
CREATE TABLE IF NOT EXISTS tb_operator
(
    id           INT PRIMARY KEY COMMENT '操作员编号',
    name         VARCHAR(12) NOT NULL COMMENT '姓名',
    sex          VARCHAR(2) COMMENT '性别',
    age          INT COMMENT '年龄',
    phone        VARCHAR(13) COMMENT '电话',
    identityCard VARCHAR(30) NOT NULL COMMENT '身份证号',
    workDate     DATETIME    NOT NULL COMMENT '工作日期',
    admin        BOOLEAN DEFAULT 0 COMMENT '是否为管理员',
    userName     VARCHAR(20) NOT NULL COMMENT '用户名',
    password     VARCHAR(10) NOT NULL COMMENT '密码',
    UNIQUE KEY (userName),
    UNIQUE KEY (identityCard)
) COMMENT '操作员信息表&用户表';