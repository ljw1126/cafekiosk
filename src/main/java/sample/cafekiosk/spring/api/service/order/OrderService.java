package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers  = request.getProductNumbers();

        List<Product> duplicateProducts = findProductBy(productNumbers);

        // Order
        Order order = Order.create(duplicateProducts, registeredDateTime);
        Order saveOrder = orderRepository.save(order); // 저장 후 pk id

        return OrderResponse.of(saveOrder);
    }

    private List<Product> findProductBy(List<String> productNumbers) {
        // Product
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

        // 중복 상품에 대한 처리 (001 아메리카노 1잔인 경우)
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, p -> p)); // { "001" : 상품Object }

        List<Product> duplicateProducts = productNumbers.stream().map(productMap::get)
                .collect(Collectors.toList());

        return duplicateProducts;
    }
}
