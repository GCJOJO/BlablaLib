package io.github.gcjojo.blablalib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gcjojo.blablalib.client.models.NPCModel;
import io.github.gcjojo.blablalib.client.models.NpcModelRegistry;
import io.github.gcjojo.blablalib.entities.NPC;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class NPCRenderer extends LivingEntityRenderer<NPC, EntityModel<NPC>> {
    private static final ModelPart FALLBACK_ROOT = buildFallbackRoot();
    private static final EntityModel<NPC> FALLBACK_MODEL = new NPCModel(FALLBACK_ROOT);

    public NPCRenderer(EntityRendererProvider.Context context) {
        super(context, FALLBACK_MODEL, 0.5f);
    }

    private static ModelPart buildFallbackRoot() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("cube",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4, -8, -4, 8, 16, 8), PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16).bakeRoot();
    }

    @Override
    public void render(NPC entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight){
        this.model = resolveModel(entity);
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private EntityModel<NPC> resolveModel(NPC entity) {
        ResourceLocation modelId = ResourceLocation.tryBuild(entity.getModelNamespace(), entity.getModelPath());
        ModelPart root = NpcModelRegistry.getBakedModel(modelId);

        if(root != null) return NpcModelRegistry.getOrCreateModelInstance(modelId, root);

        return FALLBACK_MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(NPC entity) {
        ResourceLocation resLoc = ResourceLocation.tryBuild(entity.getTextureNamespace(), entity.getTexturePath());
        if(resLoc != null) return resLoc;

        return MissingTextureAtlasSprite.getLocation();
    }
}
