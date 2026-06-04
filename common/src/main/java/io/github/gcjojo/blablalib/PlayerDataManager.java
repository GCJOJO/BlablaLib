package io.github.gcjojo.blablalib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerDataManager {
    public abstract void setPlayerInDialogue(Player player, boolean isInDialogue);
    public abstract boolean getPlayerInDialogue(Player player);

    public abstract void setPlayerLastReadChapter(Player player, String lastReadChapter);
    public abstract String getPlayerLastReadChapter(Player player);

    public abstract void setPlayerCurrentChapter(Player player, String currentChapter);
    public abstract String getPlayerCurrentChapter(Player player);

    public abstract CompoundTag getAdditionalData(Player player);
    public abstract void setAdditionalData(Player player, CompoundTag data);

    public void copyPlayer(Player oldPlayer, Player newPlayer){
        setPlayerInDialogue(newPlayer, false);
        setPlayerCurrentChapter(newPlayer, getPlayerCurrentChapter(oldPlayer));
        setPlayerLastReadChapter(newPlayer, getPlayerLastReadChapter(oldPlayer));
        setAdditionalData(newPlayer, getAdditionalData(oldPlayer));
    }
}
