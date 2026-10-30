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
public class XorCondition extends TransitionCondition {
    public final TransitionCondition leftCondition;
    public final TransitionCondition rightCondition;

    public static final MapCodec<XorCondition> XOR_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TransitionCondition.CODEC.fieldOf("left").forGetter(XorCondition::getLeftCondition),
                    TransitionCondition.CODEC.fieldOf("right").forGetter(XorCondition::getRightCondition)
            ).apply(instance, XorCondition::new));

    public static final ConditionType<XorCondition> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "xor"),
            XOR_CODEC
    );

    public XorCondition(TransitionCondition left, TransitionCondition right) {
        leftCondition = left;
        rightCondition = right;
    }

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        return leftCondition.canTransition(player, state) ^ rightCondition.canTransition(player, state);
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
