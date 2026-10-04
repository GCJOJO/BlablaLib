package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueScreenOpenedPayload() implements CustomPacketPayload {
    public static final Type<DialogueScreenOpenedPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_screen_opened"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueScreenOpenedPayload> STREAM_CODEC = StreamCodec.unit(new DialogueScreenOpenedPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
