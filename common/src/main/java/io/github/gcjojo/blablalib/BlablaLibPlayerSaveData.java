package io.github.gcjojo.blablalib;

import io.github.gcjojo.liblib.utils.PlayerSaveData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class BlablaLibPlayerSaveData extends PlayerSaveData {
    protected boolean isInDialogue = false;
    protected ResourceLocation currentDialogue = null;
    protected ResourceLocation lastReadDialogue = null;

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("IsInDialogue", isInDialogue);
        nbt.putString("CurrentDialogue", currentDialogue != null ? currentDialogue.toString() : "");
        nbt.putString("LastReadDialogue", lastReadDialogue != null ? lastReadDialogue.toString() : "");
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        if (nbt.contains("IsInDialogue"))
            this.isInDialogue = nbt.getBoolean("IsInDialogue");
        if (nbt.contains("CurrentDialogue"))
            this.currentDialogue = ResourceLocation.tryParse(nbt.getString("CurrentDialogue"));
        if (nbt.contains("LastReadDialogue"))
            this.lastReadDialogue = ResourceLocation.tryParse(nbt.getString("LastReadDialogue"));
    }
}
