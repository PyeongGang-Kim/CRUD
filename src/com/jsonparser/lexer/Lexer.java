package com.jsonparser.lexer;

import com.jsonparser.exception.JsonParseException;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String source;
    private int pos;

    public Lexer(String source) {
        this.source = source;
        this.pos = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < source.length()) {
            skipWhitespace();
            if (pos >= source.length()) break;
            tokens.add(nextToken());
        }
        tokens.add(new Token(TokenType.EOF, "", pos));
        return tokens;
    }

    private void skipWhitespace() {
        while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
            pos++;
        }
    }

    private Token nextToken() {
        int start = pos;
        char c = source.charAt(pos);

        return switch (c) {
            case '{' -> { pos++; yield new Token(TokenType.LEFT_BRACE,    "{", start); }
            case '}' -> { pos++; yield new Token(TokenType.RIGHT_BRACE,   "}", start); }
            case '[' -> { pos++; yield new Token(TokenType.LEFT_BRACKET,  "[", start); }
            case ']' -> { pos++; yield new Token(TokenType.RIGHT_BRACKET, "]", start); }
            case ':' -> { pos++; yield new Token(TokenType.COLON,         ":", start); }
            case ',' -> { pos++; yield new Token(TokenType.COMMA,         ",", start); }
            case '"' -> readString();
            case 't' -> readKeyword("true",  TokenType.TRUE);
            case 'f' -> readKeyword("false", TokenType.FALSE);
            case 'n' -> readKeyword("null",  TokenType.NULL);
            default  -> {
                if (c == '-' || Character.isDigit(c)) yield readNumber();
                throw new JsonParseException(
                    "Unexpected character '" + c + "' at position " + pos
                );
            }
        };
    }

    private Token readString() {
        int start = pos;
        pos++; // opening "
        StringBuilder sb = new StringBuilder();

        while (pos < source.length() && source.charAt(pos) != '"') {
            char c = source.charAt(pos);

            if (c == '\\') {
                pos++;
                if (pos >= source.length()) {
                    throw new JsonParseException("Unexpected end in escape sequence");
                }
                char esc = source.charAt(pos);
                switch (esc) {
                    case '"'  -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/'  -> sb.append('/');
                    case 'b'  -> sb.append('\b');
                    case 'f'  -> sb.append('\f');
                    case 'n'  -> sb.append('\n');
                    case 'r'  -> sb.append('\r');
                    case 't'  -> sb.append('\t');
                    case 'u'  -> {
                        if (pos + 4 >= source.length()) {
                            throw new JsonParseException(
                                "Incomplete unicode escape at position " + pos
                            );
                        }
                        String hex = source.substring(pos + 1, pos + 5);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw new JsonParseException(
                                "Invalid unicode escape \\u" + hex + " at position " + pos
                            );
                        }
                        pos += 4;
                    }
                    default -> throw new JsonParseException(
                        "Invalid escape sequence '\\" + esc + "' at position " + pos
                    );
                }
            } else if (c < 0x20) {
                throw new JsonParseException(
                    "Unescaped control character (0x" + Integer.toHexString(c) + ") at position " + pos
                );
            } else {
                sb.append(c);
            }
            pos++;
        }

        if (pos >= source.length()) {
            throw new JsonParseException("Unterminated string starting at position " + start);
        }
        pos++; // closing "
        return new Token(TokenType.STRING, sb.toString(), start);
    }

    private Token readNumber() {
        int start = pos;

        if (source.charAt(pos) == '-') pos++;

        if (pos >= source.length() || !Character.isDigit(source.charAt(pos))) {
            throw new JsonParseException("Invalid number at position " + start);
        }

        // integer part
        if (source.charAt(pos) == '0') {
            pos++;
        } else {
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) pos++;
        }

        // fractional part
        if (pos < source.length() && source.charAt(pos) == '.') {
            pos++;
            if (pos >= source.length() || !Character.isDigit(source.charAt(pos))) {
                throw new JsonParseException("Invalid number: expected digit after '.' at position " + pos);
            }
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) pos++;
        }

        // exponent part
        if (pos < source.length() && (source.charAt(pos) == 'e' || source.charAt(pos) == 'E')) {
            pos++;
            if (pos < source.length() && (source.charAt(pos) == '+' || source.charAt(pos) == '-')) pos++;
            if (pos >= source.length() || !Character.isDigit(source.charAt(pos))) {
                throw new JsonParseException("Invalid number: expected digit in exponent at position " + pos);
            }
            while (pos < source.length() && Character.isDigit(source.charAt(pos))) pos++;
        }

        return new Token(TokenType.NUMBER, source.substring(start, pos), start);
    }

    private Token readKeyword(String keyword, TokenType type) {
        int start = pos;
        if (source.startsWith(keyword, pos)) {
            // 키워드 바로 다음이 영문자인 경우 잘못된 토큰 (예: "nulll", "truefalse")
            int end = pos + keyword.length();
            if (end < source.length()) {
                char after = source.charAt(end);
                if (Character.isLetterOrDigit(after) || after == '_') {
                    throw new JsonParseException("Unexpected token at position " + pos);
                }
            }
            pos += keyword.length();
            return new Token(type, keyword, start);
        }
        throw new JsonParseException("Unexpected token at position " + pos);
    }
}
