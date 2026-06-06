package io.github.gcjojo.blablalib.fabric.data_components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class PlayerDataComponent implements ComponentV3 {
    private boolean isInDialogue = false;
    private ResourceLocation currentDialogue = ResourceLocation.tryBuild(BlablaLib.MOD_ID, "dogcheck");
    private ResourceLocation lastReadDialogue;

    private CompoundTag additionalData = new CompoundTag();

    public boolean getIsInDialogue() { return this.isInDialogue; }
    public void setIsInDialogue(boolean value) { this.isInDialogue = value; }

    public ResourceLocation getCurrentDialogue() { return this.currentDialogue; }
    public void setCurrentDialogue(ResourceLocation value) { this.currentDialogue = value; }
    public ResourceLocation getLastReadDialogue() { return this.lastReadDialogue; }
    public void setLastReadDialogue(ResourceLocation value) { this.lastReadDialogue = value; }

    public CompoundTag getAdditionalData() { return this.additionalData; }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.isInDialogue = tag.getBoolean("IsInDialogue");
        this.currentDialogue = ResourceLocation.tryParse(tag.getString("CurrentDialogue"));
        this.lastReadDialogue = ResourceLocation.tryParse(tag.getString("LastReadDialogue"));
        this.additionalData = tag.getCompound("AdditionalData");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("IsInDialogue", this.isInDialogue);
        tag.putString("CurrentDialogue", this.currentDialogue.toString());
        tag.putString("LastReadDialogue", this.currentDialogue.toString());
        tag.put("AdditionalData", this.additionalData);
    }
}
