package sample.cafekiosk.spring.domain.order;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // OrderStatisticsServiceTest 참고
    @DisplayName("주문 상태가 결제 완료인 주문을 조회한다")
    @Test
    void findOrderByTest() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.of(2023, 3, 5, 0, 0);
        LocalDate orderDate = LocalDate.of(2023, 3, 5);

        List<Product> products = List.of(
                createProduct("001", 1000),
                createProduct("002", 2000)
        );
        // 상품 정보 저장
        productRepository.saveAll(products);

        // 상품 주문
        Order order = createPaymentCompletedOrder(registeredDateTime, products);
        Order order1 = createPaymentCompletedOrder(LocalDateTime.of(2023, 3, 4, 23, 0), products);
        Order order2 = createPaymentCompletedOrder(LocalDateTime.of(2023, 3, 5, 23, 59), products);
        Order order3 = createPaymentCompletedOrder(LocalDateTime.of(2023, 3, 1, 0, 0), products);

        // when
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETED
        );

        // then
        Assertions.assertThat(orders).hasSize(2)
                .extracting("orderStatus")
                .contains(OrderStatus.PAYMENT_COMPLETED);
    }
    private Order createPaymentCompletedOrder(LocalDateTime now, List<Product> products){
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();
        return orderRepository.save(order);
    }
    private Product createProduct(String productNumber, int price) {
        return Product.builder().productNumber("001")
                .type(ProductType.HANDMADE)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("메뉴 이름")
                .build();
    }
}