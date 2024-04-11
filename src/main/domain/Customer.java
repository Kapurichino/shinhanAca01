package main.domain;

import main.Management;
import main.Ojdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Customer extends Member{
	public void order() {
		boolean flag = true;
		Map<Product, Integer> map = new HashMap<>();

		while (flag){
			Management.findAllProduct();
			System.out.print("몇 번 상품을 구매하시겠습니까?: ");
			long productNo = Long.parseLong(Ojdbc.sc.nextLine());
			Product product = Management.findProduct(productNo);
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
			System.out.println("--------------------");
			/* todo 장바구니 상품 취소 */
			System.out.print("다른 상품을 더 구매하시겠습니까??(Y/N): ");
			String answer = Ojdbc.sc.nextLine();
			if("N".equals(answer)){
				flag = false;
			}
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
			try {
				String sql = "" +
						"INSERT INTO order_history (id, product_id, order_id, order_date, cancel, quantity) " +
						"VALUES (?, ?, ?, sysdate, ?, ?)";

				Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
				Ojdbc.pstmt.setString(1,getId());
				Ojdbc.pstmt.setLong(2, product.getProduct_id());
				Ojdbc.pstmt.setLong(3, nextVal);
				Ojdbc.pstmt.setString(4, "N");
				Ojdbc.pstmt.setLong(5, quantity);

				int rows = Ojdbc.pstmt.executeUpdate();
				System.out.println("저장된 행 수 : "+rows);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
