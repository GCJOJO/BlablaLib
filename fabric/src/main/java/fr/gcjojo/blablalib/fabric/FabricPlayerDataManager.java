package fr.gcjojo.blablalib.fabric;

import fr.gcjojo.blablalib.PlayerDataManager;
import fr.gcjojo.blablalib.fabric.data_components.ModComponents;
import fr.gcjojo.blablalib.fabric.data_components.PlayerDataComponent;
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
    public void setPlayerLastReadChapter(Player player, String lastReadChapter) {
        getPlayerDataComponent(player).setLastReadDialogue(lastReadChapter);
    }

    @Override
    public String getPlayerLastReadChapter(Player player) {
        return getPlayerDataComponent(player).getLastReadDialogue();
    }

    @Override
    public void setPlayerCurrentChapter(Player player, String currentChapter) {
        getPlayerDataComponent(player).setCurrentDialogue(currentChapter);
    }

    @Override
    public String getPlayerCurrentChapter(Player player) {
        return getPlayerDataComponent(player).getCurrentDialogue();
    }
}
