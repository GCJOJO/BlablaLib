package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueCommandPayload(String command) implements CustomPacketPayload {
    public static final Type<DialogueCommandPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_command"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueCommandPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DialogueCommandPayload::command,
            DialogueCommandPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
