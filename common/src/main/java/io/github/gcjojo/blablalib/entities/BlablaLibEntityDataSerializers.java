package io.github.gcjojo.blablalib.entities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.jetbrains.annotations.NotNull;

public class BlablaLibEntityDataSerializers {

    public static final EntityDataSerializer<NPCDefinition> NPC_DEFINITION =
            new EntityDataSerializer<NPCDefinition>() {
                @Override
                public void write(FriendlyByteBuf friendlyByteBuf, NPCDefinition object) {
                    object.write(friendlyByteBuf);
                }

                @Override
                public @NotNull NPCDefinition read(FriendlyByteBuf friendlyByteBuf) {
                    return NPCDefinition.read(friendlyByteBuf);
                }

                @Override
                public @NotNull NPCDefinition copy(NPCDefinition object) {
                    return NPCDefinition.copy(object);
                }
            };

    public static void register() {
        EntityDataSerializers.registerSerializer(NPC_DEFINITION);
    }
}
