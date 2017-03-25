package me.faithfull.jlox;
import java.util.List;

abstract class Expression {
  interface Visitor<R> {
    R visitBinaryExpression(Binary expression);
    R visitGroupingExpression(Grouping expression);
    R visitLiteralExpression(Literal expression);
    R visitConditionalExpression(Conditional expression);
    R visitPrefixExpression(Prefix expression);
    R visitPostfixExpression(Postfix expression);
  }

  static class Binary extends Expression {
    Binary(Expression left, Token operator, Expression right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpression(this);
    }

    final Expression left;
    final Token operator;
    final Expression right;
  }

  static class Grouping extends Expression {
    Grouping(Expression expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpression(this);
    }

    final Expression expression;
  }

  static class Literal extends Expression {
    Literal(Object value) {
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpression(this);
    }

    final Object value;
  }

  static class Conditional extends Expression {
    Conditional(Expression conditional, Expression thenBranch, Expression elseBranch) {
      this.conditional = conditional;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitConditionalExpression(this);
    }

    final Expression conditional;
    final Expression thenBranch;
    final Expression elseBranch;
  }

  static class Prefix extends Expression {
    Prefix(Token operator, Expression right) {
      this.operator = operator;
      this.right = right;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrefixExpression(this);
    }

    final Token operator;
    final Expression right;
  }

  static class Postfix extends Expression {
    Postfix(Token operator, Expression right) {
      this.operator = operator;
      this.right = right;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPostfixExpression(this);
    }

    final Token operator;
    final Expression right;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
