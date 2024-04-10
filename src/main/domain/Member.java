package main.domain;

import java.sql.SQLException;
import lombok.Data;
import main.Ojdbc;

@Data
public class Member {
	private String id;
	private String pwd;
	private String name;
	private String tel;
	private String member_type;
	
	public void signUp() {
		System.out.println();
		System.out.print("아이디: ");
		setId(Ojdbc.sc.nextLine());
		System.out.print("비밀번호: ");
		setPwd(Ojdbc.sc.nextLine());
		System.out.print("이름: ");
		setName(Ojdbc.sc.nextLine());
		System.out.print("전화번호: ");
		setTel(Ojdbc.sc.nextLine());
		System.out.print("학생: ");
		setMember_type(Ojdbc.sc.nextLine());
		try {
			String sql = "" +
					"INSERT INTO member (id, pwd, name, tel, member_type) " +
					"VALUES (?,?,?,?,?)";
			
			Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
			Ojdbc.pstmt.setString(1,getId());
			Ojdbc.pstmt.setString(2, getPwd());
			Ojdbc.pstmt.setString(3, getName());
			Ojdbc.pstmt.setString(4, getTel());
			Ojdbc.pstmt.setString(5, getMember_type());
			
			int rows = Ojdbc.pstmt.executeUpdate();
			System.out.println("저장된 행 수 : "+rows);
			Ojdbc.pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public void signout() {
		
	}
	
	public void signIn() {
		try {
			String sql = "SELECT * FROM member WHERE id=? and pwd=?";
			
			Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
			Ojdbc.pstmt.setString(1,"mint");
			Ojdbc.pstmt.setString(2,"1234");
			
			Ojdbc.rs = Ojdbc.pstmt.executeQuery();
			
			if(Ojdbc.rs.next()) {
				setId(Ojdbc.rs.getString("id"));
				setPwd(Ojdbc.rs.getString("pwd"));
				setName(Ojdbc.rs.getString("name"));
				setTel(Ojdbc.rs.getString("tel"));
				setMember_type(Ojdbc.rs.getString("member_type"));
				
				System.out.printf("%-6s%-12s%-16s%-40s \n",
                        getId(),
                        getPwd(),
                        getName(),
                        getTel());
			} else {
				
			}
			
			Ojdbc.pstmt.close();
			Ojdbc.rs.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
}
