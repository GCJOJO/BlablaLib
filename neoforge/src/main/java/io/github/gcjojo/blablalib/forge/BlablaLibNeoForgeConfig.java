package io.github.gcjojo.blablalib.forge;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = BlablaLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlablaLibForgeConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_COMMAND = BUILDER
            .comment("Whether or not the Blabla Lib mod should register it's default dialogue command.")
            .define("enable_command", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableCommand;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event){
        enableCommand = ENABLE_COMMAND.get();
    }
}

