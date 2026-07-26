package io.github.gcjojo.blablalib.fabric.client;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.NpcModelReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class FabricNpcModelReloadListener extends NpcModelReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.tryBuild(BlablaLib.MOD_ID, "npc_model_reload_listener");
    }
}
