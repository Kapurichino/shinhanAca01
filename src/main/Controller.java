package main;

import main.domain.Administrator;
import main.domain.Customer;
import main.domain.Member;
import main.domain.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {
    public static void modifyMemberInfo(Member member) {
        String no;
        String id = member.getId();
        String pwd = member.getPwd();
        String name = member.getName();
        String tel = member.getTel();
        while(true) {
            System.out.println("------------------------------------------------------------------");
            System.out.println("1. 아이디 수정 | 2. 패스워드 수정 | 3. 이름 수정 | 4. 전화번호 수정 | 5. 나가기");
            System.out.println("------------------------------------------------------------------");
            System.out.println("회원 정보 ID: " + member.getId() + " | 이름: " + member.getName() + " | 전화번호: " + member.getTel());
            System.out.println();
            System.out.print("메뉴 선택 : ");
            no = Ojdbc.sc.nextLine();

            if ("5".equals(no)) break;

            try {
                switch (no) {
                    case "1":
                        System.out.print("수정할 회원 아이디: ");
                        id = Ojdbc.sc.nextLine();
                        Ojdbc.sql = "UPDATE member SET id = ? WHERE id = ?";
                        Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                        Ojdbc.pstmt.setString(1, id);
                        break;
                    case "2":
                        System.out.print("수정할 회원 패스워드: ");
                        pwd = Ojdbc.sc.nextLine();
                        Ojdbc.sql = "UPDATE member SET pwd = ? WHERE id = ?";
                        Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                        Ojdbc.pstmt.setString(1, pwd);
                        break;
                    case "3":
                        System.out.print("수정할 회원 이름: ");
                        name = Ojdbc.sc.nextLine();
                        Ojdbc.sql = "UPDATE member SET name = ? WHERE id = ?";
                        Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                        Ojdbc.pstmt.setString(1, name);
                        break;
                    case "4":
                        System.out.print("수정할 회원 전화번호: ");
                        tel = Ojdbc.sc.nextLine();
                        Ojdbc.sql = "UPDATE member SET tel = ? WHERE id = ?";
                        Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                        Ojdbc.pstmt.setString(1, tel);
                        break;
                }
                Ojdbc.pstmt.setString(2, member.getId());
                Ojdbc.pstmt.executeUpdate();
                member.setId(id);
                member.setPwd(pwd);
                member.setName(name);
                member.setTel(tel);
                System.out.println("정보가 정상적으로 변경되었습니다");
                Ojdbc.pstmt.close();
            } catch (SQLException e){
                System.out.println("이미 존재하는 회원 아이디입니다");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void signUp() throws Exception{
        Member member = new Member();
        System.out.println();
        System.out.print("아이디: ");
        member.setId(Ojdbc.sc.nextLine());
        System.out.print("비밀번호: ");
        member.setPwd(Ojdbc.sc.nextLine());
        System.out.print("이름: ");
        member.setName(Ojdbc.sc.nextLine());
        System.out.print("전화번호: ");
        member.setTel(Ojdbc.sc.nextLine());
        System.out.print("학생(Y/N): ");
        String status = Ojdbc.sc.nextLine();
        try {
            String sql = "" +
                    "INSERT INTO member (id, pwd, name, tel, member_type) " +
                    "VALUES (?,?,?,?,?)";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
            Ojdbc.pstmt.setString(1,member.getId());
            Ojdbc.pstmt.setString(2, member.getPwd());
            Ojdbc.pstmt.setString(3, member.getName());
            Ojdbc.pstmt.setString(4, member.getTel());
            if("Y".equals(status)){
                Ojdbc.pstmt.setString(5, "student");
            }else if("N".equals(status)){
                Ojdbc.pstmt.setString(5, "nonstudent");
            }else {
                throw new Exception();
            }

            int rows = Ojdbc.pstmt.executeUpdate();
            if(rows>0){
                System.out.println("회원가입에 성공했습니다");
            }
            Ojdbc.pstmt.close();

        } catch (SQLException e) {
            System.out.println("이미 존재하는 회원 아이디입니다");
        }
    }


    public static Member signIn() {
        Member member = null;
        try {
            String sql = "SELECT * FROM member WHERE id=? and pwd=?";
            System.out.println();
            System.out.print("아이디: ");
            String id = Ojdbc.sc.nextLine();
            System.out.print("비밀번호: ");
            String pwd = Ojdbc.sc.nextLine();
            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
            Ojdbc.pstmt.setString(1,id);
            Ojdbc.pstmt.setString(2,pwd);

            Ojdbc.rs = Ojdbc.pstmt.executeQuery();


            if(Ojdbc.rs.next()) {
                if("admin".equals(Ojdbc.rs.getString("member_type"))){
                    //어드민일시
                    Member admin = new Administrator(
                            Ojdbc.rs.getString("id"),
                            Ojdbc.rs.getString("pwd"),
                            Ojdbc.rs.getString("name"),
                            Ojdbc.rs.getString("tel"),
                            Ojdbc.rs.getString("member_type")
                    );
                    System.out.println("\n사용자정보");
                    System.out.printf("%-20s%-20s%-20s\n","id","이름","전화번호");
                    System.out.printf("%-20s%-20s%-20s",
                            admin.getId(),
                            admin.getName(),
                            admin.getTel());
                    Ojdbc.pstmt.close();
                    Ojdbc.rs.close();
                    member = admin;
                }else {
                    //커스터머일시
                    Member customer = new Customer(
                            Ojdbc.rs.getString("id"),
                            Ojdbc.rs.getString("pwd"),
                            Ojdbc.rs.getString("name"),
                            Ojdbc.rs.getString("tel"),
                            Ojdbc.rs.getString("member_type")
                    );
                    System.out.println("\n사용자정보");
                    System.out.printf("%-20s%-20s%-20s\n","id","이름","전화번호");
                    System.out.printf("%-20s%-20s%-20s",
                            customer.getId(),
                            customer.getName(),
                            customer.getTel());
                    Ojdbc.pstmt.close();
                    Ojdbc.rs.close();
                    member = customer;
                }

            } else {
                System.out.println("아이디와 비밀번호를 확인해주세요");
            }

            Ojdbc.pstmt.close();
            Ojdbc.rs.close();

        }catch (Exception e){
            System.out.println("제대로 된 값을 입력해주세요");
        }
        return member;
    }

    public static void findAllProduct() {
        try {
            String sql = "" +
                    "SELECT * FROM product ORDER BY product_id DESC ";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);

            ResultSet rs = Ojdbc.pstmt.executeQuery();
            System.out.printf("%-6s %-10s %-8s %-8s %-8s\n"
                    ,"상품 번호"
                    ,"상품 이름"
                    ,"상품 가격"
                    ,"할인율"
                    ,"할인적용가격");
            while (rs.next()) {
                Product product = new Product(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("discount_rate"),
                        rs.getLong("price")
                );

                System.out.printf("%-9d %-10s %-10d %-10d %-8d\n"
                        ,product.getProduct_id()
                        ,product.getProduct_name()
                        ,product.getPrice()
                        ,product.getDiscount_rate()
                        ,product.getPrice() * (100-product.getDiscount_rate()) / 100
                );
            }
        } catch (SQLException e) {
            System.out.println("정보를 불러올 수 없습니다");
        }
    }

    public static void findAllProduct(Customer customer) {
        try {
            String sql = "" +
                    "SELECT * FROM product ORDER BY product_id DESC ";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);

            ResultSet rs = Ojdbc.pstmt.executeQuery();
            if("student".equals(customer.getMember_type())){
                System.out.printf("%-6s %-10s %-8s %-8s %-8s\n"
                        ,"상품 번호"
                        ,"상품 이름"
                        ,"상품 가격"
                        ,"할인율"
                        ,"할인적용가격");
            } else {
                System.out.printf("%-6s %-10s %-8s\n"
                        ,"상품 번호"
                        ,"상품 이름"
                        ,"상품 가격");
            }

            while (rs.next()) {
                Product product = new Product(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("discount_rate"),
                        rs.getLong("price")
                );

                if("student".equals(customer.getMember_type())){
                    System.out.printf("%-9d %-10s %-10d %-10d %-8d\n"
                            ,product.getProduct_id()
                            ,product.getProduct_name()
                            ,product.getPrice()
                            ,product.getDiscount_rate()
                            ,product.getPrice() * (100-product.getDiscount_rate()) / 100
                    );
                } else {
                    System.out.printf("%-9d %-10s %-10d\n"
                            ,product.getProduct_id()
                            ,product.getProduct_name()
                            ,product.getPrice()
                    );
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Product findProduct(Long product_id){
        Product product = null;
        try {
            String sql = "" +
                    "SELECT * FROM product "+
                    "WHERE product_id=? ";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
            Ojdbc.pstmt.setLong(1,product_id);
            ResultSet rs = Ojdbc.pstmt.executeQuery();
            while (rs.next()) {
                product = new Product(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("discount_rate"),
                        rs.getLong("price")
                );

                System.out.printf("%-6d %-12s %-6d %-6d %-6d\n"
                        ,product.getProduct_id()
                        ,product.getProduct_name()
                        ,product.getPrice()
                        ,product.getDiscount_rate()
                        ,product.getPrice() * (100-product.getDiscount_rate()) / 100
                );
            }
            Ojdbc.pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public static Product findProduct(Long product_id, Customer customer){
        Product product = null;
        try {
            String sql = "" +
                    "SELECT * FROM product "+
                    "WHERE product_id=? ";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
            Ojdbc.pstmt.setLong(1,product_id);
            ResultSet rs = Ojdbc.pstmt.executeQuery();
            while (rs.next()) {
                product = new Product(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("discount_rate"),
                        rs.getLong("price")
                );

                if("student".equals(customer.getMember_type())){
                    System.out.printf("%-6d %-12s %-6d %-6d %-6d\n"
                            ,product.getProduct_id()
                            ,product.getProduct_name()
                            ,product.getPrice()
                            ,product.getDiscount_rate()
                            ,product.getPrice() * (100-product.getDiscount_rate()) / 100
                    );
                } else {
                    System.out.printf("%-6d %-12s %-6d\n"
                            ,product.getProduct_id()
                            ,product.getProduct_name()
                            ,product.getPrice()
                    );
                }


            }
            Ojdbc.pstmt.close();
        }catch (SQLException e) {
            System.out.println("상품 번호를 확인해주세요");
        }catch (NumberFormatException e){
            System.out.println("숫자만 입력해주세요");
        }catch (Exception e){
            System.out.println("제대로 된 값을 입력해주세요");
        }
        return product;
    }

    public static int showOrderHistory() throws Exception{
        int seq = 0;
        while (Ojdbc.rs.next()) {
            System.out.printf(
                    "주문 번호: %4d | 상품명: %10s | 수량: %4d | 주문날짜: %10s | 취소여부: %3s 총 금액: %10d\n",
                    Ojdbc.rs.getLong(1), Ojdbc.rs.getString(2), Ojdbc.rs.getLong(3),
                    Ojdbc.rs.getDate(4), Ojdbc.rs.getString(5), Ojdbc.rs.getLong(6)
            );
            seq++;
        }
        if (seq == 0) {
            System.out.println("주문 내역이 없습니다");
        }
        return seq;
    }

    public static boolean productIsExist(long product_id){
        Product product = null;
        try {
            String sql = "" +
                    "SELECT * FROM product "+
                    "WHERE product_id=? ";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
            Ojdbc.pstmt.setLong(1,product_id);
            ResultSet rs = Ojdbc.pstmt.executeQuery();
            while (rs.next()) {
                product = new Product(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("discount_rate"),
                        rs.getLong("price")
                );
            }
            Ojdbc.pstmt.close();
        }catch (Exception e){
        }
        if(product == null){
            return false;
        }
        return true;
    }
}
