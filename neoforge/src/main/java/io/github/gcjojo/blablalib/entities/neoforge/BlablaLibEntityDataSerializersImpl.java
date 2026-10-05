package io.github.gcjojo.blablalib.entities.neoforge;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.entities.NPCDefinition;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlablaLibEntityDataSerializersImpl {

    public static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, BlablaLib.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<NPCDefinition>> NPC_SERIALIZER = SERIALIZERS.register(
      "npc", () -> EntityDataSerializer.forValueType(NPCDefinition.STREAM_CODEC)
    );

    public static EntityDataSerializer<NPCDefinition> npcDefinition(){
        return NPC_SERIALIZER.get();
    }

}
