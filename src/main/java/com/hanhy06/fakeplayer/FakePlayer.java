package com.hanhy06.fakeplayer;

import com.hanhy06.fakeplayer.FakeServerPlayer.FakeClientConnection;
import com.hanhy06.fakeplayer.FakeServerPlayer.FakeServerPlayer;
import com.hanhy06.fakeplayer.FakeServerPlayer.PlayerProfileFetcher;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.UUID;

public class FakePlayer implements ModInitializer {
	public static final String MOD_ID = "fakeplayer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(
				(minecraftServer, serverWorld) -> {
					if(serverWorld.getRegistryKey()== World.OVERWORLD){
						GameProfile profile = new GameProfile(UUID.randomUUID(),"hanhy06");
                        PlayerProfileFetcher.applySkinFromGameProfile(profile);

                        FakeServerPlayer player = new FakeServerPlayer(minecraftServer,minecraftServer.getOverworld(),profile, SyncedClientOptions.createDefault());

						minecraftServer.getPlayerManager().onPlayerConnect(FakeClientConnection.SERVER_FAKE_CONNECTION,player,ConnectedClientData.createDefault(profile,false));
					}
				}
		);

//		ServerPlayConnectionEvents.JOIN.register(
//				(serverPlayNetworkHandler, packetSender, minecraftServer) -> {
//					GameProfile profile = serverPlayNetworkHandler.getPlayer().getGameProfile();
//					PropertyMap map = profile.getProperties();
//					Collection<Property> textures = map.get("textures");
//				}
//		);
	}
}