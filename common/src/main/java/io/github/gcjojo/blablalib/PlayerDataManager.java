package io.github.gcjojo.blablalib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@Deprecated
public abstract class PlayerDataManager {
    public abstract void setPlayerInDialogue(Player player, boolean isInDialogue);
    public abstract boolean getPlayerInDialogue(Player player);

    public abstract void setPlayerLastReadDialogue(Player player, ResourceLocation lastReadChapter);
    public abstract ResourceLocation getPlayerLastReadDialogue(Player player);

    public abstract void setPlayerCurrentDialogue(Player player, ResourceLocation currentChapter);
    public abstract ResourceLocation getPlayerCurrentDialogue(Player player);

    public abstract CompoundTag getAdditionalData(Player player);
    public abstract void setAdditionalData(Player player, CompoundTag data);

    public CompoundTag getAllData(Player player) {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("IsInDialogue", getPlayerInDialogue(player));
        nbt.putString("CurrentDialogue", getPlayerCurrentDialogue(player).toString());
        nbt.putString("LastReadDialogue", getPlayerLastReadDialogue(player).toString());
        nbt.put("AdditionalData", getAdditionalData(player));

        return nbt;
    }

    public void copyPlayer(Player oldPlayer, Player newPlayer){
        setPlayerInDialogue(newPlayer, false);
        setPlayerCurrentDialogue(newPlayer, getPlayerCurrentDialogue(oldPlayer));
        setPlayerLastReadDialogue(newPlayer, getPlayerLastReadDialogue(oldPlayer));
        setAdditionalData(newPlayer, getAdditionalData(oldPlayer));
    }
}
