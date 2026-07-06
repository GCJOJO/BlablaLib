package io.github.gcjojo.blablalib.api;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class BlablaLibAPIImpl extends io.github.gcjojo.liblib.api.BlablaLibAPI {
    @Override
    public void openDialogue(ServerPlayer player, ResourceLocation dialogueId) {
        BlablaLib.openDialogue(player, dialogueId);
    }

    @Override
    public void setCurrentDialogue(ServerPlayer player, ResourceLocation dialogueId) {
        BlablaLib.setPlayerDialogue(player, dialogueId);
    }

    @Override
    public boolean isPlayerInDialogue(ServerPlayer player) {
        return BlablaLib.isPlayerInDialogue(player);
    }

    @Override
    public Optional<ResourceLocation> getPlayerLastReadDialogue(ServerPlayer player) {
        return Optional.ofNullable(BlablaLib.getPlayerLastReadDialogue(player));
    }

    @Override
    public void setPlayerLastReadDialogue(ServerPlayer player, ResourceLocation lastReadDialogueId) {
        BlablaLib.setPlayerLastReadDialogue(player, lastReadDialogueId);
    }
}
