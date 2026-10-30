package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.MapCodec;
import io.github.gcjojo.blablalib.npc.ConditionType;

public class LessThanOrEqualCondition extends NumericComparisonCondition {

    public static final MapCodec<LessThanOrEqualCondition> CODEC = createCodec(LessThanOrEqualCondition::new);

    public LessThanOrEqualCondition(String variableName, double targetValue) {
        super(variableName, targetValue);
    }

    @Override
    protected boolean compare(double currentValue, double targetValue) { return currentValue <= targetValue; }

    @Override
    public ConditionType<?> type() {
        return TransitionConditions.LESS_THAN_OR_EQUAL;
    }
}
