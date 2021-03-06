package me.faithfull.jlox;

/**
 * Created by wfaithfull on 24/03/17.
 */
public class AstPrinter implements Expression.Visitor<String> {


    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        return expression.value.toString();
    }

    @Override
    public String visitConditionalExpression(Expression.Conditional expression) {
        return parenthesize(
                parenthesize("?", expression.conditional) +
                        parenthesize(":", expression.thenBranch) +
                        parenthesize(":", expression.elseBranch));
    }

    @Override
    public String visitPrefixExpression(Expression.Prefix expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    @Override
    public String visitPostfixExpression(Expression.Postfix expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    private String parenthesize(String name, Expression... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    String print(Expression expression) {
        return expression.accept(this);
    }
}
