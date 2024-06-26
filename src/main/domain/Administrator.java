package main.domain;

import lombok.NoArgsConstructor;
import main.Controller;
import main.Ojdbc;

import java.sql.SQLException;

@NoArgsConstructor
public class Administrator extends Member {
    public Administrator(String id, String pwd, String name, String tel, String member_type) {
        super(id, pwd, name, tel, member_type);
    }

    public void modifyProduct() {
        try {
            while (true) {
                System.out.println("--------------------------------------------------------");
                Controller.findAllProduct();
                System.out.print("수정할 상품 번호: ");
                long productId = Long.parseLong(Ojdbc.sc.nextLine());
                if (!Controller.productIsExist(productId)) {
                    System.out.println("상품이 존재하지 않습니다.");
                    break;
                } else {
                    Product product = Controller.findProduct(productId);
                    System.out.println("------------------------------------------------------------------");
                    System.out.println("1. 상품 이름 수정 | 2. 상품 가격 수정 | 3. 할인율 수정 | 4. 나가기");
                    System.out.println("------------------------------------------------------------------");
                    System.out.println();
                    System.out.print("메뉴 선택 : ");
                    String answer = Ojdbc.sc.nextLine();

                    if ("4".equals(answer)) break;
                    try {

                        switch (answer) {
                            case "1":
                                System.out.print("수정할 상품 이름: ");
                                String name = Ojdbc.sc.nextLine();
                                Ojdbc.sql = "UPDATE product SET product_name = ? WHERE product_id = ?";
                                Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                                Ojdbc.pstmt.setString(1, name);
                                break;
                            case "2":
                                System.out.print("수정할 상품 가격: ");
                                long price = Long.parseLong(Ojdbc.sc.nextLine());
                                Ojdbc.sql = "UPDATE product SET price = ? WHERE product_id = ?";
                                Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                                Ojdbc.pstmt.setLong(1, price);
                                break;
                            case "3":
                                System.out.print("수정할 할인율: ");
                                long discountRate = Long.parseLong(Ojdbc.sc.nextLine());
                                Ojdbc.sql = "UPDATE product SET discount_rate = ? WHERE product_id = ?";
                                Ojdbc.pstmt = Ojdbc.conn.prepareStatement(Ojdbc.sql);
                                Ojdbc.pstmt.setLong(1, discountRate);
                                break;
                        }
                        Ojdbc.pstmt.setLong(2, product.getProduct_id());
                        Ojdbc.pstmt.executeUpdate();
                        System.out.println("정보가 정상적으로 변경되었습니다.");
                        Ojdbc.pstmt.close();
                        break;
                    } catch (SQLException e) {
                        System.out.println("알맞은 형식의 값을 입력해주세요");
                    } catch (NumberFormatException e) {
                        System.out.println("숫자만 입력해주세요");
                    } catch (Exception e) {
                        System.out.println("정보 수정에 실패했습니다");
                    }
                }


            }
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요");
        }


    }

    public void createProduct() {
        Product product = new Product();
        try {
            System.out.println("--------------------------------------------------------");
            System.out.print("상품 이름: ");

            product.setProduct_name(Ojdbc.sc.nextLine());
            System.out.print("상품 가격: ");
            product.setPrice(Long.parseLong(Ojdbc.sc.nextLine()));
            System.out.print("할인율: ");
            product.setDiscount_rate(Long.parseLong(Ojdbc.sc.nextLine()));
            System.out.println("--------------------------------------------------------");
            String sql = "" +
                    "INSERT INTO product (product_id, product_name, price, discount_rate) " +
                    "VALUES (PRODUCT_SEQUENCE.NEXTVAL,?,?,?)";

            Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
            Ojdbc.pstmt.setString(1, product.getProduct_name());
            Ojdbc.pstmt.setLong(2, product.getPrice());
            Ojdbc.pstmt.setLong(3, product.getDiscount_rate());

            int rows = Ojdbc.pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("상품이 성공적으로 등록되었습니다");
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct() {
        try {
            Controller.findAllProduct();
            System.out.println("--------------------------------------------------------");
            System.out.print("삭제할 상품 번호: ");
            long productId = Long.parseLong(Ojdbc.sc.nextLine());
            if (!Controller.productIsExist(productId)) {
                System.out.println("상품이 존재하지 않습니다");
            } else {
                try {
                    String sql = "" +
                            "DELETE FROM product " +
                            "WHERE product_id = ?";

                    Ojdbc.pstmt = Ojdbc.conn.prepareStatement(sql);
                    Ojdbc.pstmt.setLong(1, productId);

                    System.out.print("해당 상품을 정말 삭제하시겠습니까?(Y/N): ");
                    String answer = Ojdbc.sc.nextLine();

                    if ("Y".equals(answer)) {
                        int res = Ojdbc.pstmt.executeUpdate();

                        if (res > 0) {
                            System.out.println(productId + "번 상품이 정상적으로 삭제되었습니다");
                        } else {
                            System.out.println("상품 삭제를 실패했습니다");
                        }
                    } else if ("N".equals(answer)) {
                        System.out.println("상품을 삭제하지 않습니다");
                    } else {
                        System.out.println("제대로 된 값을 입력해주세요");
                        deleteProduct();
                    }
                    Ojdbc.pstmt.close();
                } catch (SQLException e) {
                    System.out.println("상품 번호를 확인해주세요");
                } catch (NumberFormatException e) {
                    System.out.println("숫자만 입력해주세요");
                } catch (Exception e) {
                    System.out.println("제대로 된 값을 입력해주세요");
                }
            }
        }catch (NumberFormatException e){
            System.out.println("숫자를 입력해주세요");
        }

    }


}
