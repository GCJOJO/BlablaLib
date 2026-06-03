package io.github.gcjojo.blablalib.fabric.data_components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;

public class PlayerDataComponent implements ComponentV3 {
    private boolean isInDialogue = false;
    private String currentDialogue = "";
    private String lastReadDialogue = "";

    public boolean getIsInDialogue() { return this.isInDialogue; }
    public void setIsInDialogue(boolean value) { this.isInDialogue = value; }

    public String getCurrentDialogue() { return this.currentDialogue; }
    public void setCurrentDialogue(String value) { this.currentDialogue = value; }
    public String getLastReadDialogue() { return this.lastReadDialogue; }
    public void setLastReadDialogue(String value) { this.lastReadDialogue = value; }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.isInDialogue = tag.getBoolean("IsInDialogue");
        this.currentDialogue = tag.getString("CurrentDialogue");
        this.lastReadDialogue = tag.getString("LastReadDialogue");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("IsInDialogue", this.isInDialogue);
        tag.putString("CurrentDialogue", this.currentDialogue);
        tag.putString("LastReadDialogue", this.currentDialogue);
    }
}
