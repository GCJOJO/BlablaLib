package io.github.gcjojo.blablalib.entities;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.jetbrains.annotations.NotNull;

public class BlablaLibEntityDataSerializers {

    public static final EntityDataSerializer<NPCDefinition> NPC_DEFINITION =
            new EntityDataSerializer<NPCDefinition>() {
                public static final StreamCodec<RegistryFriendlyByteBuf, NPCDefinition> STREAM_CODEC = StreamCodec.of(
                    (buf, def) -> def.write(buf),
                    NPCDefinition::read
                );

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, NPCDefinition> codec() {
                    return STREAM_CODEC;
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
