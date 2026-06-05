package io.github.gcjojo.blablalib.forge.commands;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.forge.BlablaLibForgeConfig;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlablaLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommandRegister {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if(BlablaLibForgeConfig.enableCommand)
            DialogueCommand.register(event.getDispatcher());
    }
}
