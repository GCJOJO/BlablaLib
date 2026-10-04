package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueSetTaskStatePayload(ResourceLocation questId, ResourceLocation taskId, String newState) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DialogueSetTaskStatePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_set_task_state_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSetTaskStatePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DialogueSetTaskStatePayload::questId,
            ResourceLocation.STREAM_CODEC, DialogueSetTaskStatePayload::taskId,
            ByteBufCodecs.STRING_UTF8, DialogueSetTaskStatePayload::newState,
            DialogueSetTaskStatePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
