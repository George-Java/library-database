USE library;

-- 图书信息表 - 核心修改在这里
CREATE TABLE IF NOT EXISTS tb_bookInfo
(
    bookISBN   CHAR(13) PRIMARY KEY COMMENT '书籍编号',
    category   VARCHAR(13) NOT NULL COMMENT '图书类别', -- 恢复NOT NULL约束
    bookname   VARCHAR(40) NOT NULL COMMENT '图书名称',
    writer     VARCHAR(20) COMMENT '作者',
    publisher  VARCHAR(50) NOT NULL COMMENT '出版社',
    translator VARCHAR(30) COMMENT '译者',
    date       DATE        NOT NULL COMMENT '出版日期',
    price      DOUBLE      NOT NULL COMMENT '图书价格',
    CONSTRAINT fk_book_category FOREIGN KEY (category)
        REFERENCES tb_bookType (number)
        ON DELETE RESTRICT                              -- 关键修改：阻止删除有关联图书的类别
);