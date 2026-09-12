package com.wsy.util;

import com.wsy.mapper.LibraryMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
    private static final Logger logger = Logger.getLogger(DBUtils.class.getName());
    private static final SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory();

    @FunctionalInterface
    public interface MapperAction<T> {
        T apply(LibraryMapper mapper) throws SQLException;
    }

    private static SqlSessionFactory buildSqlSessionFactory() {
        try (InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml")) {
            Properties properties = new Properties();
            properties.setProperty("db.driver", valueOrDefault("DB_DRIVER", "com.mysql.cj.jdbc.Driver"));
            properties.setProperty("db.url", valueOrDefault("DB_URL",
                    "jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai"));
            properties.setProperty("db.username", valueOrDefault("DB_USER", "root"));
            properties.setProperty("db.password", valueOrDefault("DB_PASSWORD", "123456"));
            return new SqlSessionFactoryBuilder().build(inputStream, properties);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "MyBatis 配置加载失败", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static String valueOrDefault(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String envValue = System.getenv(key);
        return envValue == null || envValue.isBlank() ? defaultValue : envValue;
    }

    public static <T> T query(MapperAction<T> action) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return action.apply(session.getMapper(LibraryMapper.class));
        } catch (PersistenceException ex) {
            throw unwrapSQLException(ex);
        }
    }

    public static <T> T transaction(MapperAction<T> action) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            T result = action.apply(session.getMapper(LibraryMapper.class));
            session.commit();
            return result;
        } catch (PersistenceException ex) {
            throw unwrapSQLException(ex);
        }
    }

    private static SQLException unwrapSQLException(PersistenceException ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof SQLException sqlException) {
                return sqlException;
            }
            current = current.getCause();
        }
        return new SQLException(ex.getMessage(), ex);
    }

    public static boolean userExists(String username) {
        try {
            return query(mapper -> mapper.countOperatorByUsername(username) > 0);
        } catch (SQLException ex) {
            logger.severe(ex.toString());
            return false;
        }
    }

    public static boolean authenticate(String username, String password) {
        try {
            return query(mapper -> mapper.countOperatorByCredentials(username, password) > 0);
        } catch (SQLException ex) {
            logger.severe(ex.toString());
            return false;
        }
    }
}
