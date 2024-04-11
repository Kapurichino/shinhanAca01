package main;

import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Data;
import main.domain.Administrator;
import main.domain.Customer;
import main.domain.Member;

@Data

public class Menu {
    private static Menu menu = null;
    private String no;

    Menu() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Ojdbc.conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "sampleuser",
                    "sample1234"
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // TODO: handle exception
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void show() {
        while(true){
            System.out.println("---------------------------------------");
            System.out.println("메인 메뉴: 1. 로그인 | 2. 회원가입 | 3.종료");
            System.out.println("---------------------------------------");
            System.out.print("메뉴 선택: ");
            setNo(Ojdbc.sc.nextLine());

            switch (getNo()) {
                case "1" -> {
                    Member member = Controller.signIn();
                    if (member instanceof Administrator admin) {
                        adminMain(admin);
                    } else if (member instanceof Customer customer) {
                        customerMain(customer);
                    }
                }
                case "2" -> {
                    try {
                        Controller.signUp();
                    } catch (Exception e) {
                        System.out.println("제대로 된 값을 입력해주세요");
                        show();
                    }
                }
                case "3" -> {
                    try {
                        Ojdbc.rs.close();
                        Ojdbc.pstmt.close();
                        Ojdbc.conn.close();
                        System.exit(0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void orderHistoryMain(Customer customer){
        while(true){
            System.out.println("-----------------------------------------");
            System.out.println("1. 전체 주문 내역, 2. 월간 주문 내역 3. 나가기");
            System.out.println("-----------------------------------------");
            System.out.print("메뉴 선택 : ");
            String no = Ojdbc.sc.nextLine();

            if("1".equals(no)) {
                customer.checkTotalOrderHistory();
            } else if ("2".equals(no)){
                System.out.print("원하는 달의 주문 내역을 선택해주세요(ex: 202301) : ");
                String date = Ojdbc.sc.nextLine();
                customer.checkMonthlyOrderHistory(date);
            }else if("3".equals(no)){
                break;
            } else {
                System.out.println("유효한 메뉴를 선택해주세요");
            }
        }
    }

    public void customerMain(Customer customer) {
        boolean flag = true;
        while (flag){
            System.out.println();
            System.out.println("--------------------------------------------------------");
            System.out.println("1.상품구매");
            System.out.println("2.내역조회");
            System.out.println("3.학생 인증");
            System.out.println("4.회원 정보 수정");
            System.out.println("5.로그아웃");
            System.out.println("--------------------------------------------------------");
            System.out.print("선택: ");

            String num = Ojdbc.sc.nextLine();
            switch (num) {
                case "1":
                    customer.order();
                    break;
                case "2":
                    orderHistoryMain(customer);
                    break;
                case "3":
                    customer.authorizeStudent();
                    break;
                case "4":
                    Controller.modifyMemberInfo(customer);
                    break;
                case "5":
                    System.out.println("로그아웃 되었습니다");
                    flag = false;
                    break;
            }
        }

    }

    public void adminMain(Administrator admin) {
        boolean flag = true;
        while (flag){
            System.out.println();
            System.out.println("--------------------------------------------------------");
            System.out.println("1.상품등록");
            System.out.println("2.상품 정보 수정");
            System.out.println("3.상품 삭제");
            System.out.println("4.로그아웃");
            System.out.println("--------------------------------------------------------");
            System.out.print("선택: ");
            String num = Ojdbc.sc.nextLine();
            switch (num) {
                case "1":
                    admin.createProduct();
                    break;
                case "2":
                    admin.modifyProduct();
                    break;
                case "3":
                    admin.deleteProduct();
                    break;
                case "4":
                    System.out.println("로그아웃 되었습니다");
                    flag = false;
                    break;
            }
        }

    }

    public static Menu getInstance() {
        if (menu == null) {
            menu = new Menu();
        }
        return menu;
    }
}
