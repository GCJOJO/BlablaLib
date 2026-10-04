package io.github.gcjojo.blablalib.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.gcjojo.blablalib.entities.NPC;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public class NPCModel extends EntityModel<NPC> {
    private final ModelPart root;

    public NPCModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public void setupAnim(NPC entity, float limbSwing, float limbSwingArm, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        root.render(poseStack, vertexConsumer, i, j);
    }
}
