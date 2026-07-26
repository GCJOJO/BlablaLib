package io.github.gcjojo.blablalib.client.models;

import io.github.gcjojo.blablalib.entities.NPC;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class NpcModelRegistry {
    private static final Map<ResourceLocation, ModelLoader.Geometry> geometries = new HashMap<>();
    private static final Map<ResourceLocation, ModelPart> bakedCache = new HashMap<>();
    private static final Map<ResourceLocation, EntityModel<NPC>> modelInstanceCache = new HashMap();

    public static void register(ResourceLocation geoId, ModelLoader.Geometry geometry){
        geometries.put(geoId, geometry);
        bakedCache.remove(geoId);
    }

    public static ModelPart getBakedModel(ResourceLocation id) {
        return bakedCache.computeIfAbsent(id, key -> {
            ModelLoader.Geometry geometry = geometries.get(id);
            if(geometry == null) return null;

            LayerDefinition layer = DynamicModelBuilder.build(geometry);
            return layer.bakeRoot();
        });
    }

    public static void clearDynamicModels(){
        geometries.clear();
        bakedCache.clear();
        modelInstanceCache.clear();
    }

    public static EntityModel<NPC> getOrCreateModelInstance(ResourceLocation modelId, ModelPart root) {
        return modelInstanceCache.computeIfAbsent(modelId, id -> new NPCModel(root));
    }
}
