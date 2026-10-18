package io.github.gcjojo.blablalib.dialogues.actions;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.dialogues.DialogueAction;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec2;

public class DialogueText extends DialogueAction {
    @Getter
    private String text;
    @Getter
    private TextTimings timings;
    @Getter
    private TextTransforms transforms;
    /*private float fadeInTime;
    private float holdTime;
    private float fadeOutTime;
    private float xPercentage;
    private float yPercentage;
    private float scale;*/
    private Color color;

    public DialogueText(String text, float fadeInTime, float holdTime, float fadeOutTime, float xPercentage, float yPercentage, float scale, String color){
        this.text = text;
        this.fadeInTime = fadeInTime;
        this.holdTime = holdTime;
        this.fadeOutTime = fadeOutTime;
        this.xPercentage = xPercentage;
        this.yPercentage = yPercentage;
        this.scale = scale;

        try {
            this.color = Color.fromString(color);
        } catch (Exception e) { BlablaLib.getLogger().error(e.getMessage()); }

        this.alignment = TextAlignment.CENTER;
        this.alpha = 0;
        state = DisplayState.FADE_IN;
    }
    public DialogueText(JsonObject object){
        this.text = "";
        this.fadeInTime = 0.0f;
        this.holdTime = 1.0f;
        this.fadeOutTime = 0.0f;
        this.xPercentage = 0.0f;
        this.yPercentage = 0.0f;
        this.scale = 1.0f;
        this.color = Color.WHITE;
        this.alignment = TextAlignment.CENTER;

        this.alpha = 0;
        this.state = DisplayState.FADE_IN;

        if(object.has("text"))
            this.text = object.get("text").getAsString();
        if(object.has("fade_in_time"))
            this.fadeInTime = object.get("fade_in_time").getAsFloat();
        if(object.has("hold_time"))
            this.holdTime = object.get("hold_time").getAsFloat();
        if(object.has("fade_out_time"))
            this.fadeOutTime = object.get("fade_out_time").getAsFloat();
        if(object.has("x"))
            this.xPercentage = object.get("x").getAsFloat();
        if(object.has("y"))
            this.yPercentage = object.get("y").getAsFloat();
        if(object.has("scale"))
            this.scale = object.get("scale").getAsFloat();
        if(object.has("color")) {
            String color = object.get("color").getAsString();
            try {
                this.color = Color.fromString(color);
            } catch (Exception e) { BlablaLib.getLogger().error(e.getMessage()); }
        }

        if(object.has("alignment"))
        {
            switch(object.get("alignment").getAsString())
            {
                case "center" -> this.alignment = TextAlignment.CENTER;
                case "right" -> this.alignment = TextAlignment.RIGHT;
                default -> this.alignment = TextAlignment.LEFT;
            }
        }
    }
    enum DisplayState {
        FADE_IN,
        HOLD,
        FADE_OUT,
        TRANSPARENT
    }
    enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
    private TextAlignment alignment;


    private DisplayState state;
    private static final Font font = Minecraft.getInstance().font;

    private int alpha = 0;
    private float currentTime = 0.0f;

    public record TextTimings(float fadeInTime, float holdTime, float fadeOutTime) {
        public static final StreamCodec<RegistryFriendlyByteBuf, TextTimings> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, TextTimings::fadeInTime,
                ByteBufCodecs.FLOAT, TextTimings::holdTime,
                ByteBufCodecs.FLOAT, TextTimings::fadeOutTime,
                TextTimings::new
        );

        public TextTimings withFadeInTime(float newFadeInTime) { return new TextTimings(newFadeInTime, holdTime(), fadeOutTime()); }
        public TextTimings withHoldTime(float newHoldTime) { return new TextTimings(fadeInTime(), newHoldTime, fadeOutTime()); }
        public TextTimings withFadeOutTime(float newFadeOutTime) { return new TextTimings(fadeInTime(), holdTime(), newFadeOutTime); }
    }

    public record TextTransforms(Vec2 position, float rotation, float scale) {
        public static final StreamCodec<RegistryFriendlyByteBuf, TextTransforms> STREAM_CODEC = StreamCodec.composite(
                LibLibNetwork.VEC2_STREAM_CODEC, TextTransforms::position,
                ByteBufCodecs.FLOAT, TextTransforms::rotation,
                ByteBufCodecs.FLOAT, TextTransforms::scale,
                TextTransforms::new
        );

        public TextTransforms withX(float newX) { return new TextTransforms(new Vec2(newX, position().y), rotation(), scale()); }
        public TextTransforms withY(float newY) { return new TextTransforms(new Vec2(position().x, newY), rotation(), scale()); }
        public TextTransforms withPosition(Vec2 newPos) { return new TextTransforms(newPos, rotation(), scale()); }
        public TextTransforms withRotation(float newRotation) { return new TextTransforms(position(), newRotation, scale()); }
        public TextTransforms withScale(float newScale) { return new TextTransforms(position(), rotation(), newScale); }
    }

    @Override
    public void step() {
    }

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(state == DisplayState.TRANSPARENT)
            return;

        this.alpha = 0;
        RenderSystem.enableBlend();
        PoseStack pose = graphics.pose();
        pose.pushPose();
        switch (state)
        {
            case FADE_IN -> {
                float percentage = MathUtils.clamp(currentTime / fadeInTime, 0.00f, 1.0f);
                this.alpha = (int)(percentage * 255);
                if(currentTime >= fadeInTime) {
                    currentTime = 0.0f;
                    state = DisplayState.HOLD;
                }
            }
            case HOLD -> {
                this.alpha = 255;
                if(currentTime >= holdTime) {
                    currentTime = 0.0f;
                    state = DisplayState.FADE_OUT;
                }
            }
            case FADE_OUT -> {
                float percentage = MathUtils.clamp(currentTime / fadeOutTime, 0.0f, 1.0f);
                this.alpha = 255 - (int)(percentage * 255);
                if (currentTime >= fadeOutTime)
                {
                    currentTime = 0.0f;
                    state = DisplayState.TRANSPARENT;
                    this.alpha = 0;
                }
            }
        }

        if(this.alpha >= 8)
        {
            MutableComponent textComponent = Component.translatable(this.text);

            int color = FastColor.ARGB32.color(this.alpha, this.color.getX(), this.color.getY(), this.color.getZ());

            //ModEntry.getLogger().info("Alpha : %d, Red : %d, Green : %d, Blue : %d".formatted(this.alpha, this.color.getX(), this.color.getY(), this.color.getZ()));

            int xPos;
            switch(this.alignment)
            {
                case LEFT       -> xPos = (int)(screen.width * (this.xPercentage * 0.01f));
                case RIGHT      -> xPos = (int)(screen.width * (this.xPercentage * 0.01f) - (font.width(textComponent) * scale));
                default         -> xPos = (int)(screen.width * (this.xPercentage * 0.01f) - (font.width(textComponent) * scale * 0.5f));    // CENTER ALIGNMENT
            }

            int yPos = (int)(screen.height * (this.yPercentage * 0.01f));

            pose.translate(xPos, yPos, 1.0f);
            pose.scale(this.scale, this.scale, 1.0f);
            graphics.drawString(font, textComponent, 0, 0, color);
        }
        pose.popPose();

        currentTime += partialTick;
        RenderSystem.disableBlend();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isSkippable() { return true; }
}
