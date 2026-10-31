package io.github.gcjojo.blablalib.npc.conditions;

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
public class AndCondition extends TransitionCondition {
    private final List<TransitionCondition> conditions;

    public static final MapCodec<AndCondition> AND_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TransitionCondition.CODEC.listOf().fieldOf("conditions").forGetter(AndCondition::getConditions)
            ).apply(instance, AndCondition::new)
    );

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "and"),
            AND_CODEC
    );

    public AndCondition(List<TransitionCondition> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        for(TransitionCondition condition : conditions) {
            if(!condition.canTransition(player, state))
                return false;
        }

        return true;
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
