package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.MapCodec;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.TransitionConditions;

public class GreaterThanOrEqualCondition extends NumericComparisonCondition {

    public static final MapCodec<GreaterThanOrEqualCondition> CODEC = createCodec(GreaterThanOrEqualCondition::new);

    public GreaterThanOrEqualCondition(String variableName, double targetValue) {
        super(variableName, targetValue);
    }

    @Override
    protected boolean compare(double currentValue, double targetValue) {
        return currentValue >= targetValue;
    }

    @Override
    public ConditionType<?> type() {
        return TransitionConditions.GREATER_THAN_OR_EQUAL;
    }
}
