-- 创建数据库
CREATE DATABASE IF NOT EXISTS library;
USE library;

-- 操作员信息表
CREATE TABLE IF NOT EXISTS tb_operator
(
    id           INT PRIMARY KEY COMMENT '操作员编号',
    name         VARCHAR(20) NOT NULL COMMENT '姓名',
    sex          VARCHAR(10) COMMENT '性别',
    age          INT COMMENT '年龄',
    phone        VARCHAR(20) COMMENT '电话',
    identityCard VARCHAR(30) NOT NULL COMMENT '身份证号',
    workDate     DATETIME    NOT NULL COMMENT '工作日期',
    admin        BOOLEAN DEFAULT 0 COMMENT '是否为管理员',
    userName     VARCHAR(30) NOT NULL COMMENT '用户名',
    password     VARCHAR(30) NOT NULL COMMENT '密码',
    UNIQUE KEY (userName),
    UNIQUE KEY (identityCard)
) COMMENT '操作员信息表&用户表';

INSERT INTO tb_operator
VALUES (101, 'Jonny', '男', 24, '1000000', '452167200104064537', '2025-06-04 09:00:00', 1, 'root', 'myroot'),
       (102, '张静', '女', 28, '1000001', '452167199501154538', '2020-03-12 09:00:00', 0, 'zhangjing', 'jing123'),
       (103, '王明', '男', 30, '1000002', '452167199301215539', '2019-11-05 09:00:00', 0, 'wangming', 'ming456');

-- 图书类别表（先创建）
CREATE TABLE IF NOT EXISTS tb_bookType
(
    number   VARCHAR(13) PRIMARY KEY COMMENT '类别编号',
    typeName VARCHAR(30) NOT NULL COMMENT '类别名称',
    days     INT         NOT NULL DEFAULT 30 CHECK (days BETWEEN 1 AND 365) COMMENT '可借天数',
    fk       FLOAT       NOT NULL DEFAULT 0.5 CHECK (fk >= 0) COMMENT '罚款金额',
    UNIQUE KEY (typeName)
);

INSERT INTO tb_bookType (number, typeName, days, fk)
VALUES ('TP0001', '计算机', 30, 0.5),
       ('I00001', '文学', 45, 0.3),
       ('O00001', '数理化', 40, 0.4),
       ('F00001', '经济管理', 35, 0.5);

-- 读者表
CREATE TABLE IF NOT EXISTS tb_reader
(
    barcode        VARCHAR(13) PRIMARY KEY COMMENT '读者条形码',
    name           VARCHAR(20)  NOT NULL COMMENT '姓名',
    sex            VARCHAR(10) COMMENT '性别',
    age            INT COMMENT '年龄',
    profession     VARCHAR(30) COMMENT '职业',
    type           VARCHAR(30)  NOT NULL COMMENT '有效证件类型',
    identityCard   VARCHAR(30)  NOT NULL COMMENT '证件号码',
    maxNum         INT UNSIGNED NOT NULL DEFAULT 5 COMMENT '最大借书数量',
    date           DATETIME     NOT NULL COMMENT '会员有效日期',
    phone          VARCHAR(20)  NOT NULL COMMENT '电话',
    keepMoney      FLOAT        NOT NULL DEFAULT 100.00 COMMENT '押金',
    dateOfIssuance DATE         NOT NULL COMMENT '办证日期',
    UNIQUE KEY (identityCard)
);

INSERT INTO tb_reader (barcode, name, sex, age, profession, type, identityCard, maxNum, date, phone, keepMoney,
                       dateOfIssuance)
VALUES ('R20230001', '李明', '男', 25, '工程师', '身份证', '110101199801013456', 5, '2025-12-31 23:59:59',
        '13800138001', 100.00, '2023-01-15'),
       ('R20230002', '王芳', '女', 22, '研究生', '身份证', '210103200101023457', 5, '2024-12-31 23:59:59',
        '13900139002', 100.00, '2023-02-20'),
       ('R20230003', '张伟', '男', 30, '教师', '身份证', '310105199302034568', 8, '2026-12-31 23:59:59', '13700137003',
        200.00, '2023-03-10');

-- 图书信息表
CREATE TABLE IF NOT EXISTS tb_bookInfo
(
    bookISBN   CHAR(13) PRIMARY KEY COMMENT '书籍编号',
    category   VARCHAR(13) NOT NULL COMMENT '图书类别',
    bookname   VARCHAR(100) NOT NULL COMMENT '图书名称',
    writer     VARCHAR(100) COMMENT '作者',
    publisher  VARCHAR(100) NOT NULL COMMENT '出版社',
    translator VARCHAR(100) COMMENT '译者',
    date       DATE        NOT NULL COMMENT '出版日期',
    price      DOUBLE      NOT NULL COMMENT '图书价格',
    CONSTRAINT fk_book_category FOREIGN KEY (category)
        REFERENCES tb_bookType (number)
        ON DELETE RESTRICT
);

INSERT INTO tb_bookInfo (bookISBN, category, bookname, writer, publisher, translator, date, price)
VALUES ('9787121346244', 'TP0001', '深入理解计算机系统', 'Randal E.Bryant', '机械工业出版社', NULL, '2019-02-01',
        139.00),
       ('9787115474582', 'TP0001', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '袁国忠', '2020-07-01',
        89.00),
       ('9787020153981', 'I00001', '百年孤独', '加西亚·马尔克斯', '南海出版公司', '范晔', '2017-09-01', 55.00),
       ('9787544291170', 'I00001', '解忧杂货店', '东野圭吾', '南海出版公司', '李盈春', '2020-03-01', 59.60),
       ('9787040396638', 'O00001', '高等数学（第七版）', '同济大学数学系', '高等教育出版社', NULL, '2014-07-01', 58.70),
       ('9787111550697', 'F00001', '管理学：原理与方法', '周三多', '高等教育出版社', NULL, '2018-01-01', 52.00);

-- 图书库存表
CREATE TABLE IF NOT EXISTS tb_stockpile
(
    bookISBN      CHAR(13) PRIMARY KEY COMMENT '书籍编号',
    stockQuantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    CONSTRAINT fk_stock_book FOREIGN KEY (bookISBN)
        REFERENCES tb_bookInfo (bookISBN) ON DELETE CASCADE
);

INSERT INTO tb_stockpile (bookISBN, stockQuantity)
VALUES ('9787121346244', 15),
       ('9787115474582', 22),
       ('9787020153981', 8),
       ('9787544291170', 12),
       ('9787040396638', 30),
       ('9787111550697', 10);

-- 图书订购表
CREATE TABLE IF NOT EXISTS tb_order
(
    orderId        INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单编号',
    bookISBN       CHAR(13) NOT NULL COMMENT '书籍编号',
    date           DATETIME NOT NULL COMMENT '订购日期',
    number         INT      NOT NULL COMMENT '订购数量',
    operator       INT      NOT NULL COMMENT '操作员ID',
    checkAndAccept INT      NOT NULL DEFAULT 0 COMMENT '是否验收（0-未验收，1-已验收）',
    discount       FLOAT    NOT NULL DEFAULT 0.0 COMMENT '折扣',
    CONSTRAINT fk_order_book FOREIGN KEY (bookISBN)
        REFERENCES tb_bookInfo (bookISBN) ON DELETE CASCADE,
    CONSTRAINT fk_order_operator FOREIGN KEY (operator)
        REFERENCES tb_operator (id) ON DELETE CASCADE
);

INSERT INTO tb_order (bookISBN, date, number, operator, checkAndAccept, discount)
VALUES ('9787121346244', '2023-05-01 10:23:45', 5, 102, 0, 0.1),
       ('9787115474582', '2023-05-02 14:30:22', 10, 103, 1, 0.15),
       ('9787040396638', '2023-05-03 09:15:30', 15, 102, 1, 0.2),
       ('9787111550697', '2023-05-04 11:20:18', 8, 103, 0, 0.1);

-- 图书借阅表
CREATE TABLE IF NOT EXISTS tb_borrow
(
    borrowId     INT AUTO_INCREMENT PRIMARY KEY COMMENT '借阅ID',
    readerNumber VARCHAR(13) NOT NULL COMMENT '读者编号',
    bookISBN     CHAR(13)    NOT NULL COMMENT '书籍编号',
    operatorId   INT         NOT NULL COMMENT '操作员编号',
    isBack       INT         NOT NULL DEFAULT 0 COMMENT '是否归还（0-未还，1-已还）',
    borrowDate   DATETIME    NOT NULL COMMENT '借阅日期',
    backDate     DATETIME COMMENT '归还日期',
    CONSTRAINT fk_borrow_reader FOREIGN KEY (readerNumber)
        REFERENCES tb_reader (barcode) ON DELETE CASCADE,
    CONSTRAINT fk_borrow_book FOREIGN KEY (bookISBN)
        REFERENCES tb_bookInfo (bookISBN) ON DELETE CASCADE,
    CONSTRAINT fk_borrow_operator FOREIGN KEY (operatorId)
        REFERENCES tb_operator (id) ON DELETE CASCADE,
    INDEX idx_reader_book (readerNumber, bookISBN)
);

INSERT INTO tb_borrow (readerNumber, bookISBN, operatorId, isBack, borrowDate, backDate)
VALUES ('R20230001', '9787121346244', 102, 0, '2023-06-01 09:30:00', NULL),
       ('R20230001', '9787115474582', 102, 1, '2023-06-01 09:35:00', '2023-06-15 15:20:00'),
       ('R20230002', '9787020153981', 103, 0, '2023-06-02 10:20:00', NULL),
       ('R20230003', '9787040396638', 103, 0, '2023-06-03 14:15:00', NULL);

-- 创建视图：图书详细信息视图
CREATE OR REPLACE VIEW v_book_details AS
SELECT b.bookISBN      AS ISBN,
       b.bookname      AS book_name,
       bt.typeName     AS type_name,
       b.writer        AS writer,
       b.translator    AS translator,
       b.publisher     AS publisher,
       b.date          AS pub_date,
       b.price         AS price,
       s.stockQuantity AS stock_quantity,
       bt.days         AS borrow_days,
       bt.fk           AS daily_fine
FROM tb_bookInfo b
         JOIN tb_bookType bt ON b.category = bt.number
         JOIN tb_stockpile s ON b.bookISBN = s.bookISBN;

-- 创建视图：借阅详情视图
CREATE OR REPLACE VIEW v_borrow_details AS
SELECT b.borrowId     AS borrow_id,
       r.barcode      AS reader_barcode,
       r.name         AS reader_name,
       bi.bookname    AS book_name,
       o.name         AS operator_name,
       b.borrowDate   AS borrow_date,
       b.backDate     AS return_date,
       bt.days        AS allowed_days,
       IF(b.isBack = 1, 'returned', 'borrowed') AS status,
       IF(b.isBack = 0 AND DATEDIFF(CURRENT_DATE(), b.borrowDate) > bt.days, 
          CONCAT('Overdue ', GREATEST(0, DATEDIFF(CURRENT_DATE(), b.borrowDate) - bt.days), 
                 ' days, fine: ', (GREATEST(0, DATEDIFF(CURRENT_DATE(), b.borrowDate) - bt.days)) * bt.fk),
          NULL) AS overdue_info
FROM tb_borrow b
         JOIN tb_reader r ON b.readerNumber = r.barcode
         JOIN tb_bookInfo bi ON b.bookISBN = bi.bookISBN
         JOIN tb_bookType bt ON bi.category = bt.number
         JOIN tb_operator o ON b.operatorId = o.id;

-- 创建视图：订单详情视图
CREATE OR REPLACE VIEW v_order_details AS
SELECT o.orderId   AS order_id,
       o.bookISBN  AS isbn,
       b.bookname  AS book_name,
       o.date      AS order_date,
       o.number    AS quantity,
       op.name     AS operator_name,
       o.discount  AS discount,
       b.price     AS unit_price,
       (b.price * o.number * (1 - o.discount)) AS total_price,
       IF(o.checkAndAccept = 0, 'unchecked', 'checked') AS check_status
FROM tb_order o
         JOIN tb_bookInfo b ON o.bookISBN = b.bookISBN
         JOIN tb_operator op ON o.operator = op.id;

ALTER TABLE tb_borrow ADD INDEX idx_reader (readerNumber);
ALTER TABLE tb_bookInfo ADD INDEX idx_bookname (bookname);
