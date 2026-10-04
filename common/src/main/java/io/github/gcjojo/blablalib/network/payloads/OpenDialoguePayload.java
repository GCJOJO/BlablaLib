package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenDialoguePayload(ResourceLocation dialogueId) implements CustomPacketPayload {
    public static final Type<OpenDialoguePayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "open_dialogue"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDialoguePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, OpenDialoguePayload::dialogueId,
            OpenDialoguePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
