package io.github.gcjojo.blablalib.forge;

import io.github.gcjojo.blablalib.BlablaLib;
import dev.architectury.platform.forge.EventBuses;
import io.github.gcjojo.blablalib.forge.client.ForgeSoundPlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BlablaLib.MOD_ID)
public final class ModEntryForge {
    public ModEntryForge() {
        FMLJavaModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeConfig.SPEC);

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(BlablaLib.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        BlablaLib.init();
        BlablaLib.setSoundPlayer(new ForgeSoundPlayer());
        BlablaLib.setPlayerDataManager(new ForgePlayerDataManager());
    }
}
