package main.domain;

import main.Controller;
import main.Ojdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Customer extends Member{
	public void order(){
		boolean flag = true;
		Map<Product, Integer> map = new HashMap<>();
		try {

			while (flag){
				Controller.findAllProduct(this);
				System.out.print("몇 번 상품을 구매하시겠습니까?: ");
				try {
				long productNo = Long.parseLong(Ojdbc.sc.nextLine());
				Product product = Controller.findProduct(productNo, this);
				if(product == null){
					throw new NullPointerException();
				}
				System.out.print("몇 개를 구매하시겠습니까?: ");
				int quantity = Integer.parseInt(Ojdbc.sc.nextLine());

				if(map.containsKey(product)){
					map.put(product, map.get(product)+quantity);
				}else{
					map.put(product, quantity);
				}

				System.out.println("장바구니에 추가되었습니다.");
				System.out.println("--------------------");
				System.out.println("장바구니");

					for (Map.Entry<Product, Integer> entry : map.entrySet()) {
						String productName = entry.getKey().getProduct_name();
						Integer value = entry.getValue();
						long price = entry.getKey().getPrice();
						long discountRate = entry.getKey().getDiscount_rate();
						if("student".equals(getMember_type())){
							System.out.printf("상품명 : %-8s 수량 : %-4d 총가격 : %-8d\n",productName, value, price*(100-discountRate)/100*value);
						}else{
							System.out.printf("상품명 : %-8s 수량 : %-4d 총가격 : %-8d\n",productName, value, price*value);
						}
					}
				}catch (NullPointerException e){
					System.out.println("해당 상품은 존재하지 않습니다.");
					break;
				}

				System.out.println("--------------------");
				System.out.print("다른 상품을 더 구매하시겠습니까??(Y/N): ");
				String answer = Ojdbc.sc.nextLine();
				if("N".equals(answer)){
					flag = false;
				}
			}
		}catch (NumberFormatException e){
			System.out.println("숫자를 입력해주세요");
		}

		int nextVal = -1;

		try {
			String sql = "SELECT ORDER_HISTORY_SEQUENCE.NEXTVAL FROM dual ";
			Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
			ResultSet rs = Ojdbc.pstmt.executeQuery();
			while (rs.next()){
				nextVal = rs.getInt("NEXTVAL");
			}
		}catch (Exception e){}

		for (Map.Entry<Product, Integer> entry : map.entrySet()) {
			Product product = entry.getKey();
			Integer quantity = entry.getValue();
			Long price = entry.getKey().getPrice();
			Long discountRate = entry.getKey().getDiscount_rate();
			try {
				String sql = "" +
						"INSERT INTO order_history (id, product_id, order_id, order_date, cancel, quantity, total_price, product_name) " +
						"VALUES (?, ?, ?, sysdate, ?, ?, ?, ?)";

				Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
				Ojdbc.pstmt.setString(1,getId());
				Ojdbc.pstmt.setLong(2, product.getProduct_id());
				Ojdbc.pstmt.setLong(3, nextVal);
				Ojdbc.pstmt.setString(4, "N");
				Ojdbc.pstmt.setLong(5, quantity);
				if("student".equals(getMember_type())){
					Ojdbc.pstmt.setLong(6, price*(100-discountRate)/100*quantity);
				}else{
					Ojdbc.pstmt.setLong(6, price*quantity);
				}
				Ojdbc.pstmt.setString(7, product.getProduct_name());

				int res = Ojdbc.pstmt.executeUpdate();
				if(res>0){
					System.out.print("상품을 정상적으로 구매하였습니다.");
				}

				Ojdbc.pstmt.close();
				Ojdbc.rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void checkMonthlyOrderHistory(String date){
		String sql = "SELECT order_id, product_name, quantity, order_date, cancel, total_price " +
				"FROM order_history " +
				"WHERE id = ? and TO_CHAR(order_date, 'YYYYMM') = ?";
		try {
			Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
			Ojdbc.pstmt.setString(1, getId());
			Ojdbc.pstmt.setString(2, date);

			Ojdbc.rs = Ojdbc.pstmt.executeQuery();
			Controller.showOrderHistory();

			Ojdbc.pstmt.close();
			Ojdbc.rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkTotalOrderHistory() {
		String sql = "SELECT order_id, product_name, quantity, order_date, cancel, total_price " +
				"FROM order_history " +
				"WHERE id = ?";
		try {
			Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
			Ojdbc.pstmt.setString(1, getId());

			Ojdbc.rs = Ojdbc.pstmt.executeQuery();
			Controller.showOrderHistory();

			Ojdbc.pstmt.close();
			Ojdbc.rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void authorizeStudent() {
		if("student".equals(getMember_type())){
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
				setMember_type("student");
			} else if ("N".equals(status)) {
				Ojdbc.pstmt.setString(1, "nonstudent");
				setMember_type("nonstudent");
			} else {
				throw new Exception();
			}
			Ojdbc.pstmt.setString(2, getId());

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
