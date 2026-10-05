package io.github.gcjojo.blablalib.neoforge;

import io.github.gcjojo.blablalib.BlablaLib;
import io.github.gcjojo.blablalib.client.ClientModEvents;
import io.github.gcjojo.blablalib.client.NPCRenderer;
import io.github.gcjojo.blablalib.client.NpcModelReloadListener;
import io.github.gcjojo.blablalib.commands.DialogueCommand;
import io.github.gcjojo.blablalib.entities.BlablaLibEntityTypes;
import io.github.gcjojo.blablalib.entities.neoforge.BlablaLibEntityDataSerializersImpl;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(BlablaLib.MOD_ID)
public final class ModEntryNeoForge {
    public ModEntryNeoForge(ModContainer container) {
        // Run our common setup.
        BlablaLib.init();

        BlablaLibEntityDataSerializersImpl.SERIALIZERS.register(container.getEventBus());

        container.registerConfig(ModConfig.Type.COMMON, BlablaLibNeoForgeConfig.CONFIG_SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        NeoForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            DialogueCommand.register(event.getDispatcher());
        }
    }

    @Mod(value = BlablaLib.MOD_ID, dist = Dist.CLIENT)
    public static class ClientModEntryForge {
        public ClientModEntryForge(IEventBus modEventBus) {
            BlablaLib.initClient();
            ClientModEvents.registerClientModEvents();

            modEventBus.addListener(ClientModEntryForge::registerRenderers);
            modEventBus.addListener(ClientModEntryForge::registerReloadListeners);
        }

        private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(BlablaLibEntityTypes.NPC_TYPE.get(), NPCRenderer::new);
        }

        private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new NpcModelReloadListener());
        }
    }
}
