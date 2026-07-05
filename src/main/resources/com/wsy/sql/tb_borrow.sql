USE library;

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