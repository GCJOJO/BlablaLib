package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueChoicePayload(ResourceLocation nextDialogue, ResourceLocation saveDialogue, String action) implements CustomPacketPayload {
    public static final Type<DialogueChoicePayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_choice"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoicePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueChoicePayload::nextDialogue,
            ResourceLocation.STREAM_CODEC, DialogueChoicePayload::saveDialogue,
            ByteBufCodecs.STRING_UTF8, DialogueChoicePayload::action,
            DialogueChoicePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
