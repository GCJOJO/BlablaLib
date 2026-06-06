package io.github.gcjojo.blablalib.forge;

import io.github.gcjojo.blablalib.PlayerDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.lang.module.ResolutionException;

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
    public void setPlayerLastReadDialogue(Player player, ResourceLocation lastReadDialogue) {
        getBlablalibTag(player).putString("LastReadDialogue", lastReadDialogue.toString());
    }

    @Override
    public ResourceLocation getPlayerLastReadDialogue(Player player) {
        return ResourceLocation.tryParse(getBlablalibTag(player).getString("LastReadDialogue"));
    }

    @Override
    public void setPlayerCurrentDialogue(Player player, ResourceLocation currentDialogue) {
        getBlablalibTag(player).putString("CurrentDialogue", currentDialogue.toString());
    }

    @Override
    public ResourceLocation getPlayerCurrentDialogue(Player player) {
        return ResourceLocation.tryParse(getBlablalibTag(player).getString("CurrentDialogue"));
    }

    @Override
    public CompoundTag getAdditionalData(Player player) {
        CompoundTag blablalibTag = getBlablalibTag(player);
        if(!blablalibTag.contains("AdditionalData"))
            blablalibTag.put("AdditionalData", new CompoundTag());
        return blablalibTag.getCompound("AdditionalData");
    }

    @Override
    public void setAdditionalData(Player player, CompoundTag data) {
        getAdditionalData(player).merge(data);
    }
}
