package io.github.gcjojo.blablalib.entities;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlablaLibEntityDataSerializers {

    @ExpectPlatform
    public static EntityDataSerializer<NPCDefinition> npcDefinition() {
        throw new AssertionError();
    }

    public static void init() {}
}
