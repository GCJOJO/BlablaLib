package io.github.gcjojo.blablalib;

import net.minecraft.world.entity.player.Player;

public abstract class PlayerDataManager {
    public abstract void setPlayerInDialogue(Player player, boolean isInDialogue);
    public abstract boolean getPlayerInDialogue(Player player);

    public abstract void setPlayerLastReadChapter(Player player, String lastReadChapter);
    public abstract String getPlayerLastReadChapter(Player player);

    public abstract void setPlayerCurrentChapter(Player player, String currentChapter);
    public abstract String getPlayerCurrentChapter(Player player);
}
