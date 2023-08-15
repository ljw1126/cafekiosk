package sample.cafekiosk.unit;

import lombok.Getter;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CafeKiosk {

    private final List<Beverage> beverages = new ArrayList<>();
    private final static LocalTime OPEN_HOUR = LocalTime.of(10, 0);
    private final static LocalTime CLOSE_HOUR = LocalTime.of(22, 0);

    public void add(Beverage beverage) {
        beverages.add(beverage);
    }

    public void add(Beverage beverage, int count) {
        if(count <= 0) throw new IllegalArgumentException("음료는 1잔 이상 주문하실 수 있습니다.");

        for(int i = 1; i <= count; i++) {
            beverages.add(beverage);
        }
    }

    public void remove(Beverage beverage) {
        beverages.remove(beverage);
    }

    public void clear() {
        beverages.clear();
    }

    public int calculateTotalPrice() {
        return beverages.stream().mapToInt(Beverage::getPrice).sum();
    }

    public Order createOrder() {
        return new Order(LocalDateTime.now(), beverages);
    }

    public Order createOrder(LocalDateTime now) {
        LocalTime orderTime = now.toLocalTime();

        if(OPEN_HOUR.isBefore(orderTime) || CLOSE_HOUR.isAfter(orderTime)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요");
        }

        return new Order(now, beverages);
    }

}
