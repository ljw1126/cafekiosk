package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.domain.product.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static sample.cafekiosk.spring.domain.order.OrderStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class OrderTest {
    @DisplayName("주문 생성시 상품 리스트에서 주문의 총 금액을 계산한다.")
    @Test
    void calculateTotalPrice() {
        //given 주문 생성
        List<Product> products = List.of(createProduct("001", 1000), createProduct("001", 2000));

        //when
        Order order = Order.create(products, LocalDateTime.now());

        //then
        assertThat(order.getTotalPrice()).isEqualTo(3000);
    }

    @DisplayName("주문 생성시 상품 리스트에서 주문 상태는 INIT이다")
    @Test
    void init() {
        //given 주문 생성
        List<Product> products = List.of(createProduct("001", 1000), createProduct("001", 2000));

        //when
        Order order = Order.create(products, LocalDateTime.now());

        //then
        assertThat(order.getOrderStatus()).isEqualByComparingTo(INIT);
    }

    @DisplayName("주문 생성시 주문 등록 시간을 기록한다")
    @Test
    void registeredDateTime() {
        //given
        LocalDateTime registeredDataTime = LocalDateTime.now();
        List<Product> products = List.of(createProduct("001", 1000), createProduct("001", 2000));

        //when
        Order order = Order.create(products, registeredDataTime);

        //then
        assertThat(order.getRegisteredTime()).isEqualTo(registeredDataTime);
    }

    private static Product createProduct(String productNumber, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("메뉴")
                .price(price)
                .build();
    }
}