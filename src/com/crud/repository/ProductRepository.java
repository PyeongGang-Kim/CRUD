package com.crud.repository;

import com.crud.model.Product;
import com.jsonparser.Json;
import com.jsonparser.value.JsonArray;
import com.jsonparser.value.JsonObject;
import com.jsonparser.value.JsonValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    private final Path       dataFile;
    private final List<Product> products;
    private long nextId;

    public ProductRepository(Path dataFile) {
        this.dataFile = dataFile;
        this.products = new ArrayList<>();
        this.nextId   = 1;
        load();
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    public List<Product> findAll() {
        return Collections.unmodifiableList(products);
    }

    public Optional<Product> findById(long id) {
        return products.stream().filter(p -> p.getId() == id).findFirst();
    }

    /** 상품명에 keyword가 포함된 상품 목록을 반환한다 (대소문자 무시). */
    public List<Product> findByName(String keyword) {
        String lower = keyword.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lower))
                .toList();
    }

    /** 새 상품을 추가하고 저장한다. */
    public Product create(String name, double price, int quantity) {
        Product p = new Product(nextId++, name, price, quantity);
        products.add(p);
        save();
        return p;
    }

    /** 기존 상품을 수정하고 저장한다. id가 없으면 false를 반환한다. */
    public boolean update(long id, String name, double price, int quantity) {
        Optional<Product> opt = findById(id);
        if (opt.isEmpty()) return false;

        Product p = opt.get();
        p.setName(name);
        p.setPrice(price);
        p.setQuantity(quantity);
        save();
        return true;
    }

    /** 상품을 삭제하고 저장한다. id가 없으면 false를 반환한다. */
    public boolean deleteById(long id) {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) save();
        return removed;
    }

    // -------------------------------------------------------------------------
    // 파일 I/O (JsonParser 라이브러리 사용)
    // -------------------------------------------------------------------------

    private void load() {
        if (!Files.exists(dataFile)) return;

        try {
            JsonValue root = Json.parseFile(dataFile);
            if (!root.isArray()) return;

            JsonArray arr = root.asArray();
            long maxId = 0;
            for (JsonValue item : arr) {
                if (!item.isObject()) continue;
                JsonObject obj = item.asObject();

                long   id  = obj.get("id").asNumber().longValue();
                String nm  = obj.get("name").asString().getValue();
                double pr  = obj.get("price").asNumber().doubleValue();
                int    qty = obj.get("quantity").asNumber().intValue();

                products.add(new Product(id, nm, pr, qty));
                if (id > maxId) maxId = id;
            }
            nextId = maxId + 1;

        } catch (IOException e) {
            System.err.println("[경고] 데이터 파일 로드 실패: " + e.getMessage());
        }
    }

    private void save() {
        try {
            Files.createDirectories(dataFile.getParent());

            JsonArray arr = Json.array();
            for (Product p : products) {
                JsonObject obj = Json.object();
                obj.put("id",       Json.of(p.getId()));
                obj.put("name",     Json.of(p.getName()));
                obj.put("price",    Json.of(p.getPrice()));
                obj.put("quantity", Json.of((long) p.getQuantity()));
                arr.add(obj);
            }
            Json.saveToFile(arr, dataFile);

        } catch (IOException e) {
            System.err.println("[오류] 데이터 파일 저장 실패: " + e.getMessage());
        }
    }
}
