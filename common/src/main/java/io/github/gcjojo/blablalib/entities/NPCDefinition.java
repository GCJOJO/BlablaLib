package io.github.gcjojo.blablalib.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public record NPCDefinition(
        @SerializedName("model") String modelLocation,
        @SerializedName("texture") String textureLocation,
        @SerializedName("animation") String animation,
        @SerializedName("dialogue") String dialogue
) {
    private static final Gson GSON = new GsonBuilder().create();
    public static NPCDefinition EMPTY = new NPCDefinition("", "", "", "");

    public static NPCDefinition fromCompoundTag(CompoundTag nbt){
        String model = "";
        String texture = "";
        String animation = "";
        String dialogue = "";

        if(nbt.contains("Model"))
            model = nbt.getString("Model");
        if(nbt.contains("Texture"))
            texture = nbt.getString("Texture");
        if(nbt.contains("Animation"))
            animation = nbt.getString("Animation");
        if(nbt.contains("Dialogue"))
            dialogue = nbt.getString("Dialogue");

        return new NPCDefinition(model, texture, animation, dialogue);
    }

    public static NPCDefinition loadDefinition(String input){
        return GSON.fromJson(input, NPCDefinition.class);
    }

    public static NPCDefinition read(FriendlyByteBuf buf) {
        return new NPCDefinition(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf());
    }

    public static NPCDefinition copy(NPCDefinition definition){
        return new NPCDefinition(definition.modelLocation, definition.textureLocation, definition.animation, definition.dialogue);
    }

    public NPCDefinition withModel(String model){
        return new NPCDefinition(model, this.textureLocation, this.animation, this.dialogue);
    }

    public NPCDefinition withTexture(String texture){
        return new NPCDefinition(this.modelLocation, texture, this.animation, this.dialogue);
    }

    public NPCDefinition withAnimation(String animation){
        return new NPCDefinition(this.modelLocation, this.textureLocation, animation, this.dialogue);
    }

    public NPCDefinition withDialogue(String dialogue){
        return new NPCDefinition(this.modelLocation, this.textureLocation, this.animation, dialogue);
    }

    public CompoundTag toCompoundTag(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Model", modelLocation);
        nbt.putString("Texture", textureLocation);
        nbt.putString("Animation", animation);
        nbt.putString("Dialogue", dialogue);
        return nbt;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modelLocation);
        buf.writeUtf(textureLocation);
        buf.writeUtf(animation);
        buf.writeUtf(dialogue);
    }
}
