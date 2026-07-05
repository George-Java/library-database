USE library;

-- 图书库存表
CREATE TABLE IF NOT EXISTS tb_stockpile
(
    bookISBN      CHAR(13) PRIMARY KEY COMMENT '书籍编号',
    stockQuantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    CONSTRAINT fk_stock_book FOREIGN KEY (bookISBN)
        REFERENCES tb_bookInfo (bookISBN) ON DELETE CASCADE
);