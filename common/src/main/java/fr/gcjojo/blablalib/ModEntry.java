package fr.gcjojo.blablalib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import fr.gcjojo.blablalib.client.SoundPlayer;
import fr.gcjojo.blablalib.events.BlablalibEvents;
import fr.gcjojo.blablalib.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public final class ModEntry {
    public static final String MOD_ID = "blablalib";
    private static SoundPlayer SOUND_PLAYER;
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.
        ModNetwork.registerPackets();

        BlablalibEvents.DIALOGUE_COMPLETED.register((ServerPlayer player, String completedDialogue) -> {
            LOGGER.warn("Player {} has completed dialogue {}", player.getName().getString(), completedDialogue);
            return EventResult.interruptDefault();
        });
    }

    public static SoundPlayer getSoundPlayer() { return SOUND_PLAYER; }
    public static void setSoundPlayer(SoundPlayer newSoundPlayer) { SOUND_PLAYER = newSoundPlayer; }

    public static PlayerDataManager getPlayerDataManager() { return PLAYER_DATA_MANAGER; }
    public static void setPlayerDataManager(PlayerDataManager newPlayerDataManager) { PLAYER_DATA_MANAGER = newPlayerDataManager; }

    public static Logger getLogger() { return LOGGER; }
}
