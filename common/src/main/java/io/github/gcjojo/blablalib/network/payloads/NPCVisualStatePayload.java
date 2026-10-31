package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.npc.NPCVisualState;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record NPCVisualStatePayload(UUID entityId, NPCVisualState newVisualState) implements CustomPacketPayload {
    public static final Type<NPCVisualStatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "npc_visual_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NPCVisualStatePayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, NPCVisualStatePayload::entityId,
            NPCVisualState.STREAM_CODEC, NPCVisualStatePayload::newVisualState,
            NPCVisualStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
