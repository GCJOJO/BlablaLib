package io.github.gcjojo.blablalib.client;

import io.github.gcjojo.blablalib.client.models.ModelLoader;
import io.github.gcjojo.blablalib.client.models.NpcModelRegistry;
import io.github.gcjojo.liblib.LibLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NpcModelReloadListener implements ResourceManagerReloadListener {
    public static final String NPC_MODELS_PATH = "npc_models";

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        NpcModelRegistry.clearDynamicModels();

        Map<ResourceLocation, Resource> resources = resourceManager.listResources(NPC_MODELS_PATH, path -> path.getPath().endsWith(".geo.json"));

        for(var entry : resources.entrySet()) {
            ResourceLocation fileLocation = entry.getKey();
            try(InputStream stream = entry.getValue().open()){
                String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                ModelLoader.GeoFile geoFile = ModelLoader.loadModel(json);

                for(ModelLoader.Geometry geometry : geoFile.geometries()){
                    ResourceLocation geoId = ResourceLocation.tryParse(geometry.description().identifier());
                    NpcModelRegistry.register(geoId, geometry);
                }
            }
            catch (Exception e)
            {
                LibLib.printException("Error when loading npc model.", e);
            }
        }
    }
}
