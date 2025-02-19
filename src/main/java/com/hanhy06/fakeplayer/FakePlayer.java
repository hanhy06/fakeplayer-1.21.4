package com.hanhy06.fakeplayer;

import com.hanhy06.fakeplayer.FakeServerPlayer.FakeClientConnection;
import com.hanhy06.fakeplayer.FakeServerPlayer.FakeServerPlayer;
import com.hanhy06.fakeplayer.FakeServerPlayer.PlayerProfileFetcher;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class FakePlayer implements ModInitializer {
	public static final String MOD_ID = "fakeplayer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register(
				(minecraftServer, serverWorld) -> {
					if(serverWorld.getRegistryKey()== World.OVERWORLD){
						GameProfile profile = new GameProfile(UUID.randomUUID(),"dorong1972");
                        PlayerProfileFetcher.applySkinFromGameProfile(profile);

                        FakeServerPlayer player = new FakeServerPlayer(minecraftServer,minecraftServer.getOverworld(),profile);

						PlayerManager manager = minecraftServer.getPlayerManager();

						manager.onPlayerConnect(FakeClientConnection.SERVER_FAKE_CONNECTION,player,ConnectedClientData.createDefault(profile,false));
					}
				}
		);

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if(entity instanceof FakeServerPlayer){
				PlayerManager manager= entity.getServer().getPlayerManager();

				manager.respawnPlayer((FakeServerPlayer) entity,false, Entity.RemovalReason.DISCARDED);
			}
		});
	}
};