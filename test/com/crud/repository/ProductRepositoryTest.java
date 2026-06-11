package com.crud.repository;

import com.crud.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductRepository CRUD 테스트")
class ProductRepositoryTest {

    @TempDir
    Path tempDir;

    private ProductRepository repo;

    @BeforeEach
    void setUp() {
        repo = new ProductRepository(tempDir.resolve("products.json"));
    }

    // ── create ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("상품 생성: ID 자동 부여 및 필드 검증")
    void createProduct() {
        Product p = repo.create("노트북", 1200000.0, 5);

        assertEquals(1L,         p.getId());
        assertEquals("노트북",   p.getName());
        assertEquals(1200000.0,  p.getPrice(), 1e-9);
        assertEquals(5,          p.getQuantity());
    }

    @Test
    @DisplayName("복수 상품 생성 시 ID 순차 증가")
    void createMultipleProductsHaveSequentialIds() {
        Product p1 = repo.create("A", 1000.0, 1);
        Product p2 = repo.create("B", 2000.0, 2);
        Product p3 = repo.create("C", 3000.0, 3);

        assertEquals(1L, p1.getId());
        assertEquals(2L, p2.getId());
        assertEquals(3L, p3.getId());
    }

    @Test
    @DisplayName("생성된 상품이 findAll 목록에 포함")
    void createAppearsInFindAll() {
        repo.create("노트북", 1200000.0, 5);
        repo.create("마우스", 30000.0, 10);

        assertEquals(2, repo.findAll().size());
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 저장소 findAll → 빈 리스트")
    void findAllEmpty() {
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("findAll 반환 리스트는 불변")
    void findAllIsImmutable() {
        repo.create("A", 100.0, 1);
        List<Product> list = repo.findAll();
        assertThrows(UnsupportedOperationException.class, () -> list.remove(0));
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("존재하는 ID 조회 → 상품 반환")
    void findByIdFound() {
        Product created = repo.create("노트북", 1200000.0, 5);

        Optional<Product> found = repo.findById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("노트북", found.get().getName());
        assertEquals(1200000.0, found.get().getPrice(), 1e-9);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 → empty Optional")
    void findByIdNotFound() {
        assertTrue(repo.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("복수 상품 중 특정 ID 조회")
    void findByIdAmongMultiple() {
        repo.create("A", 100.0, 1);
        Product target = repo.create("B", 200.0, 2);
        repo.create("C", 300.0, 3);

        Optional<Product> found = repo.findById(target.getId());

        assertTrue(found.isPresent());
        assertEquals("B", found.get().getName());
    }

    // ── findByName ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("이름 키워드 검색: 부분 일치")
    void findByNamePartialMatch() {
        repo.create("무선 마우스", 30000.0, 10);
        repo.create("유선 마우스", 20000.0, 5);
        repo.create("키보드", 80000.0, 3);

        List<Product> result = repo.findByName("마우스");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("이름 키워드 검색: 대소문자 무시")
    void findByNameCaseInsensitive() {
        repo.create("USB Hub", 15000.0, 7);
        repo.create("HDMI 케이블", 5000.0, 15);

        List<Product> result = repo.findByName("usb");

        assertEquals(1, result.size());
        assertEquals("USB Hub", result.get(0).getName());
    }

    @Test
    @DisplayName("이름 키워드 검색: 결과 없음 → 빈 리스트")
    void findByNameNoMatch() {
        repo.create("노트북", 1200000.0, 5);

        assertTrue(repo.findByName("태블릿").isEmpty());
    }

    @Test
    @DisplayName("이름 키워드 검색: 전체 이름 일치")
    void findByNameExactMatch() {
        repo.create("노트북", 1200000.0, 5);
        repo.create("노트북 거치대", 25000.0, 3);

        List<Product> result = repo.findByName("노트북");

        assertEquals(2, result.size());
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("상품 수정 성공: true 반환 및 값 갱신")
    void updateSuccess() {
        Product p = repo.create("구형 노트북", 800000.0, 2);

        boolean updated = repo.update(p.getId(), "신형 노트북", 1500000.0, 3);

        assertTrue(updated);
        Product found = repo.findById(p.getId()).orElseThrow();
        assertEquals("신형 노트북", found.getName());
        assertEquals(1500000.0, found.getPrice(), 1e-9);
        assertEquals(3, found.getQuantity());
    }

    @Test
    @DisplayName("존재하지 않는 ID 수정 → false 반환")
    void updateNotFound() {
        assertFalse(repo.update(999L, "없는상품", 0.0, 0));
    }

    @Test
    @DisplayName("수정 후 다른 상품은 변경 없음")
    void updateOnlyAffectsTarget() {
        Product p1 = repo.create("A", 100.0, 1);
        Product p2 = repo.create("B", 200.0, 2);

        repo.update(p1.getId(), "A-Updated", 150.0, 5);

        Product unchanged = repo.findById(p2.getId()).orElseThrow();
        assertEquals("B", unchanged.getName());
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("상품 삭제 성공: true 반환 및 목록에서 제거")
    void deleteSuccess() {
        Product p = repo.create("삭제될 상품", 1000.0, 1);

        assertTrue(repo.deleteById(p.getId()));
        assertTrue(repo.findById(p.getId()).isEmpty());
        assertEquals(0, repo.findAll().size());
    }

    @Test
    @DisplayName("존재하지 않는 ID 삭제 → false 반환")
    void deleteNotFound() {
        assertFalse(repo.deleteById(999L));
    }

    @Test
    @DisplayName("삭제 후 남은 상품 개수 감소")
    void deleteReducesCount() {
        repo.create("A", 100.0, 1);
        Product toDelete = repo.create("B", 200.0, 2);
        repo.create("C", 300.0, 3);

        repo.deleteById(toDelete.getId());

        assertEquals(2, repo.findAll().size());
    }

    @Test
    @DisplayName("삭제 후 다른 상품은 유지")
    void deleteOnlyRemovesTarget() {
        repo.create("A", 100.0, 1);
        Product toDelete = repo.create("B", 200.0, 2);
        repo.create("C", 300.0, 3);

        repo.deleteById(toDelete.getId());

        List<Product> remaining = repo.findAll();
        assertTrue(remaining.stream().noneMatch(p -> p.getId() == toDelete.getId()));
        assertEquals(2, remaining.size());
    }

    // ── 파일 영속성 ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("저장 후 재로드 시 데이터 유지")
    void persistenceAfterReload() {
        repo.create("노트북", 1200000.0, 5);
        repo.create("마우스", 30000.0, 10);

        ProductRepository reloaded = new ProductRepository(tempDir.resolve("products.json"));
        List<Product> products = reloaded.findAll();

        assertEquals(2, products.size());
        assertEquals("노트북", products.get(0).getName());
        assertEquals(1200000.0, products.get(0).getPrice(), 1e-9);
        assertEquals(5,         products.get(0).getQuantity());
        assertEquals("마우스",  products.get(1).getName());
    }

    @Test
    @DisplayName("재로드 후 ID 자동 증가 연속")
    void idContinuesAfterReload() {
        repo.create("A", 100.0, 1);
        repo.create("B", 200.0, 2);

        ProductRepository reloaded = new ProductRepository(tempDir.resolve("products.json"));
        Product c = reloaded.create("C", 300.0, 3);

        assertEquals(3L, c.getId());
    }

    @Test
    @DisplayName("수정 후 재로드 시 변경 내용 반영")
    void updatePersistsAfterReload() {
        Product p = repo.create("구형", 100.0, 1);
        repo.update(p.getId(), "신형", 200.0, 2);

        ProductRepository reloaded = new ProductRepository(tempDir.resolve("products.json"));
        Product found = reloaded.findById(p.getId()).orElseThrow();

        assertEquals("신형", found.getName());
        assertEquals(200.0, found.getPrice(), 1e-9);
        assertEquals(2, found.getQuantity());
    }

    @Test
    @DisplayName("삭제 후 재로드 시 삭제 내용 반영")
    void deletePersistsAfterReload() {
        Product p1 = repo.create("A", 100.0, 1);
        repo.create("B", 200.0, 2);
        repo.deleteById(p1.getId());

        ProductRepository reloaded = new ProductRepository(tempDir.resolve("products.json"));
        List<Product> products = reloaded.findAll();

        assertEquals(1, products.size());
        assertEquals("B", products.get(0).getName());
    }

    @Test
    @DisplayName("존재하지 않는 파일로 초기화 → 빈 저장소")
    void nonExistentFileStartsEmpty() {
        ProductRepository fresh = new ProductRepository(
                tempDir.resolve("nonexistent.json"));
        assertTrue(fresh.findAll().isEmpty());
    }
}
