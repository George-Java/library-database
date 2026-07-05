package com.wsy.mapper;

import com.wsy.entity.BookInfo;
import com.wsy.entity.BookType;
import com.wsy.entity.Borrow;
import com.wsy.entity.Operator;
import com.wsy.entity.Order;
import com.wsy.entity.Reader;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface LibraryMapper {
    @Select("SELECT COUNT(*) FROM tb_operator WHERE userName = #{username}")
    int countOperatorByUsername(String username);

    @Select("SELECT COUNT(*) FROM tb_operator WHERE userName = #{username} AND password = #{password}")
    int countOperatorByCredentials(@Param("username") String username, @Param("password") String password);

    @Select("SELECT id, name, sex, age, phone, identityCard, workDate, admin, userName, password FROM tb_operator ORDER BY id")
    List<Operator> selectAllOperators();

    @Select("""
            <script>
            SELECT id, name, sex, age, phone, identityCard, workDate, admin, userName, password
              FROM tb_operator
             <where>
               <choose>
                 <when test="searchType == 1">id LIKE CONCAT('%', #{keyword}, '%')</when>
                 <when test="searchType == 2">name LIKE CONCAT('%', #{keyword}, '%')</when>
                 <when test="searchType == 3">userName LIKE CONCAT('%', #{keyword}, '%')</when>
                 <when test="searchType == 4">identityCard LIKE CONCAT('%', #{keyword}, '%')</when>
               </choose>
             </where>
             ORDER BY id
            </script>
            """)
    List<Operator> searchOperators(@Param("searchType") int searchType, @Param("keyword") String keyword);

    @Select("SELECT id, name, sex, age, phone, identityCard, workDate, admin, userName, password FROM tb_operator WHERE userName = #{username}")
    Operator selectOperatorByUsername(String username);

    @Select("SELECT name FROM tb_operator WHERE id = #{id}")
    String selectOperatorNameById(int id);

    @Select("SELECT COUNT(*) FROM tb_operator WHERE id = #{id}")
    int countOperatorById(int id);

    @Select("SELECT COUNT(*) FROM tb_operator WHERE identityCard = #{identityCard}")
    int countOperatorByIdentityCard(String identityCard);

    @Insert("""
            INSERT INTO tb_operator (id, name, sex, age, phone, identityCard, workDate, admin, userName, password)
            VALUES (#{id}, #{name}, #{sex}, #{age}, #{phone}, #{identityCard}, #{workDate}, #{admin}, #{userName}, #{password})
            """)
    int insertOperator(Operator operator);

    @Update("""
            <script>
            UPDATE tb_operator
               SET name = #{name},
                   sex = #{sex},
                   age = #{age},
                   phone = #{phone},
                   identityCard = #{identityCard},
                   workDate = #{workDate},
                   admin = #{admin},
                   userName = #{userName}
                   <if test="password != null and password != ''">, password = #{password}</if>
             WHERE id = #{id}
            </script>
            """)
    int updateOperator(Operator operator);

    @Update("UPDATE tb_operator SET password = #{password} WHERE userName = #{username}")
    int updateOperatorPassword(@Param("username") String username, @Param("password") String password);

    @Delete("DELETE FROM tb_operator WHERE id = #{id}")
    int deleteOperator(int id);

    @Select("SELECT number, typeName, days, fk FROM tb_bookType ORDER BY number")
    List<BookType> selectAllBookTypes();

    @Select("""
            SELECT number, typeName, days, fk
              FROM tb_bookType
             WHERE number LIKE CONCAT('%', #{keyword}, '%')
                OR typeName LIKE CONCAT('%', #{keyword}, '%')
             ORDER BY number
            """)
    List<BookType> searchBookTypes(String keyword);

    @Select("SELECT COUNT(*) FROM tb_bookType WHERE number = #{number}")
    int countBookTypeByNumber(String number);

    @Select("SELECT COUNT(*) FROM tb_bookType WHERE typeName = #{typeName}")
    int countBookTypeByName(String typeName);

    @Select("SELECT number FROM tb_bookType WHERE typeName = #{typeName} AND number != #{number} LIMIT 1")
    String selectDuplicateBookTypeNumber(@Param("typeName") String typeName, @Param("number") String number);

    @Insert("INSERT INTO tb_bookType (number, typeName, days, fk) VALUES (#{number}, #{typeName}, #{days}, #{fk})")
    int insertBookType(BookType bookType);

    @Update("UPDATE tb_bookType SET typeName = #{typeName}, days = #{days}, fk = #{fk} WHERE number = #{number}")
    int updateBookType(BookType bookType);

    @Delete("DELETE FROM tb_bookType WHERE number = #{number}")
    int deleteBookType(String number);

    @Select("SELECT COUNT(*) FROM tb_bookInfo WHERE category = #{category}")
    int countBooksByCategory(String category);

    @Select("SELECT bookISBN, category, bookname, writer, publisher, translator, date, price FROM tb_bookInfo")
    List<BookInfo> selectAllBookInfos();

    @Select("SELECT bookISBN, category, bookname, writer, publisher, translator, date, price FROM tb_bookInfo WHERE bookISBN = #{bookISBN}")
    BookInfo selectBookInfoByIsbn(String bookISBN);

    @Select("SELECT COUNT(*) FROM tb_bookInfo WHERE bookISBN = #{bookISBN}")
    int countBookInfoByIsbn(String bookISBN);

    @Select("SELECT DISTINCT publisher FROM tb_bookInfo WHERE publisher IS NOT NULL AND publisher <> '' ORDER BY publisher")
    List<String> selectDistinctPublishers();

    @Select("""
            SELECT b.bookISBN, b.category, b.bookname, b.writer, b.publisher, b.translator, b.date, b.price
              FROM tb_bookInfo b
              JOIN tb_bookType bt ON b.category = bt.number
             ORDER BY b.bookISBN
            """)
    List<BookInfo> selectBooksForUpdate();

    @Select("SELECT bookISBN, category, bookname, writer, publisher, translator, date, price FROM tb_bookInfo WHERE bookname LIKE CONCAT('%', #{keyword}, '%')")
    List<BookInfo> searchBookInfosByName(String keyword);

    @Select("SELECT bookISBN, category, bookname, writer, publisher, translator, date, price FROM tb_bookInfo WHERE bookISBN = #{bookISBN}")
    List<BookInfo> searchBookInfosByIsbn(String bookISBN);

    @Select("""
            SELECT bookISBN, category, bookname, writer, publisher, translator, date, price
              FROM tb_bookInfo
             WHERE writer LIKE CONCAT('%', #{keyword}, '%')
                OR translator LIKE CONCAT('%', #{keyword}, '%')
            """)
    List<BookInfo> searchBookInfosByAuthor(String keyword);

    @Insert("""
            INSERT INTO tb_bookInfo (bookISBN, category, bookname, writer, publisher, translator, date, price)
            VALUES (#{bookISBN}, #{category}, #{bookname}, #{writer}, #{publisher}, #{translator}, #{date}, #{price})
            """)
    int insertBookInfo(BookInfo bookInfo);

    @Update("""
            UPDATE tb_bookInfo
               SET category = #{category},
                   bookname = #{bookname},
                   writer = #{writer},
                   publisher = #{publisher},
                   translator = #{translator},
                   date = #{date},
                   price = #{price}
             WHERE bookISBN = #{bookISBN}
            """)
    int updateBookInfo(BookInfo bookInfo);

    @Delete("DELETE FROM tb_bookInfo WHERE bookISBN = #{bookISBN}")
    int deleteBookInfo(String bookISBN);

    @Select("""
            SELECT b.bookISBN, b.bookname, bt.typeName AS categoryName, b.writer, b.translator,
                   b.publisher, b.date, b.price, s.stockQuantity
              FROM tb_bookInfo b
              JOIN tb_bookType bt ON b.category = bt.number
              LEFT JOIN tb_stockpile s ON b.bookISBN = s.bookISBN
             WHERE b.bookISBN = #{bookISBN}
            """)
    Map<String, Object> selectBookDetails(String bookISBN);

    @Select("""
            SELECT b.bookname, bt.typeName AS category, b.price, s.stockQuantity
              FROM tb_bookInfo b
              JOIN tb_bookType bt ON b.category = bt.number
              LEFT JOIN tb_stockpile s ON b.bookISBN = s.bookISBN
             WHERE b.bookISBN = #{bookISBN}
            """)
    Map<String, Object> selectBorrowBookInfo(String bookISBN);

    @Select("SELECT bookname FROM tb_bookInfo WHERE bookISBN = #{bookISBN}")
    String selectBookNameByIsbn(String bookISBN);

    @Select("SELECT bt.days FROM tb_bookInfo b JOIN tb_bookType bt ON b.category = bt.number WHERE b.bookISBN = #{bookISBN}")
    Integer selectBookRuleDays(String bookISBN);

    @Select("SELECT bt.fk FROM tb_bookInfo b JOIN tb_bookType bt ON b.category = bt.number WHERE b.bookISBN = #{bookISBN}")
    Double selectBookFinePerDay(String bookISBN);

    @Select("""
            SELECT b.bookname, s.stockQuantity
              FROM tb_stockpile s
              JOIN tb_bookInfo b ON s.bookISBN = b.bookISBN
             ORDER BY s.stockQuantity DESC
             LIMIT 5
            """)
    List<Map<String, Object>> selectTopStockBooks();

    @Select("""
            SELECT b.bookname, COUNT(*) AS borrowCount
              FROM tb_borrow br
              JOIN tb_bookInfo b ON br.bookISBN = b.bookISBN
             GROUP BY br.bookISBN
             ORDER BY borrowCount DESC
             LIMIT 5
            """)
    List<Map<String, Object>> selectTopBorrowedBooks();

    @Select("SELECT stockQuantity FROM tb_stockpile WHERE bookISBN = #{bookISBN}")
    Integer selectStockQuantity(String bookISBN);

    @Insert("INSERT INTO tb_stockpile (bookISBN, stockQuantity) VALUES (#{bookISBN}, #{stockQuantity})")
    int insertStockpile(@Param("bookISBN") String bookISBN, @Param("stockQuantity") int stockQuantity);

    @Update("UPDATE tb_stockpile SET stockQuantity = stockQuantity + #{quantity} WHERE bookISBN = #{bookISBN}")
    int addStockQuantity(@Param("bookISBN") String bookISBN, @Param("quantity") int quantity);

    @Update("UPDATE tb_stockpile SET stockQuantity = #{stockQuantity} WHERE bookISBN = #{bookISBN}")
    int updateStockQuantity(@Param("bookISBN") String bookISBN, @Param("stockQuantity") int stockQuantity);

    @Delete("DELETE FROM tb_stockpile WHERE bookISBN = #{bookISBN}")
    int deleteStockpile(String bookISBN);

    @Select("SELECT barcode, name, sex, age, profession, type, identityCard, maxNum, date, phone, keepMoney, dateOfIssuance FROM tb_reader ORDER BY dateOfIssuance DESC")
    List<Reader> selectAllReaders();

    @Select("SELECT barcode, name, sex, age, profession, type, identityCard, maxNum, date, phone, keepMoney, dateOfIssuance FROM tb_reader WHERE barcode = #{barcode}")
    Reader selectReaderByBarcode(String barcode);

    @Select("SELECT COUNT(*) FROM tb_borrow WHERE readerNumber = #{readerNumber} AND isBack = 0")
    int countActiveBorrowsByReader(String readerNumber);

    @Insert("""
            INSERT INTO tb_reader (barcode, name, sex, age, profession, type, identityCard, maxNum, date, phone, keepMoney, dateOfIssuance)
            VALUES (#{barcode}, #{name}, #{sex}, #{age}, #{profession}, #{type}, #{identityCard}, #{maxNum}, #{date}, #{phone}, #{keepMoney}, #{dateOfIssuance})
            """)
    int insertReader(Reader reader);

    @Update("""
            UPDATE tb_reader
               SET barcode = #{reader.barcode},
                   name = #{reader.name},
                   sex = #{reader.sex},
                   age = #{reader.age},
                   profession = #{reader.profession},
                   type = #{reader.type},
                   identityCard = #{reader.identityCard},
                   maxNum = #{reader.maxNum},
                   date = #{reader.date},
                   phone = #{reader.phone},
                   keepMoney = #{reader.keepMoney},
                   dateOfIssuance = #{reader.dateOfIssuance}
             WHERE barcode = #{oldBarcode}
            """)
    int updateReader(@Param("reader") Reader reader, @Param("oldBarcode") String oldBarcode);

    @Delete("DELETE FROM tb_reader WHERE barcode = #{barcode}")
    int deleteReader(String barcode);

    @Select("SELECT borrowId, readerNumber, bookISBN, operatorId, isBack, borrowDate, backDate FROM tb_borrow ORDER BY borrowId")
    List<Borrow> selectAllBorrows();

    @Select("SELECT borrowId, readerNumber, bookISBN, operatorId, isBack, borrowDate, backDate FROM tb_borrow WHERE readerNumber = #{readerNumber} AND isBack = 0 ORDER BY borrowDate DESC")
    List<Borrow> selectUnreturnedBorrowsByReader(String readerNumber);

    @Insert("INSERT INTO tb_borrow (readerNumber, bookISBN, operatorId, borrowDate, isBack) VALUES (#{readerNumber}, #{bookISBN}, #{operatorId}, NOW(), 0)")
    int insertBorrow(@Param("readerNumber") String readerNumber, @Param("bookISBN") String bookISBN, @Param("operatorId") int operatorId);

    @Update("UPDATE tb_borrow SET isBack = 1, backDate = NOW() WHERE borrowId = #{borrowId}")
    int markBorrowReturned(int borrowId);

    @Select("SELECT orderId, bookISBN, date, number, operator, checkAndAccept, discount FROM tb_order WHERE checkAndAccept = 0")
    List<Order> selectUnacceptedOrders();

    @Insert("""
            INSERT INTO tb_order (bookISBN, date, number, operator, checkAndAccept, discount)
            VALUES (#{bookISBN}, #{date}, #{number}, #{operator}, #{checkAndAccept}, #{discount})
            """)
    int insertOrder(Order order);

    @Update("UPDATE tb_order SET checkAndAccept = 1 WHERE orderId = #{orderId}")
    int markOrderAccepted(int orderId);

    @Delete("DELETE FROM tb_order WHERE orderId = #{orderId}")
    int deleteOrder(int orderId);
}
