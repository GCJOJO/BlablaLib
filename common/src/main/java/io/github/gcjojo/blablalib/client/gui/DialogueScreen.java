package io.github.gcjojo.blablalib.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueSpeaker;
import io.github.gcjojo.blablalib.dialogues.actions.*;
import io.github.gcjojo.blablalib.network.BlablaLibNetwork;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DialogueScreen extends Screen {

    private static Map<String, Class<? extends DialogueAction>> dialogueActionClasses = Map.ofEntries(
            Map.entry("clear",      DialogueClear.class),
            Map.entry("wait",       DialogueWait.class),
            Map.entry("choice",     DialogueChoice.class),
            Map.entry("change_set", DialogueNext.class),
            Map.entry("fade",       DialogueFading.class),
            Map.entry("message",    DialogueMessage.class),
            Map.entry("image",      DialogueImage.class),
            Map.entry("credit",     DialogueCredit.class),
            Map.entry("image_move", DialogueMoveImage.class),
            Map.entry("command",    DialogueExecuteCommand.class),
            Map.entry("sound",      DialogueSound.class)
    );

    private List<DialogueAction> dialogueActions;
    private int actionIndex = -1;
    private List<DialogueSpeaker> dialogueSpeakers;

    private String currentSet;

    private final List<DialogueAction> currentActions = new ArrayList<>();

    private boolean advanceDialogueAtTickEnd = false;
    private String queuedNextSet = null;

    private float currentFadingTime = -1.0f;
    private static final float endFade = 7.5f;

    private boolean guiVisible = true;

    private final List<Button> buttons = new ArrayList<>();

    public static void RegisterCustomAction(String name, Class<? extends DialogueAction> action) {
        dialogueActionClasses.put(name, action);
    }

    public DialogueScreen(String setName) {
        super(Component.literal("Dialogue"));
        this.currentSet = setName;
        var actions = loadSet(setName);
        if(actions == null || actions.isEmpty())
        {
            this.onClose();
            return;
        }
        this.dialogueActions = actions;

        var speakers = loadSpeakers(setName);
        if(speakers == null || speakers.isEmpty())
        {
            this.onClose();
            return;
        }
        this.dialogueSpeakers = speakers;
    }

    public DialogueScreen(String setName, List<DialogueAction> actions, List<DialogueSpeaker> speakers) {
        super(Component.literal("Dialogue"));
        this.currentSet = setName;
        this.dialogueActions = actions;
        this.dialogueSpeakers = speakers;
        if(!actions.isEmpty())
            NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_SCREEN_OPENED_PACKET_ID, new FriendlyByteBuf(Unpooled.buffer()));
    }

    @Override
    protected void init() {
        super.init();
        if(actionIndex == -1)
            advanceDialogue();

        buttons.forEach(this::addRenderableWidget);
    }

    public void drawButton(Button button)
    {
        this.addRenderableWidget(button);
        buttons.add(button);
    }

    public void handleChoiceSelection(String nextSet, String saveSet, String action) {
        CompoundTag choiceNbt = new CompoundTag();
        choiceNbt.putString("NextSet", nextSet);
        choiceNbt.putString("SaveSet", saveSet);
        choiceNbt.putString("Action", action);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeNbt(choiceNbt);
        NetworkManager.sendToServer(BlablaLibNetwork.CHOICE_PACKET_ID, buf);
        queueAdvanceDialogue();
    }

    public void changeSet(String setName) {
        List<DialogueAction> newActions = loadSet(setName);
        if(newActions == null) {
            this.onClose();
            return;
        }
        List<DialogueSpeaker> newSpeakers = loadSpeakers(setName);
        if(newSpeakers == null) {
            this.onClose();
            return;
        }

        currentSet = setName;
        actionIndex = -1;
        dialogueActions = newActions;
        dialogueSpeakers = newSpeakers;
        advanceDialogue();
    }

    public static List<DialogueAction> loadSet(String setPath) {
        try {
            String namespace = BlablaLib.MOD_ID;
            String setName = setPath;
            if(setPath.contains(":")) {
                namespace = setPath.split(":")[0];
                setName = setPath.split(":")[1];
            }

            ResourceLocation res = ResourceLocation.tryBuild(namespace, "dialogues.json");
            var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resourceOpt.isPresent()) {
                JsonObject root = new Gson().fromJson(new InputStreamReader(resourceOpt.get().open()), JsonObject.class);
                if (root.has(setName)) {
                    List<DialogueAction> actions = new ArrayList<>();
                    root.getAsJsonArray(setName).forEach(element -> {
                        JsonObject obj = element.getAsJsonObject();
                        if(!obj.has("action"))
                            return;

                        String action = obj.get("action").getAsString();
                        /*if(dialogueActionClasses.containsKey(action)) {
                            try {
                                actions.add(dialogueActionClasses.get(action).getDeclaredConstructor().newInstance(obj));
                                return;
                            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                                     InvocationTargetException e) {
                                BlablaLib.getLogger().error(e.getMessage());
                                Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
                            }
                        }
                        actions.add(new DialogueRawAction(obj));*/


                        switch(action)
                        {
                            case "clear" -> actions.add(new DialogueClear(obj));
                            case "wait" -> actions.add(new DialogueWait(obj));
                            case "choice" -> actions.add(new DialogueChoice(obj));
                            case "change_set" -> actions.add(new DialogueNext(obj.get("set").getAsString()));
                            case "fade" -> actions.add(new DialogueFading(obj));
                            case "message" -> actions.add(new DialogueMessage(obj.get("speaker").getAsInt(), Component.translatable(obj.get("text").getAsString()).getString()));
                            case "image" -> actions.add(new DialogueImage(obj));
                            case "credit" -> actions.add(new DialogueCredit(obj));
                            case "image_move" -> actions.add(new DialogueMoveImage(obj));
                            case "command" -> actions.add(new DialogueExecuteCommand(obj));
                            case "sound" -> actions.add(new DialogueSound(obj));
                            default -> actions.add(new DialogueRawAction(obj));
                        }

                    });
                    return actions;
                }
            }
        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> BlablaLib.getLogger().error(stackTraceElement.toString()));
            return null;
        }
        return null;
    }

    public static List<DialogueSpeaker> loadSpeakers(String setPath){
        try {
            String namespace = BlablaLib.MOD_ID;
            if (setPath.contains(":"))
                namespace = setPath.split(":")[0];

            ResourceLocation res = ResourceLocation.tryBuild(namespace, "dialogues.json");
            var resourceOpt = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resourceOpt.isPresent()) {
                JsonObject root = new Gson().fromJson(new InputStreamReader(resourceOpt.get().open()), JsonObject.class);
                List<DialogueSpeaker> speakers = new ArrayList<>();
                if(!root.has("speakers"))
                    return speakers;

                root.getAsJsonArray("speakers").forEach(element -> {
                    JsonObject obj = element.getAsJsonObject();
                    speakers.add(new DialogueSpeaker(obj));
                });

                return speakers;
            }
        } catch (Exception e) {
            BlablaLib.getLogger().warn("Oopsie cannot load speakers !");
        }

        return null;
    }

    public static void openForSet(String setName) {
        List<DialogueAction> actions = loadSet(setName);
        List<DialogueSpeaker> speakers = loadSpeakers(setName);
        if(actions != null && !actions.isEmpty() && speakers != null)
            Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new DialogueScreen(setName, actions, speakers)));
    }

    @Override
    public void tick() {
        if(actionIndex >= dialogueActions.size())
            return;

        currentActions.forEach(DialogueAction::step);

        if(advanceDialogueAtTickEnd) {
            this.advanceDialogueAtTickEnd = false;
            this.advanceDialogue();
        }

        if(queuedNextSet != null){
            changeSet(queuedNextSet);
            queuedNextSet = null;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(this.currentFadingTime >= 0.0){
            if(this.currentFadingTime >= endFade || !this.guiVisible){
                this.onClose();
                return;
            }

            float fadePercentage = currentFadingTime / endFade;

            int topColor = Mth.lerpInt(fadePercentage, 0x11, 0x00);
            int bottomColor = Mth.lerpInt(fadePercentage, 0xDD, 0x00);
            int topPoint = Mth.lerpInt(fadePercentage, 0, (int)(this.height * 0.5));

            graphics.fillGradient(0, topPoint, this.width, this.height,
                    FastColor.ARGB32.color(topColor, 0, 0, 0), FastColor.ARGB32.color(bottomColor, 0, 0, 0));
            currentFadingTime += partialTick;
            return;
        }

        if(actionIndex >= dialogueActions.size())
            return;
        if(guiVisible)
            graphics.fillGradient(0, 0, this.width, this.height, 0x11000000, 0xDD000000);

        currentActions.forEach(action -> action.draw(graphics, mouseX, mouseY, partialTick));
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(actionIndex < dialogueActions.size()) {
            currentActions.forEach(dialogueAction -> dialogueAction.mouseClicked(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 256) {
            skipDialogue();
            return true;
        }

        if(actionIndex < dialogueActions.size()) {
            currentActions.forEach(dialogueAction -> dialogueAction.keyPressed(keyCode, scanCode, modifiers));
        }

        return false;
    }

    public void queueAdvanceDialogue() { this.advanceDialogueAtTickEnd = true; }

    public void queueChangeSet(String nextSet) { this.queuedNextSet = nextSet; }

    public void advanceDialogue() {
        actionIndex++;
        buttons.forEach(button -> button.visible = false);
        buttons.clear();
        if (dialogueActions.isEmpty() || actionIndex >= dialogueActions.size()) {
            endDialogue();
            return;
        }

        currentActions.removeIf(DialogueAction::isBlocking);

        for(int i = actionIndex; i <= dialogueActions.size(); i++)
        {
            DialogueAction currentAction = dialogueActions.get(i);

            if(currentAction instanceof DialogueRawAction){
                switch(((DialogueRawAction) currentAction).getRawAction()){
                    case "hide_ui" -> hideGui();
                    case "show_ui" -> showGui();
                }
                continue;
            }
            currentActions.add(currentAction);

            currentAction.setup(this);
            if(currentAction.isBlocking())
            {
                actionIndex = i;
                break;
            }
        }
    }

    public List<DialogueSpeaker> getDialogueSpeakers() { return this.dialogueSpeakers; }

    public DialogueSpeaker getDialogueSpeaker(int id){
        if (dialogueSpeakers.isEmpty())
            return DialogueSpeaker.DEFAULT_SPEAKER;
        for(var speaker : this.dialogueSpeakers)
            if(speaker.getId() == id) return speaker;
        return DialogueSpeaker.DEFAULT_SPEAKER;
    }

    public void clearActions() { currentActions.clear(); }

    public void clearActions(String clearedClass){
        Class<? extends DialogueAction> classToRemove = null;

        switch (clearedClass)
        {
            case "message" -> classToRemove = DialogueMessage.class;
            case "credit" -> classToRemove = DialogueCredit.class;
            case "fade" -> classToRemove = DialogueFading.class;
            case "image" -> classToRemove = DialogueImage.class;
            case "move_image" -> classToRemove = DialogueMoveImage.class;
        }
        if(classToRemove != null)
            currentActions.removeIf(classToRemove::isInstance);
    }

    public void showGui() { this.guiVisible = true; }
    public void hideGui() { this.guiVisible = false; }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public boolean isPauseScreen() { return false; }

    public void skipDialogue(){
        while(actionIndex < dialogueActions.size() && dialogueActions.get(actionIndex).isSkippable())
            advanceDialogue();
    }

    public void endDialogue(){
        clearActions();
        currentFadingTime = 0.0f;

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(currentSet);
        NetworkManager.sendToServer(BlablaLibNetwork.DIALOGUE_COMPLETED_PACKET_ID, buf);
    }

    public List<DialogueAction> getCurrentActions() {
        return this.currentActions;
    }
}