package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@Getter
public class NotCondition extends TransitionCondition {
    private final TransitionCondition innerCondition;

    public static final MapCodec<NotCondition> NOT_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TransitionCondition.CODEC.fieldOf("inner").forGetter(NotCondition::getInnerCondition)
            ).apply(instance, NotCondition::new)
    );

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "not"),
            NOT_CODEC
    );

    public NotCondition(TransitionCondition innerCondition) {
        this.innerCondition = innerCondition;
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        return !innerCondition.canTransition(player, state);
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
