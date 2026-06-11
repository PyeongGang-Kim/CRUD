package com.crud.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product 모델 테스트")
class ProductTest {

    @Test
    @DisplayName("기본 생성자 및 setter/getter")
    void defaultConstructorAndSetters() {
        Product p = new Product();
        p.setId(1L);
        p.setName("노트북");
        p.setPrice(1200000.0);
        p.setQuantity(5);

        assertEquals(1L,         p.getId());
        assertEquals("노트북",   p.getName());
        assertEquals(1200000.0,  p.getPrice(), 1e-9);
        assertEquals(5,          p.getQuantity());
    }

    @Test
    @DisplayName("매개변수 생성자")
    void allArgsConstructor() {
        Product p = new Product(2L, "마우스", 30000.0, 10);
        assertEquals(2L,       p.getId());
        assertEquals("마우스", p.getName());
        assertEquals(30000.0,  p.getPrice(), 1e-9);
        assertEquals(10,       p.getQuantity());
    }

    @Test
    @DisplayName("setName 변경 반영")
    void setterName() {
        Product p = new Product(1L, "구형 키보드", 50000.0, 1);
        p.setName("신형 키보드");
        assertEquals("신형 키보드", p.getName());
    }

    @Test
    @DisplayName("setPrice 변경 반영")
    void setterPrice() {
        Product p = new Product(1L, "모니터", 300000.0, 2);
        p.setPrice(350000.0);
        assertEquals(350000.0, p.getPrice(), 1e-9);
    }

    @Test
    @DisplayName("setQuantity 변경 반영")
    void setterQuantity() {
        Product p = new Product(1L, "웹캠", 80000.0, 3);
        p.setQuantity(10);
        assertEquals(10, p.getQuantity());
    }

    @Test
    @DisplayName("setId 변경 반영")
    void setterId() {
        Product p = new Product();
        p.setId(99L);
        assertEquals(99L, p.getId());
    }

    @Test
    @DisplayName("toString 형식: 모든 필드 포함")
    void toStringContainsAllFields() {
        Product p = new Product(1L, "키보드", 80000.0, 3);
        String s = p.toString();
        assertTrue(s.contains("id=1"));
        assertTrue(s.contains("name='키보드'"));
        assertTrue(s.contains("price=80000.00"));
        assertTrue(s.contains("quantity=3"));
    }

    @Test
    @DisplayName("toString 형식: price 소수점 2자리")
    void toStringPriceFormat() {
        Product p = new Product(1L, "USB", 9999.9, 1);
        assertTrue(p.toString().contains("price=9999.90"));
    }

    @Test
    @DisplayName("가격 0 설정 허용")
    void priceZero() {
        Product p = new Product(1L, "무료 샘플", 0.0, 100);
        assertEquals(0.0, p.getPrice(), 1e-9);
    }

    @Test
    @DisplayName("수량 0 설정 허용")
    void quantityZero() {
        Product p = new Product(1L, "품절 상품", 10000.0, 0);
        assertEquals(0, p.getQuantity());
    }
}
