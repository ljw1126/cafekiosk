package sample.cafekiosk.spring.api.controller.order.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateRequest {

    private List<String> productNumbers; // 상품 번호 리스트

    @Builder
    public OrderCreateRequest(List<String> productNumbers) {
        this.productNumbers = productNumbers;
    }
}
