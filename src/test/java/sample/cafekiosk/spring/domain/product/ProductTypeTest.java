package sample.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTypeTest {

    @DisplayName("상품 타입이 재고 관련 타입인지 체크 한다")
    @Test
    void containsStockType() {
        //given
        ProductType productType = ProductType.HANDMADE;

        //when
        boolean result = ProductType.containsStockType(productType);

        //then
        Assertions.assertThat(result).isFalse();
    }
}