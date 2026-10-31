package io.github.gcjojo.blablalib.npc.conditions;

import com.mojang.serialization.MapCodec;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.ConditionType;
import io.github.gcjojo.blablalib.npc.NPCState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class IsInDialogueCondition extends TransitionCondition {
    public static final MapCodec<IsInDialogueCondition> IS_IN_DIALOGUE_CODEC = MapCodec.unit(IsInDialogueCondition::new);

    public static final ConditionType<?> TYPE = new ConditionType<>(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "is_in_dialogue"),
            IS_IN_DIALOGUE_CODEC
    );

    @Override
    public boolean canTransition(ServerPlayer player, NPCState state) {
        return BlablaLib.isPlayerInDialogue(player);
    }

    @Override
    public ConditionType<?> type() {
        return TYPE;
    }
}
