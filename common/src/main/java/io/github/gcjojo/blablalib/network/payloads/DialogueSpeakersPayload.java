package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueSpeaker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DialogueSpeakersPayload(List<DialogueSpeaker> speakers) implements CustomPacketPayload {
    public static final Type<DialogueSpeakersPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_speakers"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSpeakersPayload> STREAM_CODEC = StreamCodec.composite(
            DialogueSpeaker.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogueSpeakersPayload::speakers,
            DialogueSpeakersPayload::new
    );

    @Override
    public @NotNull Type<?> type() { return TYPE; }
}
