USE library;

-- 图书类别表（先创建）
CREATE TABLE IF NOT EXISTS tb_bookType
(
    number   VARCHAR(13) PRIMARY KEY COMMENT '类别编号',
    typeName VARCHAR(20) NOT NULL COMMENT '类别名称',
    days     INT         NOT NULL DEFAULT 30 CHECK (days BETWEEN 1 AND 365) COMMENT '可借天数',
    fk       FLOAT       NOT NULL DEFAULT 0.5 CHECK (fk >= 0) COMMENT '罚款金额',
    UNIQUE KEY (typeName)
);