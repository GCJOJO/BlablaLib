package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

@Getter
public class CountCondition extends TransitionCondition {
    public final List<TransitionCondition> conditions;
    public final int requiredAmount;

    public static final MapCodec<CountCondition> COUNT_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TransitionCondition.CODEC.listOf().fieldOf("conditions").forGetter(CountCondition::getConditions),
                    Codec.INT.fieldOf("required_amount").forGetter(CountCondition::getRequiredAmount)
            ).apply(instance, CountCondition::new));

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "count"),
            COUNT_CODEC
    );

    public CountCondition(List<TransitionCondition> conditions, int requiredAmount) {
        this.conditions = List.copyOf(conditions);
        this.requiredAmount = Math.min(requiredAmount, conditions.size());
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        int passingConditions = 0;
        for(TransitionCondition condition : conditions) {
            if(condition.canTransition(player, state))
                passingConditions++;
        }

        return passingConditions >= requiredAmount;
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
