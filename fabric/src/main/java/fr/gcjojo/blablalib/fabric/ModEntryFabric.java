package fr.gcjojo.blablalib.fabric;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import fr.gcjojo.blablalib.BlablaLib;
import fr.gcjojo.blablalib.commands.DialogueCommand;
import fr.gcjojo.blablalib.fabric.client.FabricSoundPlayer;
import net.fabricmc.api.ModInitializer;

public final class ModEntryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        BlablaLib.init();
        BlablaLib.setSoundPlayer(new FabricSoundPlayer());
        BlablaLib.setPlayerDataManager(new FabricPlayerDataManager());

        CommandRegistrationEvent.EVENT.register(((dispatcher, registry, selection) -> {
            DialogueCommand.register(dispatcher);
        }));
    }
}
