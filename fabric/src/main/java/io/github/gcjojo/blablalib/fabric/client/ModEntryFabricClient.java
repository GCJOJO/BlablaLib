package io.github.gcjojo.blablalib.fabric.client;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.ClientModEvents;
import io.github.gcjojo.blablalib.client.NPCRenderer;
import io.github.gcjojo.blablalib.entities.BlablaLibEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class ModEntryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlablaLib.initClient();
        ClientModEvents.registerClientModEvents();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricNpcModelReloadListener());
        EntityRendererRegistry.register(BlablaLibEntityTypes.NPC_TYPE.get(), NPCRenderer::new);
    }
}
