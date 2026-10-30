package io.github.gcjojo.blablalib.npc;

import com.mojang.serialization.Codec;

public abstract class TransitionCondition {
    public abstract boolean canTransition(NPCState state);

    public abstract ConditionType<?> type();

    public static final Codec<TransitionCondition> CODEC = TransitionConditions.CODEC.dispatch(TransitionCondition::type, ConditionType::codec);
}
