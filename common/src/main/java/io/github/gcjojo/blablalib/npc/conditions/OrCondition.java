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
public class OrCondition extends TransitionCondition {
    private final List<TransitionCondition> conditions;

    public static final MapCodec<OrCondition> OR_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TransitionCondition.CODEC.listOf().fieldOf("conditions").forGetter(OrCondition::getConditions)
            ).apply(instance, OrCondition::new)
    );

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "or"),
            OR_CODEC
    );

    public OrCondition(List<TransitionCondition> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        for(TransitionCondition condition : conditions) {
            if(condition.canTransition(player, state))
                return true;
        }

        return false;
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
