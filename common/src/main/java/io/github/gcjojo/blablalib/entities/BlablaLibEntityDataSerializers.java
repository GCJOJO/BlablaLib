package io.github.gcjojo.blablalib.entities;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.gcjojo.blablalib.npc.NPCVisualState;
import net.minecraft.network.syncher.EntityDataSerializer;

public class BlablaLibEntityDataSerializers {

    @ExpectPlatform
    public static EntityDataSerializer<NPCVisualState> npcDefinition() {
        throw new AssertionError();
    }

    public static void init() {}
}
