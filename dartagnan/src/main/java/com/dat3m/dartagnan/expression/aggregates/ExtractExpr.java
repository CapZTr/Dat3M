package com.dat3m.dartagnan.expression.aggregates;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionKind;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.Type;
import com.dat3m.dartagnan.expression.base.UnaryExpressionBase;
import com.dat3m.dartagnan.expression.type.AggregateType;
import com.dat3m.dartagnan.expression.type.ArrayType;
import com.dat3m.dartagnan.expression.type.TypeOffset;
import com.google.common.base.Preconditions;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public final class ExtractExpr extends UnaryExpressionBase<Type, ExpressionKind.Other> {

    private final int index;

    public ExtractExpr(int index, Expression expr) {
        super(extractType(expr, index), ExpressionKind.Other.EXTRACT, expr);
        this.index = index;
    }

    private static Type extractType(Expression expr, int index) {
        final Type exprType = expr.getType();
        Preconditions.checkArgument(exprType instanceof AggregateType || exprType instanceof ArrayType,
                "Cannot extract from a non-aggregate expression: (%s)[%d].", expr, index);
        if (exprType instanceof AggregateType aggregateType) {
            final List<TypeOffset> typeOffsets = aggregateType.getTypeOffsets();
            checkArgument(0 <= index && index < typeOffsets.size());
            return typeOffsets.get(index).type();
        }
        final ArrayType arrayType = (ArrayType) exprType;
        checkArgument(0 <= index && (!arrayType.hasKnownNumElements() || index < arrayType.getNumElements()),
                "Index %s out of bounds [0,%s].", index, arrayType.getNumElements() - 1);
        return arrayType.getElementType();
    }

    public int getFieldIndex() {
        return index;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitExtractExpression(this);
    }
}
