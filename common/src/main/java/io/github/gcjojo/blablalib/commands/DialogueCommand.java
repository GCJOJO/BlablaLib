package io.github.gcjojo.blablalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class DialogueCommand {

    public interface DialogueTransformation {
        String transformDialogue(CommandContext<CommandSourceStack> context, String dialogue);

        public static final DialogueTransformation DEFAULT_TRANSFORMATION = (CommandContext<CommandSourceStack> context, String dialogue) -> { return dialogue; };
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        register(dispatcher, new ArrayList<>(), DialogueTransformation.DEFAULT_TRANSFORMATION);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, List<String> dialogues){
        register(dispatcher, dialogues, DialogueTransformation.DEFAULT_TRANSFORMATION);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, List<String> dialogues, DialogueTransformation transformation)
    {
        dispatcher.register(Commands.literal("dialogue")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("play")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String currentChapter = BlablaLib.getPlayerDataManager().getPlayerCurrentChapter(player);
                            if(!currentChapter.isEmpty()) {
                                BlablaLib.openDialogue(player, currentChapter);
                                return 1;
                            }

                            player.sendSystemMessage(Component.translatable("blablalib.commands.invalid_dialogue", currentChapter).withStyle(ChatFormatting.RED));
                            return 0;
                        }))
                .then(Commands.literal("set")
                        .then(Commands.argument("chapterName", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(dialogues, builder))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String chapterName = StringArgumentType.getString(context, "chapterName");

                                    chapterName = transformation.transformDialogue(context, chapterName);

                                    BlablaLib.getPlayerDataManager().setPlayerCurrentChapter(player, chapterName);
                                    String finalChapterName = chapterName;
                                    context.getSource().sendSuccess(() -> Component.translatable("blablalib.commands.updated_dialogue", finalChapterName).withStyle(ChatFormatting.GREEN), true);
                                    return 1;
                                })))
                .then(Commands.literal("reset").executes(context -> {
                    if(!context.getSource().isPlayer())
                    {
                        context.getSource().sendFailure(Component.translatable("blablalib.commands.must_be_player").withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    ServerPlayer player = context.getSource().getPlayerOrException();
                    BlablaLib.resetPlayerLastReadDialogue(player);
                    context.getSource().sendSuccess(() -> Component.translatable("blablalib.commands.reset_success").withStyle(ChatFormatting.GREEN), true);
                    return 1;
                }))
        );
    }
}
