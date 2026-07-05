USE library;

-- 读者表
CREATE TABLE IF NOT EXISTS tb_reader
(
    barcode        VARCHAR(13) PRIMARY KEY COMMENT '读者条形码',
    name           VARCHAR(10)  NOT NULL COMMENT '姓名',
    sex            VARCHAR(2) COMMENT '性别',
    age            INT COMMENT '年龄',
    profession     VARCHAR(20) COMMENT '职业',
    type           VARCHAR(15)  NOT NULL COMMENT '有效证件类型',
    identityCard   VARCHAR(30)  NOT NULL COMMENT '证件号码',
    maxNum         INT UNSIGNED NOT NULL DEFAULT 5 COMMENT '最大借书数量',
    date           DATETIME     NOT NULL COMMENT '会员有效日期',
    phone          VARCHAR(13)  NOT NULL COMMENT '电话',
    keepMoney      FLOAT        NOT NULL DEFAULT 100.00 COMMENT '押金',
    dateOfIssuance DATE         NOT NULL COMMENT '办证日期',
    UNIQUE KEY (identityCard)
);