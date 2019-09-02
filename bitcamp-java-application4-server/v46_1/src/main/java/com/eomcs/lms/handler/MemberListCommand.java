package com.eomcs.lms.handler;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.eomcs.lms.dao.MemberDao;
import com.eomcs.lms.domain.Member;

public class MemberListCommand implements Command {
  private SqlSessionFactory sqlSessionFactory;
  
  public MemberListCommand(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionFactory = sqlSessionFactory;
  }
  
  @Override
  public void execute(BufferedReader in, PrintStream out) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      MemberDao memberDao = sqlSession.getMapper(MemberDao.class);
      
      List<Member> members = memberDao.findAll();
      for (Member member : members) {
        out.printf("%s, %s, %s, %s, %s\n", 
            member.getNo(), member.getName(), member.getEmail(), 
            member.getTel(), member.getRegisteredDate());
      }

    } catch (Exception e) {
      out.println("데이터 목록 조회에 실패했습니다!");
      System.out.println(e.getMessage());
    }
  }

}