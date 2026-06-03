package io.github.gcjojo.blablalib.fabric.data_components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.resources.ResourceLocation;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerDataComponent> PLAYER_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new ResourceLocation(BlablaLib.MOD_ID, "player_data"),
                    PlayerDataComponent.class
            );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PLAYER_DATA, player -> new PlayerDataComponent(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
