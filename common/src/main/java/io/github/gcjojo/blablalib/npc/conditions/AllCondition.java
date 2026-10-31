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
public class AllCondition extends TransitionCondition {
    public final List<TransitionCondition> conditions;

    public static final MapCodec<AllCondition> ALL_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TransitionCondition.CODEC.listOf().fieldOf("conditions").forGetter(AllCondition::getConditions)
            ).apply(instance, AllCondition::new));

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "all"),
            ALL_CODEC
    );

    public AllCondition(List<TransitionCondition> conditions) {
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
