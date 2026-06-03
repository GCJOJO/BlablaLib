package fr.gcjojo.blablalib.forge.commands;

import fr.gcjojo.blablalib.BlablaLib;
import fr.gcjojo.blablalib.commands.DialogueCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlablaLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommandRegister {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DialogueCommand.register(event.getDispatcher());
    }
}
