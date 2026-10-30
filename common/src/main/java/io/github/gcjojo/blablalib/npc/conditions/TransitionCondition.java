package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.Codec;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import net.minecraft.server.level.ServerPlayer;

public abstract class TransitionCondition {
    public abstract boolean canTransition(ServerPlayer player, NPCState state);

    public abstract ConditionType<?> type();

    public static final Codec<TransitionCondition> CODEC = TransitionConditions.CODEC.dispatch(TransitionCondition::type, ConditionType::codec);
}
