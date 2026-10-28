package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DialogueAdvancedPayload(int newActionIndex) implements CustomPacketPayload {
    public static final Type<DialogueAdvancedPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "advance_dialogue"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueAdvancedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueAdvancedPayload::newActionIndex,
            DialogueAdvancedPayload::new
    );

    @Override
    public @NotNull Type<?> type() { return TYPE; }
}
