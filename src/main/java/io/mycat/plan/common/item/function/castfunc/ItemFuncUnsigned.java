package io.mycat.plan.common.item.function.castfunc;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import io.mycat.plan.common.field.Field;
import io.mycat.plan.common.item.Item;
import io.mycat.plan.common.item.function.primary.ItemIntFunc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * CAST(expr AS type)<br>
 * type:<br>
 * BINARY[(N)]<br>
 * CHAR[(N)]<br>
 * DATE<br>
 * DATETIME<br>
 * DECIMAL[(M[,D])]<br>
 * SIGNED [INTEGER]<br>
 * TIME<br>
 * UNSIGNED [INTEGER]<br>
 *
 * @author Administrator
 */
public class ItemFuncUnsigned extends ItemIntFunc {

    public ItemFuncUnsigned(Item a) {
        super(new ArrayList<Item>());
        args.add(a);
    }

    @Override
    public final String funcName() {
        return "cast_as_unsigned";
    }

    @Override
    public BigInteger valInt() {
        BigInteger value = BigInteger.ZERO;

        if (args.get(0).castToIntType() == ItemResult.DECIMAL_RESULT) {
            BigDecimal dec = args.get(0).valDecimal();
            if (!(nullValue = args.get(0).nullValue))
                value = dec.toBigInteger();
            else
                value = BigInteger.ZERO;
            return value;
        } else if (args.get(0).castToIntType() != ItemResult.STRING_RESULT || args.get(0).isTemporal()) {
            value = args.get(0).valInt();
            nullValue = args.get(0).nullValue;
            return value;
        }

        try {
            value = val_int_from_str();
        } catch (Exception e) {
            value = new BigInteger("-1");
            logger.error("Cast to unsigned converted negative integer to it's " + "positive complement", e);
        }
        return value;
    }

    protected BigInteger val_int_from_str() throws Exception {
        /*
         * For a string result, we must first get the string and then convert it
         * to a longlong
         */

        String res = args.get(0).valStr();
        if (res == null) {
            nullValue = true;
            return BigInteger.ZERO;
        }

        nullValue = false;
        return new BigInteger(res);
    }

    @Override
    public int decimalPrecision() {
        return args.get(0).decimalPrecision();
    }

    @Override
    public SQLExpr toExpression() {
        SQLCastExpr cast = new SQLCastExpr();
        cast.setExpr(args.get(0).toExpression());
        SQLDataTypeImpl dataType = new SQLDataTypeImpl("UNSIGNED");
        cast.setDataType(dataType);
        return cast;
    }

    @Override
    protected Item cloneStruct(boolean forCalculate, List<Item> calArgs, boolean isPushDown, List<Field> fields) {
        List<Item> newArgs = null;
        if (!forCalculate)
            newArgs = cloneStructList(args);
        else
            newArgs = calArgs;
        return new ItemFuncUnsigned(newArgs.get(0));
    }

}
