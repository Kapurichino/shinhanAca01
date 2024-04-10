package main.domain;

import main.Ojdbc;

import java.sql.SQLException;

public class Customer extends Member{
	public void order() {

	}

	public void checkHistory() {

	}

	public void authorizeStudent(Customer customer) {
		if("student".equals(customer.getMember_type())){
			System.out.println("이미 학생 인증을 마치셨습니다");
			return;
		}
		System.out.println("--------------------------------------------------------");
		System.out.print("학생(Y/N): ");
		String status = Ojdbc.sc.nextLine();

		try {
			String sql = new StringBuilder()
					.append("UPDATE member SET ")
					.append("member_type =? ")
					.append("WHERE id =?")
					.toString();
			Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
			if ("Y".equals(status)) {
				Ojdbc.pstmt.setString(1, "student");
				customer.setMember_type("student");
			} else if ("N".equals(status)) {
				Ojdbc.pstmt.setString(1, "nonstudent");
				customer.setMember_type("nonstudent");
			} else {
				throw new Exception();
			}
			Ojdbc.pstmt.setString(2, customer.getId());

			Ojdbc.pstmt.executeUpdate();

			System.out.println("학생 인증에 성공하였습니다.");
			Ojdbc.pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public Customer(String id, String pwd, String name, String tel, String member_type) {
		super(id, pwd, name, tel, member_type);
	}
}
