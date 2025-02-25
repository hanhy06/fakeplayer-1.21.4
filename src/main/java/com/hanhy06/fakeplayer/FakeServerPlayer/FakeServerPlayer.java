package com.hanhy06.fakeplayer.FakeServerPlayer;

import com.hanhy06.fakeplayer.FakePlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeServerPlayer extends ServerPlayerEntity {
    public FakeServerPlayer(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
        super(server, world, profile, clientOptions);

        this.networkHandler = new ServerPlayNetworkHandler(server,FakeClientConnection.SERVER_FAKE_CONNECTION,this, ConnectedClientData.createDefault(profile,false));
    }

    public FakeServerPlayer(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile,new SyncedClientOptions("en_us", 0, ChatVisibility.FULL, true, 127, PlayerEntity.DEFAULT_MAIN_ARM, false, false, ParticlesMode.ALL));

        PlayerProfileFetcher.applySkinFromGameProfile(profile);
        this.networkHandler = new ServerPlayNetworkHandler(server,FakeClientConnection.SERVER_FAKE_CONNECTION,this, ConnectedClientData.createDefault(profile,false));
    }

    @Override
    public void sleep(BlockPos pos) {
        this.setPose(EntityPose.SLEEPING);
        this.setSleepingPosition(pos);
        this.setVelocity(Vec3d.ZERO);
    }
}
