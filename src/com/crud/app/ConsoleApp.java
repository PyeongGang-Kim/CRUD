package com.crud.app;

import com.crud.model.Product;
import com.crud.repository.ProductRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {

    private static final String DATA_FILE = "data/products.json";
    private static final String SEP = "─".repeat(50);

    private final ProductRepository repository;
    private final Scanner scanner;

    public ConsoleApp() {
        this.repository = new ProductRepository(Path.of(DATA_FILE));
        this.scanner    = new Scanner(System.in);
    }

    public void run() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║           상품 관리 시스템 (JSON CRUD)           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("데이터 파일: " + DATA_FILE);

        while (true) {
            printMenu();
            String input = prompt("선택");
            System.out.println();

            switch (input.trim()) {
                case "1" -> listAll();
                case "2" -> findOne();
                case "3" -> create();
                case "4" -> update();
                case "5" -> delete();
                case "0" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return;
                }
                default  -> System.out.println("[오류] 올바른 메뉴 번호를 입력하세요.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // 메뉴
    // -------------------------------------------------------------------------

    private void printMenu() {
        System.out.println("\n" + SEP);
        System.out.println("  1. 전체 상품 조회");
        System.out.println("  2. 상품 검색  (ID 또는 상품명)");
        System.out.println("  3. 상품 추가");
        System.out.println("  4. 상품 수정");
        System.out.println("  5. 상품 삭제");
        System.out.println("  0. 종료");
        System.out.println(SEP);
    }

    // -------------------------------------------------------------------------
    // Read - 전체 조회
    // -------------------------------------------------------------------------

    private void listAll() {
        List<Product> list = repository.findAll();
        System.out.println("\n[ 전체 상품 목록 ]");
        System.out.println(SEP);

        if (list.isEmpty()) {
            System.out.println("  등록된 상품이 없습니다.");
        } else {
            System.out.printf("  %-6s %-20s %12s %8s%n", "ID", "상품명", "가격(원)", "수량");
            System.out.println("  " + "─".repeat(48));
            for (Product p : list) {
                System.out.printf("  %-6d %-20s %,12.0f %8d%n",
                        p.getId(), p.getName(), p.getPrice(), p.getQuantity());
            }
            System.out.println("  총 " + list.size() + "개");
        }
        System.out.println(SEP);
    }

    // -------------------------------------------------------------------------
    // Read - 검색 (ID 또는 상품명 키워드)
    // -------------------------------------------------------------------------

    private void findOne() {
        System.out.println("\n[ 상품 검색 ]");
        System.out.println(SEP);
        System.out.println("  검색 방법을 선택하세요.");
        System.out.println("  1. ID로 검색");
        System.out.println("  2. 상품명으로 검색");
        System.out.println(SEP);

        String mode = prompt("선택");
        System.out.println();

        switch (mode.trim()) {
            case "1" -> searchById();
            case "2" -> searchByName();
            default  -> System.out.println("  [오류] 올바른 번호를 입력하세요.");
        }
    }

    private void searchById() {
        long id = promptLong("조회할 상품 ID");
        Optional<Product> opt = repository.findById(id);

        System.out.println("\n[ ID 검색 결과 ]");
        System.out.println(SEP);
        if (opt.isEmpty()) {
            System.out.println("  ID " + id + "에 해당하는 상품이 없습니다.");
        } else {
            printDetail(opt.get());
        }
        System.out.println(SEP);
    }

    private void searchByName() {
        String keyword = promptString("검색할 상품명 (부분 일치)");
        List<Product> results = repository.findByName(keyword);

        System.out.println("\n[ 상품명 검색 결과: \"" + keyword + "\" ]");
        System.out.println(SEP);
        if (results.isEmpty()) {
            System.out.println("  \"" + keyword + "\"에 해당하는 상품이 없습니다.");
        } else {
            System.out.printf("  %-6s %-20s %12s %8s%n", "ID", "상품명", "가격(원)", "수량");
            System.out.println("  " + "─".repeat(48));
            for (Product p : results) {
                System.out.printf("  %-6d %-20s %,12.0f %8d%n",
                        p.getId(), p.getName(), p.getPrice(), p.getQuantity());
            }
            System.out.println("  " + results.size() + "건 검색됨");
        }
        System.out.println(SEP);
    }

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    private void create() {
        System.out.println("\n[ 상품 추가 ]");
        System.out.println(SEP);

        String name     = promptString("상품명");
        double price    = promptDouble("가격(원)");
        int    quantity = promptInt("수량");

        Product created = repository.create(name, price, quantity);

        System.out.println(SEP);
        System.out.println("  상품이 추가되었습니다.");
        printDetail(created);
        System.out.println(SEP);
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    private void update() {
        System.out.println("\n[ 상품 수정 ]");
        System.out.println(SEP);

        long id = promptLong("수정할 상품 ID");
        Optional<Product> opt = repository.findById(id);

        if (opt.isEmpty()) {
            System.out.println("  ID " + id + "에 해당하는 상품이 없습니다.");
            System.out.println(SEP);
            return;
        }

        Product current = opt.get();
        System.out.println("  현재 정보:");
        printDetail(current);
        System.out.println("  (변경하지 않으려면 Enter 를 누르세요)");
        System.out.println();

        String name     = promptWithDefault("새 상품명",    current.getName());
        double price    = promptDoubleWithDefault("새 가격(원)", current.getPrice());
        int    quantity = promptIntWithDefault("새 수량",    current.getQuantity());

        repository.update(id, name, price, quantity);

        System.out.println(SEP);
        System.out.println("  상품이 수정되었습니다.");
        printDetail(repository.findById(id).get());
        System.out.println(SEP);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    private void delete() {
        System.out.println("\n[ 상품 삭제 ]");
        System.out.println(SEP);

        long id = promptLong("삭제할 상품 ID");
        Optional<Product> opt = repository.findById(id);

        if (opt.isEmpty()) {
            System.out.println("  ID " + id + "에 해당하는 상품이 없습니다.");
            System.out.println(SEP);
            return;
        }

        printDetail(opt.get());
        String confirm = prompt("정말 삭제하시겠습니까? (y/N)");

        if (confirm.trim().equalsIgnoreCase("y")) {
            repository.deleteById(id);
            System.out.println("  상품이 삭제되었습니다.");
        } else {
            System.out.println("  삭제가 취소되었습니다.");
        }
        System.out.println(SEP);
    }

    // -------------------------------------------------------------------------
    // 출력 헬퍼
    // -------------------------------------------------------------------------

    private void printDetail(Product p) {
        System.out.printf("  ID       : %d%n",      p.getId());
        System.out.printf("  상품명   : %s%n",      p.getName());
        System.out.printf("  가격     : %,.0f 원%n", p.getPrice());
        System.out.printf("  수량     : %d 개%n",   p.getQuantity());
    }

    // -------------------------------------------------------------------------
    // 입력 헬퍼
    // -------------------------------------------------------------------------

    private String prompt(String label) {
        System.out.print("  " + label + " > ");
        return scanner.nextLine();
    }

    private String promptString(String label) {
        while (true) {
            String val = prompt(label);
            if (!val.isBlank()) return val.trim();
            System.out.println("  [오류] 값을 입력해주세요.");
        }
    }

    private long promptLong(String label) {
        while (true) {
            try {
                return Long.parseLong(prompt(label).trim());
            } catch (NumberFormatException e) {
                System.out.println("  [오류] 숫자를 입력해주세요.");
            }
        }
    }

    private double promptDouble(String label) {
        while (true) {
            try {
                double val = Double.parseDouble(prompt(label).trim());
                if (val < 0) { System.out.println("  [오류] 0 이상의 값을 입력해주세요."); continue; }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [오류] 숫자를 입력해주세요.");
            }
        }
    }

    private int promptInt(String label) {
        while (true) {
            try {
                int val = Integer.parseInt(prompt(label).trim());
                if (val < 0) { System.out.println("  [오류] 0 이상의 값을 입력해주세요."); continue; }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [오류] 숫자를 입력해주세요.");
            }
        }
    }

    private String promptWithDefault(String label, String defaultVal) {
        String val = prompt(label + " [" + defaultVal + "]");
        return val.isBlank() ? defaultVal : val.trim();
    }

    private double promptDoubleWithDefault(String label, double defaultVal) {
        while (true) {
            String input = prompt(label + " [" + String.format("%.0f", defaultVal) + "]");
            if (input.isBlank()) return defaultVal;
            try {
                double val = Double.parseDouble(input.trim());
                if (val < 0) { System.out.println("  [오류] 0 이상의 값을 입력해주세요."); continue; }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [오류] 숫자를 입력해주세요.");
            }
        }
    }

    private int promptIntWithDefault(String label, int defaultVal) {
        while (true) {
            String input = prompt(label + " [" + defaultVal + "]");
            if (input.isBlank()) return defaultVal;
            try {
                int val = Integer.parseInt(input.trim());
                if (val < 0) { System.out.println("  [오류] 0 이상의 값을 입력해주세요."); continue; }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  [오류] 숫자를 입력해주세요.");
            }
        }
    }
}
