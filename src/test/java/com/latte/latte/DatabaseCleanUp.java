package com.latte.latte;

import com.latte.member.mapper.AuthMapper;
import com.latte.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleanUp implements InitializingBean {

    private final SqlSessionFactory sqlSessionFactory;
    private AuthMapper authMapper;
    private List<String> tableNames = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            // MyBatis Mapper를 이용하여 Mapper XML에서 필요한 정보를 가져옴
            ///tableNames = memberMapper.getTableNames();
        }
    }

    @Transactional
    public void truncateAllEntity() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            sqlSession.getConnection().setAutoCommit(false);

            for (String tableName : tableNames) {
                sqlSession.update("your.namespace.truncateTable", tableName);
            }

            sqlSession.getConnection().commit();
        } catch (Exception e) {
            log.error("Error truncating tables", e);
        }
    }
}
