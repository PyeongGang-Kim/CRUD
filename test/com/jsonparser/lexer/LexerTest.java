package com.jsonparser.lexer;

import com.jsonparser.exception.JsonParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Lexer 토크나이저 테스트")
class LexerTest {

    private List<Token> tokenize(String input) {
        return new Lexer(input).tokenize();
    }

    // ── 구조 토큰 ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("중괄호 토큰 인식")
    void braces() {
        List<Token> tokens = tokenize("{}");
        assertEquals(TokenType.LEFT_BRACE,  tokens.get(0).type());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(1).type());
        assertEquals(TokenType.EOF,         tokens.get(2).type());
    }

    @Test
    @DisplayName("대괄호 토큰 인식")
    void brackets() {
        List<Token> tokens = tokenize("[]");
        assertEquals(TokenType.LEFT_BRACKET,  tokens.get(0).type());
        assertEquals(TokenType.RIGHT_BRACKET, tokens.get(1).type());
    }

    @Test
    @DisplayName("콜론과 쉼표 토큰 인식")
    void colonAndComma() {
        List<Token> tokens = tokenize(":,");
        assertEquals(TokenType.COLON, tokens.get(0).type());
        assertEquals(TokenType.COMMA, tokens.get(1).type());
    }

    @Test
    @DisplayName("빈 입력 → EOF만 반환")
    void emptyInput() {
        List<Token> tokens = tokenize("");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).type());
    }

    @Test
    @DisplayName("공백, 탭, 개행 무시")
    void whitespaceIgnored() {
        List<Token> tokens = tokenize("  \t\n  true  ");
        assertEquals(TokenType.TRUE, tokens.get(0).type());
        assertEquals(TokenType.EOF,  tokens.get(1).type());
    }

    // ── 문자열 ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("기본 문자열 토큰")
    void simpleString() {
        Token t = tokenize("\"hello\"").get(0);
        assertEquals(TokenType.STRING, t.type());
        assertEquals("hello", t.value());
    }

    @Test
    @DisplayName("빈 문자열 토큰")
    void emptyString() {
        Token t = tokenize("\"\"").get(0);
        assertEquals(TokenType.STRING, t.type());
        assertEquals("", t.value());
    }

    @Test
    @DisplayName("escape: 큰따옴표 \\\"")
    void escapeQuote() {
        assertEquals("say \"hi\"", tokenize("\"say \\\"hi\\\"\"").get(0).value());
    }

    @Test
    @DisplayName("escape: 백슬래시 \\\\")
    void escapeBackslash() {
        assertEquals("a\\b", tokenize("\"a\\\\b\"").get(0).value());
    }

    @Test
    @DisplayName("escape: 슬래시 \\/")
    void escapeSlash() {
        assertEquals("a/b", tokenize("\"a\\/b\"").get(0).value());
    }

    @Test
    @DisplayName("escape: 특수문자 \\b \\f \\n \\r \\t")
    void escapeSpecials() {
        assertEquals("\b\f\n\r\t", tokenize("\"\\b\\f\\n\\r\\t\"").get(0).value());
    }

    @Test
    @DisplayName("escape: 유니코드 \\uXXXX")
    void escapeUnicode() {
        assertEquals("A", tokenize("\"\\u0041\"").get(0).value());
    }

    @Test
    @DisplayName("한글 유니코드 escape \\uAC00")
    void escapeKorean() {
        assertEquals("가", tokenize("\"\\uAC00\"").get(0).value());
    }

    @Test
    @DisplayName("미종료 문자열 → JsonParseException")
    void unterminatedString() {
        assertThrows(JsonParseException.class, () -> tokenize("\"abc"));
    }

    @Test
    @DisplayName("잘못된 escape sequence → JsonParseException")
    void invalidEscapeSequence() {
        assertThrows(JsonParseException.class, () -> tokenize("\"\\q\""));
    }

    @Test
    @DisplayName("제어 문자 비이스케이프 → JsonParseException")
    void unescapedControlChar() {
        assertThrows(JsonParseException.class, () -> tokenize("\"\""));
    }

    @Test
    @DisplayName("불완전한 유니코드 → JsonParseException")
    void incompleteUnicode() {
        assertThrows(JsonParseException.class, () -> tokenize("\"\\u00\""));
    }

    @Test
    @DisplayName("잘못된 유니코드 hex → JsonParseException")
    void invalidUnicodeHex() {
        assertThrows(JsonParseException.class, () -> tokenize("\"\\uXXXX\""));
    }

    // ── 숫자 ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("정수 토큰")
    void integerNumber() {
        Token t = tokenize("42").get(0);
        assertEquals(TokenType.NUMBER, t.type());
        assertEquals("42", t.value());
    }

    @Test
    @DisplayName("0 토큰")
    void zeroNumber() {
        assertEquals("0", tokenize("0").get(0).value());
    }

    @Test
    @DisplayName("음수 토큰")
    void negativeNumber() {
        assertEquals("-7", tokenize("-7").get(0).value());
    }

    @Test
    @DisplayName("소수 토큰")
    void decimalNumber() {
        assertEquals("3.14", tokenize("3.14").get(0).value());
    }

    @Test
    @DisplayName("지수 토큰 (소문자 e)")
    void exponentLower() {
        assertEquals("1e10", tokenize("1e10").get(0).value());
    }

    @Test
    @DisplayName("지수 토큰 (대문자 E, 양수 부호)")
    void exponentUpperPositive() {
        assertEquals("1E+3", tokenize("1E+3").get(0).value());
    }

    @Test
    @DisplayName("지수 토큰 (음수 부호)")
    void exponentNegative() {
        assertEquals("2.5E-3", tokenize("2.5E-3").get(0).value());
    }

    @Test
    @DisplayName("소수점 뒤 숫자 없음 → JsonParseException")
    void numberMissingFractionalDigit() {
        assertThrows(JsonParseException.class, () -> tokenize("1."));
    }

    @Test
    @DisplayName("지수 뒤 숫자 없음 → JsonParseException")
    void numberMissingExponentDigit() {
        assertThrows(JsonParseException.class, () -> tokenize("1e"));
    }

    @Test
    @DisplayName("단독 마이너스 → JsonParseException")
    void loneMinus() {
        assertThrows(JsonParseException.class, () -> tokenize("-"));
    }

    // ── 키워드 ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("true 토큰")
    void trueKeyword() {
        assertEquals(TokenType.TRUE, tokenize("true").get(0).type());
    }

    @Test
    @DisplayName("false 토큰")
    void falseKeyword() {
        assertEquals(TokenType.FALSE, tokenize("false").get(0).type());
    }

    @Test
    @DisplayName("null 토큰")
    void nullKeyword() {
        assertEquals(TokenType.NULL, tokenize("null").get(0).type());
    }

    @Test
    @DisplayName("키워드 접미사 포함 (nulll) → JsonParseException")
    void keywordWithSuffix() {
        assertThrows(JsonParseException.class, () -> tokenize("nulll"));
    }

    @Test
    @DisplayName("키워드 접미사 포함 (truefalse) → JsonParseException")
    void keywordCombined() {
        assertThrows(JsonParseException.class, () -> tokenize("truefalse"));
    }

    @Test
    @DisplayName("예상치 못한 문자 → JsonParseException")
    void unexpectedChar() {
        assertThrows(JsonParseException.class, () -> tokenize("@"));
    }

    // ── 복합 입력 ────────────────────────────────────────────────────────

    @Test
    @DisplayName("객체 토큰 스트림 순서")
    void objectTokenStream() {
        List<Token> tokens = tokenize("{\"k\":1}");
        assertEquals(TokenType.LEFT_BRACE,  tokens.get(0).type());
        assertEquals(TokenType.STRING,      tokens.get(1).type());
        assertEquals("k",                   tokens.get(1).value());
        assertEquals(TokenType.COLON,       tokens.get(2).type());
        assertEquals(TokenType.NUMBER,      tokens.get(3).type());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(4).type());
        assertEquals(TokenType.EOF,         tokens.get(5).type());
    }

    @Test
    @DisplayName("배열 토큰 스트림 순서")
    void arrayTokenStream() {
        List<Token> tokens = tokenize("[1,2]");
        assertEquals(TokenType.LEFT_BRACKET,  tokens.get(0).type());
        assertEquals(TokenType.NUMBER,        tokens.get(1).type());
        assertEquals(TokenType.COMMA,         tokens.get(2).type());
        assertEquals(TokenType.NUMBER,        tokens.get(3).type());
        assertEquals(TokenType.RIGHT_BRACKET, tokens.get(4).type());
    }
}
