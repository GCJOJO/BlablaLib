package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.MapCodec;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.TransitionConditions;

public class GreaterCondition extends NumericComparisonCondition {

    public static final MapCodec<GreaterCondition> CODEC = createCodec(GreaterCondition::new);

    public GreaterCondition(String variableName, double targetValue) {
        super(variableName, targetValue);
    }

    @Override
    protected boolean compare(double currentValue, double targetValue) {
        return currentValue > targetValue;
    }

    @Override
    public ConditionType<?> type() {
        return TransitionConditions.GREATER;
    }
}
