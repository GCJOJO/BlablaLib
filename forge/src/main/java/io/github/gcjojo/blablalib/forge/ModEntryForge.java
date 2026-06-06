package io.github.gcjojo.blablalib.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.ClientModEvents;
import io.github.gcjojo.blablalib.forge.client.ForgeSoundPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BlablaLib.MOD_ID)
public final class ModEntryForge {
    public ModEntryForge(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.register(this);

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(BlablaLib.MOD_ID, context.getModEventBus());

        // Run our common setup.
        BlablaLib.init();
        BlablaLib.setPlayerDataManager(new ForgePlayerDataManager());

        context.registerConfig(ModConfig.Type.COMMON, BlablaLibForgeConfig.SPEC);
    }

    @Mod.EventBusSubscriber(modid = BlablaLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEntryForge {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            BlablaLib.initClient();
            BlablaLib.setSoundPlayer(new ForgeSoundPlayer());
            ClientModEvents.registerClientModEvents();
        }
    }
}
