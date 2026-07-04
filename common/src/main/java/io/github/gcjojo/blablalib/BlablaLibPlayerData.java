package io.github.gcjojo.blablalib;

import io.github.gcjojo.liblib.utils.PlayerData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class BlablaLibPlayerData extends PlayerData {
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
    public void deserialize(CompoundTag data) {
        if(data.contains("IsInDialogue"))
            this.isInDialogue = data.getBoolean("IsInDialogue");
        if(data.contains("CurrentDialogue"))
            this.currentDialogue = ResourceLocation.tryParse(data.getString("CurrentDialogue"));
        if(data.contains("LastReadDialogue"))
            this.lastReadDialogue = ResourceLocation.tryParse(data.getString("LastReadDialogue"));
    }
}
