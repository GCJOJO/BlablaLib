package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueCompletedPayload(ResourceLocation dialogueId) implements CustomPacketPayload {
    public static final Type<DialogueCompletedPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_completed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueCompletedPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueCompletedPayload::dialogueId,
            DialogueCompletedPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
