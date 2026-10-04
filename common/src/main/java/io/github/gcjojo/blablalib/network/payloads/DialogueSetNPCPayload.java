package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogueSetNPCPayload(int npcId, String npcModel, String npcTexture, String npcAnimation, boolean loopAnimation) implements CustomPacketPayload {
    public static final Type<DialogueSetNPCPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_set_npc"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSetNPCPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DialogueSetNPCPayload::npcId,
            ByteBufCodecs.STRING_UTF8, DialogueSetNPCPayload::npcModel,
            ByteBufCodecs.STRING_UTF8, DialogueSetNPCPayload::npcTexture,
            ByteBufCodecs.STRING_UTF8, DialogueSetNPCPayload::npcAnimation,
            ByteBufCodecs.BOOL, DialogueSetNPCPayload::loopAnimation,
            DialogueSetNPCPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
