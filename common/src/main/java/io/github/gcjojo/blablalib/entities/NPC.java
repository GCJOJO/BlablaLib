package io.github.gcjojo.blablalib.entities;

import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.network.payloads.NPCVisualStatePayload;
import io.github.gcjojo.blablalib.npc.NPCState;
import io.github.gcjojo.blablalib.npc.NPCVisualState;
import io.github.gcjojo.blablalib.npc.definitions.NPCDefinition;
import io.github.gcjojo.blablalib.npc.definitions.NPCStateDefinition;
import lombok.Setter;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NPC extends LivingEntity {
    // Server-Side
    private static final EntityDataAccessor<String> NPC_DEFINITION_ID =
            SynchedEntityData.defineId(NPC.class, EntityDataSerializers.STRING);

    protected NPCDefinition npcDefinition = NPCDefinition.DEFAULT;
    protected final Map<UUID, NPCState> stateForPlayers = new HashMap<>();


    // Client Side
    @Setter
    protected NPCVisualState clientState;

    public NPC(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
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

        ResourceLocation dialogueId = getCurrentStateDefinition(player).dialogueId();
        if(dialogueId != null && player instanceof ServerPlayer serverPlayer) {
            BlablaLib.setPlayerNPC(serverPlayer, this, 0);
            BlablaLib.openDialogue(serverPlayer, dialogueId);
            return false;
        }
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(NPC_DEFINITION_ID, "");
    }

    public ResourceLocation getDefinitionId() {
        return ResourceLocation.tryParse(entityData.get(NPC_DEFINITION_ID));
    }

    public void setDefinitionId(ResourceLocation newId) {
        entityData.set(NPC_DEFINITION_ID, newId.toString());
    }

    public String getModelNamespace() {
        String modelId = clientState.modelLocation();
        if(modelId.contains(":"))
            return modelId.split(":")[0];
        return ResourceLocation.DEFAULT_NAMESPACE;
    }

    public String getModelPath(){
        String modelId = clientState.modelLocation();
        if(modelId.contains(":"))
            return modelId.split(":")[1];
        return modelId;
    }

    public String getTextureNamespace() {
        String textureId = clientState.textureLocation();
        if(textureId.contains(":"))
            return textureId.split(":")[0];
        return ResourceLocation.DEFAULT_NAMESPACE;
    }

    public String getTexturePath() {
        String textureId = clientState.textureLocation();
        if(textureId.contains(":"))
            return textureId.split(":")[1];
        return textureId;
    }

    public NPCStateDefinition getRootState() {
        return npcDefinition.getRootState();
    }

    public NPCState getCurrentState(Player player) {
        return stateForPlayers.computeIfAbsent(player.getUUID(), uuid -> new NPCState(npcDefinition));
    }

    public NPCStateDefinition getCurrentStateDefinition(Player player) {
        return getCurrentState(player).currentStateDefinition;
    }

    @Override
    public void tick() {
        for(ServerPlayer player : Objects.requireNonNull(getServer()).getPlayerList().getPlayers()) {
            NPCState state = getCurrentState(player);
            if(state.updateState(player))
                NetworkManager.sendToPlayer(player, new NPCVisualStatePayload(getUUID(), state.currentStateDefinition.visualState()));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Definition", this.entityData.get(NPC_DEFINITION_ID));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(NPC_DEFINITION_ID, compound.getString("Definition"));
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) { }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
