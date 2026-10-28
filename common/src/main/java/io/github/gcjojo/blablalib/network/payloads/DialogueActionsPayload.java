package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// needs to send the next N blocking actions and the non blocking actions between them
// also needs to send the next speakers

public record DialogueActionsPayload(int blockingActionsSent, List<DialogueAction> actions) implements CustomPacketPayload {
    public static final Type<DialogueActionsPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_actions"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueActionsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueActionsPayload::blockingActionsSent,
            DialogueAction.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogueActionsPayload::actions,
            DialogueActionsPayload::new
    );

    @Override
    public @NotNull Type<?> type() { return TYPE; }
}
