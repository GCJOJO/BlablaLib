package io.github.gcjojo.blablalib.forge;

import io.github.gcjojo.blablalib.PlayerDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ForgePlayerDataManager extends PlayerDataManager {
    private CompoundTag getBlablalibTag(Player player){
        CompoundTag persistentData = player.getPersistentData();
        if(!persistentData.contains("BlablaLib"))
            persistentData.put("BlablaLib", new CompoundTag());
        return persistentData.getCompound("BlablaLib");
    }

    @Override
    public void setPlayerInDialogue(Player player, boolean isInDialogue) {
        getBlablalibTag(player).putBoolean("IsInDialogue", isInDialogue);
    }

    @Override
    public boolean getPlayerInDialogue(Player player) {
        return getBlablalibTag(player).getBoolean("IsInDialogue");
    }

    @Override
    public void setPlayerLastReadChapter(Player player, String lastReadChapter) {
        getBlablalibTag(player).putString("LastReadChapter", lastReadChapter);
    }

    @Override
    public String getPlayerLastReadChapter(Player player) {
        return getBlablalibTag(player).getString("LastReadChapter");
    }

    @Override
    public void setPlayerCurrentChapter(Player player, String currentChapter) {
        getBlablalibTag(player).putString("CurrentChapter", currentChapter);
    }

    @Override
    public String getPlayerCurrentChapter(Player player) {
        return getBlablalibTag(player).getString("CurrentChapter");
    }

    @Override
    public CompoundTag getAdditionalData(Player player) {
        CompoundTag blablalibTag = getBlablalibTag(player);
        if(!blablalibTag.contains("AdditionalData"))
            blablalibTag.put("AdditionalData", new CompoundTag());
        return blablalibTag.getCompound("AdditionalData");
    }
}
