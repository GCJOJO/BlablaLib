package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.client.gui.DialogueScreen;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueSpeaker;
import io.github.gcjojo.liblib.client.gui.elements.GuiRichText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec2;

import java.util.List;
import java.util.Random;

public class DialogueMessage extends DialogueAction
{
    private static final Font font = Minecraft.getInstance().font;
    private final int speakerId;
    private final String dialogueLine;
    private final Random random = new Random();
    GuiRichText richText;
    private int charIndex = 0;
    private int tickCount = 0;
    private int characterTimer = 0;
    private int pauseTimer = 0;

    public DialogueMessage(int speakerId, String dialogueLine) {
        this.speakerId = speakerId;
        this.dialogueLine = dialogueLine;
    }

    public DialogueMessage(JsonObject object) {
        this.speakerId = object.has("speaker") ? object.get("speaker").getAsInt() : Integer.MAX_VALUE;
        this.dialogueLine = object.has("text") ? Component.translatable(object.get("text").getAsString()).getString() : "";
    }

    @Override
    public void setup(DialogueScreen screen) {
        super.setup(screen);
        richText = new GuiRichText(screen, Component.literal(dialogueLine));

        screen.addElement(richText);
        richText.setDrawnCharacters(0);

        if(this.dialogueLine.isEmpty())
            screen.queueAdvanceDialogue();
    }

    @Override
    public void step() {
        tickCount++;

        if (pauseTimer > 0) {
            pauseTimer--;
            return;
        }

        if(++characterTimer < screen.getDialogueSpeed()) return;
        if (charIndex >= dialogueLine.length()) return;

        characterTimer = 0;
        charIndex++;
        richText.setDrawnCharacters(charIndex);
        List<SoundEvent> sounds = screen.getDialogueSpeaker(this.speakerId).getSounds();
        if(!sounds.isEmpty())
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sounds.get(random.nextInt(sounds.size())), 1.0F));

        char currentChar = richText.getLastDrawCharacter();
        if (currentChar == '.' || currentChar == '!' || currentChar == '?') {
            pauseTimer = characterTimer * screen.getWaitMultiplier();
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int boxWidth = 300;
        int boxHeight = 80;
        int boxX = (screen.width - boxWidth) / 2;
        int boxY = screen.height - boxHeight - 20;

        DialogueSpeaker speaker = screen.getDialogueSpeaker(this.speakerId);

        graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x80000000);
        String speakerTranslated = Component.translatable(speaker.getName()).getString();
        graphics.drawString(font, speakerTranslated, boxX + 10, boxY + 5, speaker.getColor(), false);

        if (dialogueLine != null) {
            //String displayedText = dialogueLine.substring(0, charIndex);
            //graphics.drawWordWrap(font, Component.literal(displayedText), boxX + 10, boxY + 20, boxWidth - 20, 0xFFFFFF);
            richText.setWidth(boxWidth - 30);
            richText.setPosition(new Vec2(boxX + 10, boxY + 20));

            if (charIndex >= dialogueLine.length() && (tickCount % 20 < 10)) {
                graphics.drawString(font, "▼", boxX + boxWidth - 15, boxY + boxHeight - 15, 0xFFFFFF, false);
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if(charIndex >= dialogueLine.length())
            screen.queueAdvanceDialogue();
        else {
            charIndex = dialogueLine.length();
            richText.setDrawnCharacters(charIndex);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode != 257 && keyCode != 32)
            return;

        if(charIndex >= dialogueLine.length())
            screen.queueAdvanceDialogue();
        else {
            charIndex = dialogueLine.length();
            richText.setDrawnCharacters(charIndex);
        }
    }

    @Override
    public boolean isBlocking() { return true; }

    @Override
    public boolean isSkippable() { return true; }
}
