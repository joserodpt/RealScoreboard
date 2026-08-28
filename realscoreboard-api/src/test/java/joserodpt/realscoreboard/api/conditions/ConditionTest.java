package joserodpt.realscoreboard.api.conditions;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionTest {
    private final Condition condition = new Condition(null, "", "", "");

    @Test
    public void evaluatesTextWithoutChangingStringOperators() {
        assertTrue(condition.parseExpression("compact == compact"));
        assertTrue(condition.parseExpression("full != compact"));
        assertTrue(condition.parseExpression("compact startsWith comp"));
        assertTrue(condition.parseExpression("full contains ull"));
        assertTrue(condition.parseExpression("1.2.3 == 1.2.3"));
        assertFalse(condition.parseExpression("compact == full"));
    }

    @Test
    public void preservesIntegerAndFloatingPointComparisons() {
        assertTrue(condition.parseExpression("2147483647 == 2147483647"));
        assertTrue(condition.parseExpression("-2147483648 == -2147483648"));
        assertTrue(condition.parseExpression("\u0661 == 1"));
        assertTrue(condition.parseExpression("2147483648 > 2147483647"));
        assertTrue(condition.parseExpression("1.25 < 1.5"));
        assertTrue(condition.parseExpression("1e3 == 1000"));
        assertTrue(condition.parseExpression("1f == 1"));
        assertTrue(condition.parseExpression("0x1.0p2 == 4"));
        assertTrue(condition.parseExpression("Infinity > 1"));
        assertFalse(condition.parseExpression("NaN == NaN"));
    }

    @Test
    public void preservesBooleanComparisons() {
        assertTrue(condition.parseExpression("true == true"));
        assertTrue(condition.parseExpression("true != false"));
        assertFalse(condition.parseExpression("false != false"));
    }
}
