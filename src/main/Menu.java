package main;

import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Data;
import main.domain.Member;

@Data
public class Menu {
	private static Menu menu = null;
	private String no;
	
	Menu(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

		    Ojdbc.conn = DriverManager.getConnection(
		            "jdbc:oracle:thin:@localhost:1521:xe",
		            "shinhan",
		            "shinhan1234"
		    );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			// TODO: handle exception
		} catch (SQLException  e) {
			e.printStackTrace();
		}
		
	}
	
	public void show() {
		Member member = new Member();
		System.out.println("------------------------------");
		System.out.println("메인 메뉴: 1. 로그인 | 2. 회원가입");
		System.out.println("------------------------------");
		System.out.print("메뉴 선택: ");
        setNo(Ojdbc.sc.nextLine());
        
        switch (getNo()){
            case "1" -> member.signIn();
            case "2" -> member.signUp();
        }
	}
	
	public static Menu getInstance() {
		if(menu == null) {
			menu = new Menu();
		}
		return menu;
	}
}
