package com.hanhy06.fakeplayer;

import com.hanhy06.fakeplayer.FakeServerPlayer.FakePlayerManager;
import com.hanhy06.fakeplayer.FakeServerPlayer.FakeServerPlayer;
import com.hanhy06.fakeplayer.mixin.PlayerManagerAccessor;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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

                        FakeServerPlayer player = new FakeServerPlayer(minecraftServer,minecraftServer.getOverworld(),profile);

						FakePlayerManager.onFakePlayerConnect(player,minecraftServer.getPlayerManager());
					}
				}
		);

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if(entity instanceof FakeServerPlayer) {
				FakeServerPlayer player = new FakeServerPlayer(entity.getServer(),((FakeServerPlayer) entity).getServerWorld(),((FakeServerPlayer) entity).getGameProfile());

				FakePlayerManager.removeFakePlayer((FakeServerPlayer) entity,entity.getServer().getPlayerManager());
				FakePlayerManager.onFakePlayerConnect(player,entity.getServer().getPlayerManager());
			}
		});
	}
};