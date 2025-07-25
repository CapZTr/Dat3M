package com.dat3m.dartagnan.expression;

import com.dat3m.dartagnan.expression.aggregates.ConstructExpr;
import com.dat3m.dartagnan.expression.aggregates.ExtractExpr;
import com.dat3m.dartagnan.expression.aggregates.InsertExpr;
import com.dat3m.dartagnan.expression.booleans.BoolBinaryOp;
import com.dat3m.dartagnan.expression.booleans.BoolUnaryOp;
import com.dat3m.dartagnan.expression.floats.FloatSizeCast;
import com.dat3m.dartagnan.expression.floats.IntToFloatCast;
import com.dat3m.dartagnan.expression.integers.*;
import com.dat3m.dartagnan.expression.misc.GEPExpr;
import com.dat3m.dartagnan.expression.misc.ITEExpr;
import com.dat3m.dartagnan.program.Register;
import com.google.common.collect.Lists;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public final class ExpressionPrinter implements ExpressionVisitor<String> {

    private final boolean printRegistersWithFunctionId;

    private ExpressionKind kind;

    private static final Set<ExpressionKind> ASSOCIATIVE_OPERATIONS = Set.of(BoolBinaryOp.AND, BoolBinaryOp.OR,
            IntBinaryOp.AND, IntBinaryOp.OR, IntBinaryOp.XOR,
            IntBinaryOp.ADD, IntBinaryOp.MUL);

    private static final Set<ExpressionKind> SIMPLE_UNARY_OPERATIONS = Set.of(BoolUnaryOp.NOT, IntUnaryOp.MINUS);

    private static final Set<ExpressionKind> AGGREGATE_OPERATIONS = Set.of(
            ExpressionKind.Other.INSERT, ExpressionKind.Other.EXTRACT, ExpressionKind.Other.CONSTRUCT
    );

    // If not printing in program / global scope, then printing in function / local scope.
    public ExpressionPrinter(boolean globalScope) {
        this.printRegistersWithFunctionId = globalScope;
    }

    public String visit(Expression expr) {
        final ExpressionKind parentKind = kind;
        kind = expr.getKind();
        final String inner = expr.accept(this);
        final boolean noParentheses = parentKind == null || // Omit at top level.
                SIMPLE_UNARY_OPERATIONS.contains(kind) || // Omit at e.g. negations.
                ASSOCIATIVE_OPERATIONS.contains(kind) && parentKind == kind || // Omit at e.g. A+B+C.
                AGGREGATE_OPERATIONS.contains(kind) ||  // Aggregates have their own parenthesis/brackets
                expr.getOperands().isEmpty(); // Omit at registers, non-det values and literals.
        kind = parentKind;
        return noParentheses ? inner : "(" + inner + ")";
    }

    @Override
    public String visitExpression(Expression expr) {
        return expr.toString();
    }

    @Override
    public String visitBinaryExpression(BinaryExpression expr) {
        return visit(expr.getLeft()) + " " + expr.getKind() + " " + visit(expr.getRight());
    }

    @Override
    public String visitUnaryExpression(UnaryExpression expr) {
        return expr.getKind() + visit(expr.getOperand());
    }

    @Override
    public String visitIntConcat(IntConcat expr) {
        return Lists.reverse(expr.getOperands()).stream().map(this::visit).collect(Collectors.joining("::"));
    }

    @Override
    public String visitIntExtract(IntExtract expr) {
        return String.format("%s[%d..%d]", expr.getOperand().accept(this), expr.getLowBit(), expr.getHighBit());
    }

    @Override
    public String visitIntSizeCastExpression(IntSizeCast expr) {
        final String opName = expr.isTruncation() ? "trunc" : (expr.preservesSign() ? "sext" : "zext");
        return String.format("%s %s to %s", opName, visit(expr.getOperand()), expr.getTargetType());
    }

    @Override
    public String visitFloatToIntCastExpression(FloatToIntCast expr) {
        final String opName = expr.isSigned() ? "fptosi" : "fptoui";
        return String.format("%s %s to %s", opName, visit(expr.getOperand()), expr.getTargetType());
    }

    @Override
    public String visitFloatSizeCastExpression(FloatSizeCast expr) {
        final String opName = expr.isTruncation() ? "trunc" : "ext";
        return String.format("%s %s to %s", visit(expr.getOperand()), opName, expr.getTargetType());
    }

    @Override
    public String visitIntToFloatCastExpression(IntToFloatCast expr) {
        final String opName = expr.isSigned() ? "sitofp" : "uitofp";
        return String.format("%s %s to %s", opName, visit(expr.getOperand()), expr.getTargetType());
    }

    @Override
    public String visitExtractExpression(ExtractExpr extract) {
        return visit(extract.getOperand()) + extract.getIndices().stream().map(Objects::toString).collect(Collectors.joining(".", "[", "]"));
    }

    @Override
    public String visitInsertExpression(InsertExpr insert) {
        return String.format("%s[%s <- %s]",
                visit(insert.getAggregate()),
                insert.getIndices().stream().map(Objects::toString).collect(Collectors.joining(".")),
                visit(insert.getInsertedValue()));
    }

    @Override
    public String visitConstructExpression(ConstructExpr expr) {
        return expr.getOperands().stream().map(this::visit).collect(Collectors.joining(", ", "{ ", " }"));
    }

    @Override
    public String visitGEPExpression(GEPExpr expr) {
        return expr.getOperands().stream().map(this::visit).collect(Collectors.joining(", ", "GEP(", ")"));
    }

    @Override
    public String visitITEExpression(ITEExpr expr) {
        return visit(expr.getCondition()) + " ? " + visit(expr.getTrueCase()) + " : " + visit(expr.getFalseCase());
    }

    @Override
    public String visitRegister(Register reg) {
        return printRegistersWithFunctionId ? reg.getFunction().getId() + ":" + reg.getName() : reg.toString();
    }
}
