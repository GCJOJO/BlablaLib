package fr.gcjojo.blablalib.forge;

import fr.gcjojo.blablalib.BlablaLib;
import dev.architectury.platform.forge.EventBuses;
import fr.gcjojo.blablalib.forge.client.ForgeSoundPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BlablaLib.MOD_ID)
public final class ModEntryForge {
    public ModEntryForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(BlablaLib.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        BlablaLib.init();
        BlablaLib.setSoundPlayer(new ForgeSoundPlayer());
        BlablaLib.setPlayerDataManager(new ForgePlayerDataManager());
    }
}
