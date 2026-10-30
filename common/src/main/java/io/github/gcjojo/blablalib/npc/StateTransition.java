package io.github.gcjojo.blablalib.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.npc.conditions.TransitionCondition;

public record StateTransition(String toState, TransitionCondition condition) {
    public static Codec<StateTransition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Codec.STRING.fieldOf("target_state").forGetter(StateTransition::toState),
                            TransitionCondition.CODEC.fieldOf("condition").forGetter(StateTransition::condition))
                    .apply(instance, StateTransition::new));
}
