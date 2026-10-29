package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.blablalib.dialogues.DialogueManager;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueFading extends DialogueAction
{
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueFading> FADE_STREAM_CODEC = StreamCodec.composite(
            LibLibNetwork.COLOR_STREAM_CODEC, DialogueFading::getFromColor,
            LibLibNetwork.COLOR_STREAM_CODEC, DialogueFading::getToColor,
            ByteBufCodecs.FLOAT, DialogueFading::getTime,
            DialogueFading::new
    );
    private static final DialogueManager.DialogueActionType<DialogueFading> TYPE = DialogueManager.registerActionType(
            ResourceLocation.fromNamespaceAndPath(BlablaLib.MOD_ID, "dialogue_fade"),
            FADE_STREAM_CODEC
    );
    @Getter
    private Color fromColor;

    private float partialTicks = 0;
    @Getter
    private Color toColor;
    @Getter
    private float time;

    public DialogueFading(String from, String to, float time) {
        this.fromColor = Color.TRANSPARENT;
        this.toColor = Color.TRANSPARENT;
        this.time = time;
        try {
            fromColor = Color.fromString(from, Color.TRANSPARENT);
            toColor = Color.fromString(to, Color.TRANSPARENT);
        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
        }
    }

    public DialogueFading(int from, int to, float time) {
        this.fromColor = new Color(from);
        this.toColor = new Color(to);
        this.time = time;
    }

    public DialogueFading(Color from, Color to, float time) {
        this.fromColor = from;
        this.toColor = to;
        this.time = time;
    }

    public DialogueFading(JsonObject object){
        try{
            if(object.has("from")) {
                String from = object.get("from").getAsString();
                fromColor = Color.fromString(from, Color.TRANSPARENT);
            }
            if(object.has("to")){
                String to = object.get("to").getAsString();
                toColor = Color.fromString(to, Color.TRANSPARENT);
            }
        } catch (Exception e) {
            BlablaLib.getLogger().error(e.getMessage());
        }

        if(object.has("time"))
            this.time = object.get("time").getAsFloat();
    }

    @Override
    public void step() {

    }

    // partialTicks = deltaTime ?
    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        partialTicks += partialTick;
        float percentage = MathUtils.clamp(partialTicks / time, 0.0f, 1.0f);

        Color lerpColor = MathUtils.lerp(fromColor, toColor, percentage);

        graphics.fill(0, 0, screen.width, screen.height, lerpColor.getColorInt());
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) { }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) { }

    @Override
    public boolean isBlocking() { return false; }

    @Override
    public boolean isSkippable() { return true; }

    @Override
    public DialogueManager.DialogueActionType<?> getType() { return TYPE; }
}
