package fr.gcjojo.blablalib.forge.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.networking.NetworkManager;
import fr.gcjojo.blablalib.ModEntry;
import fr.gcjojo.blablalib.commands.DialogueCommand;
import fr.gcjojo.blablalib.network.ModNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModEntry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommandRegister {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DialogueCommand.register(event.getDispatcher());
    }
}
