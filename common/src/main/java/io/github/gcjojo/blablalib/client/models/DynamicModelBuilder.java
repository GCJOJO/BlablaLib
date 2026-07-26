package io.github.gcjojo.blablalib.client.models;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.*;

public class DynamicModelBuilder {

    public static LayerDefinition build(ModelLoader.Geometry geo) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        Map<String, PartDefinition> created = new HashMap<>();

        List<ModelLoader.GeoBone> ordered = topologicalSort(geo.bones());

        for(ModelLoader.GeoBone bone : ordered) {
            PartDefinition parent = bone.parent() == null ? root : created.get(bone.parent());
            if(parent == null)
                throw new IllegalStateException(String.format("Invalid Parent Bone %s", bone.parent()));


            float[] bonePivot = bone.pivot() != null ? bone.pivot() : new float[]{0, 0, 0};
            PartPose bonePose = toPartPose(bonePivot, bone.rotation());

            CubeListBuilder simpleCubeBuilder = CubeListBuilder.create();
            int subBoneIndex = 0;

            for(ModelLoader.GeoCube cube : bone.cubes()){
                if(cube.pivot() != null || cube.rotation() != null) {
                    // Rotated Cube
                    String subBoneName = bone.name() + "_cube" + (subBoneIndex++);
                    CubeListBuilder subBuilder = CubeListBuilder.create()
                            .texOffs(cube.uv()[0], cube.uv()[1])
                            .addBox(
                                    cube.origin()[0] - bonePivot[0], cube.origin()[1] - bonePivot[1], cube.origin()[2] - bonePivot[2],
                                    cube.size()[0], cube.size()[1], cube.size()[2]
                            );

                    float[] cubePivot = cube.pivot() != null ? cube.pivot() : bonePivot;
                    PartPose subPose = toPartPose(
                            new float[]{ cubePivot[0] - bonePivot[0], cubePivot[1] - bonePivot[1], cubePivot[2] - bonePivot[2] },
                            cube.rotation()
                    );

                    parent.addOrReplaceChild(subBoneName, subBuilder, subPose);

                    continue;
                }

                simpleCubeBuilder.texOffs(cube.uv()[0], cube.uv()[1])
                        .addBox(
                                cube.origin()[0] - bonePivot[0],cube.origin()[1] - bonePivot[1],cube.origin()[2] - bonePivot[2],
                                cube.size()[0], cube.size()[1], cube.size()[2]
                        );
            }

            PartDefinition partDef = parent.addOrReplaceChild(bone.name(), simpleCubeBuilder, bonePose);
            created.put(bone.name(), partDef);
        }

        return LayerDefinition.create(mesh, geo.description().textureWidth(), geo.description().textureHeight());
    }

    private static List<ModelLoader.GeoBone> topologicalSort(List<ModelLoader.GeoBone> bones) {
        List<ModelLoader.GeoBone> ordered = new ArrayList<>();
        Set<String> resolved = new HashSet<>();
        resolved.add(null);

        List<ModelLoader.GeoBone> remaining = new ArrayList<>(bones);
        while(!remaining.isEmpty()) {
            Iterator<ModelLoader.GeoBone> it = remaining.iterator();
            boolean progress = false;
            while(it.hasNext() && !progress){
                ModelLoader.GeoBone bone = it.next();
                if(!resolved.contains(bone.parent()))
                    continue;

                ordered.add(bone);
                resolved.add(bone.name());
                it.remove();
                progress = true;
            }

            if(!progress)
                throw new IllegalStateException("Invalid Bone Hierarchy (either cyclic or a parent bone is missing)");

        }

        return ordered;
    }

    private static PartPose toPartPose(float[] pivot, float[] rotationDegrees) {
        if (rotationDegrees == null) {
            return PartPose.offset(pivot[0], pivot[1], pivot[2]);
        }
        return PartPose.offsetAndRotation(
                pivot[0], pivot[1], pivot[2],
                (float) Math.toRadians(rotationDegrees[0]),
                (float) Math.toRadians(rotationDegrees[1]),
                (float) Math.toRadians(rotationDegrees[2])
        );
    }
}
