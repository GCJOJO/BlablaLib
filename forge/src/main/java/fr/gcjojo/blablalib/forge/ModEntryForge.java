package fr.gcjojo.blablalib.forge;

import fr.gcjojo.blablalib.ModEntry;
import dev.architectury.platform.forge.EventBuses;
import fr.gcjojo.blablalib.forge.client.ForgeSoundPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModEntry.MOD_ID)
public final class ModEntryForge {
    public ModEntryForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(ModEntry.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        ModEntry.init();
        ModEntry.setSoundPlayer(new ForgeSoundPlayer());
        ModEntry.setPlayerDataManager(new ForgePlayerDataManager());
    }
}
