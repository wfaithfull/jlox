package me.faithfull.jlox;

import java.util.List;

/**
 * Created by wfaithfull on 25/03/17.
 */
public class Parser {

    private static class ParseError extends RuntimeException {}
    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    /**
     * expression → equality
     */
    private Expression expression() {
        return equality();
    }

    /**
     * equality → comparison ( ( "!=" | "==" ) comparison )*
     */
    private Expression equality() {
        Expression left = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    /**
     * comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )*
     */
    private Expression comparison() {
        Expression left = term();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    private Expression term() {
        Expression left = factor();

        while(match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    private Expression factor() {
        Expression left = unary();

        while(match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = unary();
            left = new Expression.Binary(left, operator, right);
        }

        return left;
    }

    private Expression unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if(match(TokenType.FALSE)) return new Expression.Literal(false);
        if(match(TokenType.TRUE)) return new Expression.Literal(true);
        if(match(TokenType.NIL)) return new Expression.Literal(null);

        if(match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(previous().literal);
        }

        if(match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            // Consume right parenthesis or syntax error
            consume(TokenType.RIGHT_PAREN, "Expected ')' after grouping expression");
            return new Expression.Grouping(expression);
        }

        throw error(peek(), "Expect expression");
    }

    private Token consume(TokenType tokenType, String message) {
        if(check(tokenType)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronise() {
        advance();

        while(!isAtEnd()) {
            if(previous().type == TokenType.SEMICOLON)
                return; // Synchronise on defined end of statement

            // Or when a new statement is starting
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType tokenType) {
        if(isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return this.tokens.get(current);
    }

    private Token previous() {
        return this.tokens.get(current - 1);
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }
}
