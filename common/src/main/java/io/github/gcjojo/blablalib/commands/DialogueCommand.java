package io.github.gcjojo.blablalib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DialogueCommand {

    private static Map<UUID, List<ResourceLocation>> playerDialogueLists = new HashMap<>();
    private static List<DialogueTransformation> transformations = new ArrayList<>();

    public static void registerPlayerDialogueList(ServerPlayer player, List<ResourceLocation> dialogueList) {
        if(playerDialogueLists.containsKey(player.getUUID()))
            return;
        playerDialogueLists.put(player.getUUID(), dialogueList);
    }

    public static void disconnectPlayer(ServerPlayer player) {
        if(playerDialogueLists.containsKey(player.getUUID()))
            playerDialogueLists.remove(player.getUUID());
    }

    private static CompletableFuture<Suggestions> suggestDialogues(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ServerPlayer player = context.getSource().getPlayer();
        if(playerDialogueLists.containsKey(player.getUUID()))
            playerDialogueLists.get(player.getUUID()).forEach(dialoguePath -> builder.suggest(dialoguePath.toString()));
        return builder.buildFuture();
    }

    public static void registerTransformation(DialogueTransformation transformation){
        transformations.add(transformation);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("dialogue")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("play")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ResourceLocation currentDialogue = BlablaLib.getPlayerDialogue(player);
                            if(currentDialogue != null) {
                                BlablaLib.openDialogue(player, currentDialogue);
                                return 1;
                            }

                            player.sendSystemMessage(Component.translatable("blablalib.commands.invalid_dialogue", currentDialogue).withStyle(ChatFormatting.RED));
                            return 0;
                        }))
                .then(Commands.literal("set")
                        .then(Commands.argument("dialogue", ResourceLocationArgument.id())
                                .suggests(DialogueCommand::suggestDialogues)
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ResourceLocation dialogue = ResourceLocationArgument.getId(context, "dialogue");

                                    AtomicReference<ResourceLocation> finalDialogue = new AtomicReference<>(dialogue);
                                    transformations.forEach(transformation -> finalDialogue.set(transformation.transformDialogue(context, finalDialogue.get())));

                                    BlablaLib.setPlayerDialogue(player, finalDialogue.get());
                                    String dialogueString = finalDialogue.get().toString();
                                    context.getSource().sendSuccess(() -> Component.translatable("blablalib.commands.updated_dialogue", dialogueString).withStyle(ChatFormatting.GREEN), true);
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
                    BlablaLib.setPlayerIsInDialogue(player, false);
                    context.getSource().sendSuccess(() -> Component.translatable("blablalib.commands.reset_success").withStyle(ChatFormatting.GREEN), true);
                    return 1;
                }))
        );
    }

    public interface DialogueTransformation {
        ResourceLocation transformDialogue(CommandContext<CommandSourceStack> context, ResourceLocation dialogue);
    }
}
