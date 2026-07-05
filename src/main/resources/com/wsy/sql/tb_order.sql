USE library;

-- 图书订购表
CREATE TABLE IF NOT EXISTS tb_order
(
    orderId        INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单编号',
    bookISBN       CHAR(13) NOT NULL COMMENT '书籍编号',
    date           DATETIME NOT NULL COMMENT '订购日期',
    number         INT      NOT NULL COMMENT '订购数量',
    operator       INT      NOT NULL COMMENT '操作员ID', -- 修改为关联操作员ID
    checkAndAccept INT      NOT NULL DEFAULT 0 COMMENT '是否验收（0-未验收，1-已验收）',
    discount       FLOAT    NOT NULL DEFAULT 0.0 COMMENT '折扣',
    CONSTRAINT fk_order_book FOREIGN KEY (bookISBN)
        REFERENCES tb_bookInfo (bookISBN) ON DELETE CASCADE,
    CONSTRAINT fk_order_operator FOREIGN KEY (operator)
        REFERENCES tb_operator (id) ON DELETE CASCADE
);