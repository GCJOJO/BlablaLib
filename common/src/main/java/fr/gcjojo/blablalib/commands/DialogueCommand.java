package fr.gcjojo.blablalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.networking.NetworkManager;
import fr.gcjojo.blablalib.BlablaLib;
import fr.gcjojo.blablalib.network.ModNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DialogueCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dialogue").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("play")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String currentChapter = BlablaLib.getPlayerDataManager().getPlayerCurrentChapter(player);
                            if(!currentChapter.isEmpty()) {
                                BlablaLib.OpenDialogue(player, currentChapter);
                                return 1;
                            }

                            player.sendSystemMessage(Component.translatable("blablalib.commands.invalid_dialogue", currentChapter).withStyle(ChatFormatting.RED));
                            return 0;
                        }))
                .then(Commands.literal("set")
                        .then(Commands.argument("chapterName", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String chapterName = StringArgumentType.getString(context, "chapterName");

                                    BlablaLib.getPlayerDataManager().setPlayerCurrentChapter(player, chapterName);
                                    context.getSource().sendSuccess(() -> Component.translatable("blablalib.commands.updated_dialogue", chapterName).withStyle(ChatFormatting.GREEN), true);
                                    return 1;
                                })))
        );
    }
}
