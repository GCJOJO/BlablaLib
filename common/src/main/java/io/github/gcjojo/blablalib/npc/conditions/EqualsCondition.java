package io.github.gcjojo.blablalib.npc.conditions;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class EqualsCondition extends TransitionCondition {
    public static final MapCodec<EqualsCondition> EQUALS_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("variable").forGetter(EqualsCondition::getVariableName),
                    Codec.PASSTHROUGH.fieldOf("value").forGetter(EqualsCondition::getRawDynamicValue)
            ).apply(instance, (var, dynamic) -> new EqualsCondition(var, dynamic.convert(JsonOps.INSTANCE).getValue()))
    );

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "equals"),
            EQUALS_CODEC
    );

    @Getter
    private final String variableName;
    @Getter
    private final Dynamic<?> rawDynamicValue;

    public EqualsCondition(String variableName, JsonElement value) {
        this.variableName = variableName;
        this.rawDynamicValue = new Dynamic<>(JsonOps.INSTANCE, value);
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        Optional<Object> currentValue = state.getVariableValue(variableName);
        if(currentValue.isEmpty()) return false;
        if(!(currentValue.get() instanceof Boolean boolValue)) return false;

        return boolValue;
    }

    @Override
    public ConditionType<?> type() { return TYPE; }
}
