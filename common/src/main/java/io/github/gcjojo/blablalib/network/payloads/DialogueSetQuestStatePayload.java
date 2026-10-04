package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueSetQuestStatePayload(ResourceLocation questId, String newState) implements CustomPacketPayload {
    public static final Type<DialogueSetQuestStatePayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_set_quest_state_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSetQuestStatePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueSetQuestStatePayload::questId,
            ByteBufCodecs.STRING_UTF8, DialogueSetQuestStatePayload::newState,
            DialogueSetQuestStatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
