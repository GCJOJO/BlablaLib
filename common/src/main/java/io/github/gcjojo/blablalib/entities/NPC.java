package io.github.gcjojo.blablalib.entities;

import io.github.gcjojo.blablalib.BlablaLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;

public class NPC extends LivingEntity {
    protected static final EntityDataAccessor<NPCDefinition> DEFAULT_STATE = SynchedEntityData.defineId(NPC.class, BlablaLibEntityDataSerializers.NPC_DEFINITION);
    protected static final EntityDataAccessor<NPCDefinition> CURRENT_STATE = SynchedEntityData.defineId(NPC.class, BlablaLibEntityDataSerializers.NPC_DEFINITION);
    protected static final EntityDataAccessor<Boolean> LOOP_CURRENT_ANIMATION = SynchedEntityData.defineId(NPC.class, EntityDataSerializers.BOOLEAN);

    public NPC(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        setCurrentModel("blablalib:geometry.npc");
        setCurrentTexture("blablalib:textures/entity/npc/default.png");
        setCurrentDialogue("blablalib:test_npc");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D);
    }

    public boolean playerInteraction(Player player, InteractionHand hand) {
        if(player.isCreative() && player.isCrouching())
        {
            // Open Edit NPC Screen
            return true;
        }

        ResourceLocation dialogueId = ResourceLocation.tryParse(getCurrentState().dialogue());
        if(dialogueId != null && player instanceof ServerPlayer serverPlayer) {
            BlablaLib.setPlayerNPC(serverPlayer, this, 0);
            BlablaLib.openDialogue(serverPlayer, dialogueId);
            return false;
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEFAULT_STATE, NPCDefinition.EMPTY);
        this.entityData.define(CURRENT_STATE, NPCDefinition.EMPTY);
        this.entityData.define(LOOP_CURRENT_ANIMATION, false);
    }

    public String getModelNamespace() {
        String modelId = getCurrentState().modelLocation();
        if(modelId.contains(":"))
            return modelId.split(":")[0];
        return ResourceLocation.DEFAULT_NAMESPACE;
    }

    public String getModelPath(){
        String modelId = getCurrentState().modelLocation();
        if(modelId.contains(":"))
            return modelId.split(":")[1];
        return modelId;
    }

    public String getTextureNamespace() {
        String textureId = getCurrentState().textureLocation();
        if(textureId.contains(":"))
            return textureId.split(":")[0];
        return ResourceLocation.DEFAULT_NAMESPACE;
    }

    public String getTexturePath() {
        String textureId = getCurrentState().textureLocation();
        if(textureId.contains(":"))
            return textureId.split(":")[1];
        return textureId;
    }

    public NPCDefinition getDefaultState() {
        return this.entityData.get(DEFAULT_STATE);
    }

    public NPCDefinition getCurrentState() {
        return this.entityData.get(CURRENT_STATE);
    }

    public void setCurrentModel(String newModel){
        this.entityData.set(CURRENT_STATE, getCurrentState().withModel(newModel));
    }

    public void setDefaultModel(String newModel) {
        this.entityData.set(DEFAULT_STATE, getDefaultState().withModel(newModel));
    }

    public void setCurrentTexture(String newTexture){
        this.entityData.set(CURRENT_STATE, getCurrentState().withTexture(newTexture));
    }

    public void setDefaultTexture(String newTexture) {
        this.entityData.set(DEFAULT_STATE, getDefaultState().withTexture(newTexture));
    }

    public void setCurrentAnimation(String newAnimation, boolean loop){
        this.entityData.set(CURRENT_STATE, getCurrentState().withAnimation(newAnimation));
        this.entityData.set(LOOP_CURRENT_ANIMATION, loop);
    }

    public void setDefaultAnimation(String newAnimation) {
        this.entityData.set(DEFAULT_STATE, getDefaultState().withAnimation(newAnimation));
    }

    public void setCurrentDialogue(String newDialogue){
        this.entityData.set(CURRENT_STATE, getCurrentState().withDialogue(newDialogue));
    }

    public void setDefaultDialogue(String newDialogue) {
        this.entityData.set(DEFAULT_STATE, getDefaultState().withDialogue(newDialogue));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("DefaultState", this.entityData.get(DEFAULT_STATE).toCompoundTag());
        compound.put("CurrentState", this.entityData.get(CURRENT_STATE).toCompoundTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
