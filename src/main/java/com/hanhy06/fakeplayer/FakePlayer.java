package com.hanhy06.fakeplayer;

import com.hanhy06.fakeplayer.FakeServerPlayer.FakePlayerAPICommand;
import com.hanhy06.fakeplayer.FakeServerPlayer.FakePlayerManager;
import com.hanhy06.fakeplayer.FakeServerPlayer.FakeServerPlayer;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
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
//		ServerWorldEvents.LOAD.register(
//				(minecraftServer, serverWorld) -> {
//					if(serverWorld.getRegistryKey()== World.OVERWORLD){
//						GameProfile profile = new GameProfile(UUID.randomUUID(),"test");
//
//                        FakeServerPlayer player = new FakeServerPlayer(minecraftServer,minecraftServer.getOverworld(),profile);
//
//						FakePlayerManager.onFakePlayerConnect(player);
//					}
//				}
//		);

		FakePlayerAPICommand.registerFakePlayerAPICommand();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if(FakePlayerManager.instantRespawn && entity instanceof FakeServerPlayer) {
				FakePlayerManager.respawnFakePlayer((FakeServerPlayer) entity,false, Entity.RemovalReason.DISCARDED);;
			}
		});
	}
};