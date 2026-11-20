package de.john.droptweaks;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class JohnsDropTweaksClient implements ClientModInitializer {
    private static boolean openNextTick = false;

    @Override
    public void onInitializeClient() {
        Config.load();
        // Rein clientseitiger Befehl -> funktioniert auf jedem Server
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("droptweaks").executes(ctx -> {
                openNextTick = true; // Chat schliesst sich nach dem Befehl, daher erst im naechsten Tick oeffnen
                return Command.SINGLE_SUCCESS;
            })));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (openNextTick) {
                openNextTick = false;
                mc.setScreen(new DropTweaksScreen(null));
            }
        });
    }
}
