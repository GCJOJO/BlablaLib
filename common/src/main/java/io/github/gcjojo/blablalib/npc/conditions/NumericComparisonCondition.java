package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.npc.NPCState;
import io.github.gcjojo.blablalib.npc.TransitionCondition;
import lombok.Getter;

import java.util.Optional;
import java.util.function.BiFunction;

@Getter
public abstract class NumericComparisonCondition extends TransitionCondition {
    private final String variableName;
    private final double targetValue;

    public NumericComparisonCondition(String variableName, double targetValue) {
        this.variableName = variableName;
        this.targetValue = targetValue;
    }

    protected static <T extends NumericComparisonCondition> MapCodec<T> createCodec(BiFunction<String, Double, T> factory) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("variable").forGetter(NumericComparisonCondition::getVariableName),
                        Codec.DOUBLE.fieldOf("value").forGetter(NumericComparisonCondition::getTargetValue)
                ).apply(instance, factory)
        );
    }

    protected abstract boolean compare(double currentValue, double targetValue);

    @Override
    public boolean canTransition(NPCState state) {
        Optional<Object> currentValue = state.getVariableValue(variableName);
        if(currentValue.isEmpty()) return false;
        if(!(currentValue.get() instanceof Number number)) return false;
        return compare(number.doubleValue(), targetValue);
    }
}
