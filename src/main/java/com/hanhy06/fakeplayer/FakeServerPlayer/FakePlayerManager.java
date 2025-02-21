package com.hanhy06.fakeplayer.FakeServerPlayer;

import com.hanhy06.fakeplayer.mixin.PlayerManagerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FakePlayerManager{
    public static void onFakePlayerConnect(FakeServerPlayer player,PlayerManager manager) {
        List<ServerPlayerEntity> players = manager.getPlayerList();
        Map<UUID, ServerPlayerEntity> playerMap = ((PlayerManagerAccessor)manager).getPlayerMap();

        players.add(player);
        playerMap.put(player.getUuid(), player);

        manager.getServer()
                .getWorld(player.getWorld().getRegistryKey())
                .onPlayerConnected(player);

        player.onSpawn();
        manager.sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
    }

    public static void removeFakePlayer(FakeServerPlayer player,PlayerManager manager) {
        List<ServerPlayerEntity> players = manager.getPlayerList();
        Map<UUID, ServerPlayerEntity> playerMap = ((PlayerManagerAccessor)manager).getPlayerMap();

        players.remove(player);
        playerMap.remove(player.getUuid());

        manager.getServer()
                .getWorld(player.getWorld().getRegistryKey())
                .removePlayer(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);

        manager.sendToAll(new PlayerRemoveS2CPacket(List.of(player.getUuid())));
    }
}
