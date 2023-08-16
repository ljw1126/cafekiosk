package sample.cafekiosk.spring.api.service.order;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.controller.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;

@ActiveProfiles("test")
@SpringBootTest
//@DataJpaTest
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다")
    @Test
    void createOrder() {
        //given
        Product product1 = createProduct("001", ProductType.HANDMADE, 1000);
        Product product2 = createProduct("002", ProductType.HANDMADE, 3000);
        Product product3 = createProduct("003", ProductType.HANDMADE, 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder().productNumbers(List.of("001", "002")).build();

        LocalDateTime registeredDateTime = LocalDateTime.now();

        //when
        OrderResponse response = orderService.createOrder(request, registeredDateTime);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 4000);

        assertThat(response.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("001", 1000),
                        Tuple.tuple("002", 3000)
                );
    }

    private static Product createProduct(String productNumber, ProductType type, int price) {
        return Product.builder()
                       .productNumber(productNumber)
                       .type(type)
                       .sellingStatus(SELLING)
                       .name("메뉴")
                       .price(price)
                       .build();
    }
}