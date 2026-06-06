package io.github.gcjojo.blablalib.fabric;

import io.github.gcjojo.blablalib.PlayerDataManager;
import io.github.gcjojo.blablalib.fabric.data_components.ModComponents;
import io.github.gcjojo.blablalib.fabric.data_components.PlayerDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class FabricPlayerDataManager extends PlayerDataManager {
    private PlayerDataComponent getPlayerDataComponent(Player player){
        return ModComponents.PLAYER_DATA.get(player);
    }

    @Override
    public void setPlayerInDialogue(Player player, boolean isInDialogue) {
        getPlayerDataComponent(player).setIsInDialogue(isInDialogue);
    }

    @Override
    public boolean getPlayerInDialogue(Player player) {
        return getPlayerDataComponent(player).getIsInDialogue();
    }

    @Override
    public void setPlayerLastReadDialogue(Player player, ResourceLocation lastReadChapter) {
        getPlayerDataComponent(player).setLastReadDialogue(lastReadChapter);
    }

    @Override
    public ResourceLocation getPlayerLastReadDialogue(Player player) {
        return getPlayerDataComponent(player).getLastReadDialogue();
    }

    @Override
    public void setPlayerCurrentDialogue(Player player, ResourceLocation currentChapter) {
        getPlayerDataComponent(player).setCurrentDialogue(currentChapter);
    }

    @Override
    public ResourceLocation getPlayerCurrentDialogue(Player player) {
        return getPlayerDataComponent(player).getCurrentDialogue();
    }

    @Override
    public CompoundTag getAdditionalData(Player player) {
        return getPlayerDataComponent(player).getAdditionalData();
    }

    @Override
    public void setAdditionalData(Player player, CompoundTag data) {
        getAdditionalData(player).merge(data);
    }


}
