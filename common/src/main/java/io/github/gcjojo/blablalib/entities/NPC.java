package io.github.gcjojo.blablalib.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NPC extends LivingEntity {
    public static final EntityDataAccessor<String> CURRENT_MODEL = SynchedEntityData.defineId(NPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> CURRENT_TEXTURE = SynchedEntityData.defineId(NPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> CURRENT_ANIMATION = SynchedEntityData.defineId(NPC.class, EntityDataSerializers.STRING);

    public NPC(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CURRENT_MODEL, "blablalib:npc");
        this.entityData.define(CURRENT_TEXTURE, "blablalib:npc/default");
        this.entityData.define(CURRENT_ANIMATION, "blablalib:npc/idle");
    }

    public String getModelNamespace() {
        String modelId = this.entityData.get(CURRENT_MODEL);
        if(modelId.contains(":"))
            return modelId.split(":")[0];
        return ResourceLocation.DEFAULT_NAMESPACE;
    }

    public String getModelPath(){
        String modelId = this.entityData.get(CURRENT_MODEL);
        if(modelId.contains(":"))
            return modelId.split(":")[1];
        return modelId;
    }

    public String getTextureNamespace() {
        String textureId = this.entityData.get(CURRENT_TEXTURE);
        if(textureId.contains(":"))
            return textureId.split(":")[0];
        return ResourceLocation.DEFAULT_NAMESPACE;
    }

    public String getTexturePath() {
        String textureId = this.entityData.get(CURRENT_TEXTURE);
        if(textureId.contains(":"))
            return textureId.split(":")[1];
        return textureId;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Model", this.entityData.get(CURRENT_MODEL));
        compound.putString("Texture", this.entityData.get(CURRENT_TEXTURE));
        compound.putString("Animation", this.entityData.get(CURRENT_ANIMATION));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Model"))
            this.entityData.set(CURRENT_MODEL, compound.getString("Model"));
        if (compound.contains("Texture"))
            this.entityData.set(CURRENT_TEXTURE, compound.getString("Texture"));
        if (compound.contains("Animation"))
            this.entityData.set(CURRENT_ANIMATION, compound.getString("Animation"));

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
}
