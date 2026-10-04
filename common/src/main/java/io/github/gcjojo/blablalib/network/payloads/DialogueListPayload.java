package io.github.gcjojo.blablalib.network.payloads;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record DialogueListPayload(List<ResourceLocation> dialogueList) implements CustomPacketPayload {

    public static final Type<DialogueListPayload> TYPE = new Type<>(ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dialogue_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueListPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogueListPayload::dialogueList,
            DialogueListPayload::new
    );

    public DialogueListPayload {
        dialogueList = List.copyOf(dialogueList);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
