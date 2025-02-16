package com.hanhy06.fakeplayer.FakeServerPlayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FakeServerPlayer extends ServerPlayerEntity {
    public FakeServerPlayer(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
        super(server, world, profile, clientOptions);

        this.networkHandler = new ServerPlayNetworkHandler(server,FakeClientConnection.SERVER_FAKE_CONNECTION,this, ConnectedClientData.createDefault(profile,false));
    }
}
