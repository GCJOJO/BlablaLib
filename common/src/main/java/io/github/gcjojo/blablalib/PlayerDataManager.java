package io.github.gcjojo.blablalib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerDataManager {
    public abstract void setPlayerInDialogue(Player player, boolean isInDialogue);
    public abstract boolean getPlayerInDialogue(Player player);

    public abstract void setPlayerLastReadDialogue(Player player, ResourceLocation lastReadChapter);
    public abstract ResourceLocation getPlayerLastReadDialogue(Player player);

    public abstract void setPlayerCurrentDialogue(Player player, ResourceLocation currentChapter);
    public abstract ResourceLocation getPlayerCurrentDialogue(Player player);

    public abstract CompoundTag getAdditionalData(Player player);
    public abstract void setAdditionalData(Player player, CompoundTag data);

    public void copyPlayer(Player oldPlayer, Player newPlayer){
        setPlayerInDialogue(newPlayer, false);
        setPlayerCurrentDialogue(newPlayer, getPlayerCurrentDialogue(oldPlayer));
        setPlayerLastReadDialogue(newPlayer, getPlayerLastReadDialogue(oldPlayer));
        setAdditionalData(newPlayer, getAdditionalData(oldPlayer));
    }
}
