package com.ukj.exam.servlet;

import com.ukj.exam.Config;
import com.ukj.exam.exception.SQLErrorException;
import com.ukj.exam.util.DBUtil;
import com.ukj.exam.util.SecSql;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/article/doWrite")
public class ArticleDoWriteServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    Connection conn = null;
    String driverName = Config.getDriverClassName();

    try {
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      System.out.printf("[ClassNotFoundException 예외, %s]", e.getMessage());
      resp.getWriter().append("DB 드라이버 클래스 로딩 실패");
      return;

    }

    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");
    resp.setContentType("text/html; charset-utf-8");

    try {
      conn = DriverManager.getConnection(Config.getDBUrl(), Config.getDBId(), Config.getDBPw());

//------------------------------------------------------

      String title = req.getParameter("title");
      String body = req.getParameter("body");

      SecSql sql = SecSql.from("INSERT INTO article");
      sql.append("(regDate, updateDate, title, body)");
      sql.append("VALUES");
      sql.append("(NOW(), NOW(), ?, ?)", title, body);

      //CONCAT('title__', RAND())

      int id = DBUtil.insert(conn, sql);

      resp.getWriter().append(String.format("<script> alert('%d번 게시글이 등록되었습니다.');" +
          "location.replace('list')</script>", id));

//------------------------------------------------------

    } catch (SQLException e) {
      System.out.printf("[ClassNotFoundException 예외, %s]", e.getMessage());
      resp.getWriter().append("DB 드라이버 클래스 로딩 실패");
      return;

    } catch (SQLErrorException e) {
      e.getOrigin().printStackTrace();

    } finally {
      try {
        if (conn != null && !conn.isClosed()) {
          conn.close();

        }

      } catch (SQLException e) {
        e.printStackTrace();

      }

    }

  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

}
