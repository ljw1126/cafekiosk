package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final StockRepository stockRepository;

    public OrderResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers  = request.getProductNumbers();
        List<Product> duplicateProducts = findProductBy(productNumbers);

        deductStockQuantities(duplicateProducts);

        // Order
        Order order = Order.create(duplicateProducts, registeredDateTime);
        Order saveOrder = orderRepository.save(order); // 저장 후 pk id

        return OrderResponse.of(saveOrder);
    }


    /**
     * Stock Entity 추가 이후 수정 필요
     * 1) 재고 차감 체크가 필요한 상품들 filter
     * 2) 재고 엔티티 조회
     * 3) 상품별 counting
     * 4) 재고 차감 시도
     */
    private void deductStockQuantities(List<Product> products) {
        // 1)
        List<String> stockProductNumbers = extractStockProductNumbers(products);

        // 2) 재고 엔티티 조회
        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);

        // 3) 상품별 카운팅 {"productNumber" : 0, ..}
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        // 4) 재고 차감 시도(stockProductNumber 중복 제거)
        for(String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if(stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다");
            }

            stock.deductQuantity(quantity);
        }
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }

    private static List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
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
