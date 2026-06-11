package com.jsonparser.parser;

import com.jsonparser.exception.JsonParseException;
import com.jsonparser.lexer.Lexer;
import com.jsonparser.lexer.Token;
import com.jsonparser.lexer.TokenType;
import com.jsonparser.value.*;

import java.math.BigDecimal;
import java.util.List;

public class JsonParser {

    private List<Token> tokens;
    private int pos;

    /**
     * JSON 문자열을 파싱해 JsonValue 트리를 반환한다.
     */
    public JsonValue parse(String json) {
        if (json == null || json.isBlank()) {
            throw new JsonParseException("Input JSON must not be null or blank");
        }

        this.tokens = new Lexer(json).tokenize();
        this.pos = 0;

        JsonValue value = parseValue();

        if (current().type() != TokenType.EOF) {
            throw new JsonParseException(
                "Unexpected token after root value at position " + current().position() + ": " + current()
            );
        }

        return value;
    }

    // -------------------------------------------------------------------------
    // 내부 파싱 메서드
    // -------------------------------------------------------------------------

    private JsonValue parseValue() {
        Token token = current();
        return switch (token.type()) {
            case LEFT_BRACE   -> parseObject();
            case LEFT_BRACKET -> parseArray();
            case STRING  -> { advance(); yield new JsonString(token.value()); }
            case NUMBER  -> { advance(); yield parseNumber(token); }
            case TRUE    -> { advance(); yield JsonBoolean.TRUE; }
            case FALSE   -> { advance(); yield JsonBoolean.FALSE; }
            case NULL    -> { advance(); yield JsonNull.INSTANCE; }
            default -> throw new JsonParseException(
                "Unexpected token at position " + token.position() + ": " + token
            );
        };
    }

    private JsonObject parseObject() {
        consume(TokenType.LEFT_BRACE);
        JsonObject obj = new JsonObject();

        if (current().type() == TokenType.RIGHT_BRACE) {
            advance();
            return obj;
        }

        while (true) {
            Token keyToken = consume(TokenType.STRING);
            consume(TokenType.COLON);
            JsonValue value = parseValue();
            obj.put(keyToken.value(), value);

            TokenType next = current().type();
            if (next == TokenType.RIGHT_BRACE) {
                advance();
                break;
            }
            if (next != TokenType.COMMA) {
                throw new JsonParseException(
                    "Expected ',' or '}' in object at position " + current().position()
                );
            }
            advance(); // consume comma

            // trailing comma 금지 (RFC 8259)
            if (current().type() == TokenType.RIGHT_BRACE) {
                throw new JsonParseException(
                    "Trailing comma in object at position " + current().position()
                );
            }
        }

        return obj;
    }

    private JsonArray parseArray() {
        consume(TokenType.LEFT_BRACKET);
        JsonArray arr = new JsonArray();

        if (current().type() == TokenType.RIGHT_BRACKET) {
            advance();
            return arr;
        }

        while (true) {
            arr.add(parseValue());

            TokenType next = current().type();
            if (next == TokenType.RIGHT_BRACKET) {
                advance();
                break;
            }
            if (next != TokenType.COMMA) {
                throw new JsonParseException(
                    "Expected ',' or ']' in array at position " + current().position()
                );
            }
            advance(); // consume comma

            // trailing comma 금지 (RFC 8259)
            if (current().type() == TokenType.RIGHT_BRACKET) {
                throw new JsonParseException(
                    "Trailing comma in array at position " + current().position()
                );
            }
        }

        return arr;
    }

    private JsonNumber parseNumber(Token token) {
        try {
            return new JsonNumber(new BigDecimal(token.value()));
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid number literal '" + token.value() + "'");
        }
    }

    // -------------------------------------------------------------------------
    // 토큰 스트림 유틸리티
    // -------------------------------------------------------------------------

    private Token current() {
        return tokens.get(pos);
    }

    private Token advance() {
        return tokens.get(pos++);
    }

    private Token consume(TokenType expected) {
        Token token = current();
        if (token.type() != expected) {
            throw new JsonParseException(
                "Expected " + expected + " but got " + token.type()
                    + " at position " + token.position()
            );
        }
        return advance();
    }
}
