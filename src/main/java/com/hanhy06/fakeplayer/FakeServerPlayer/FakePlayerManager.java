package com.hanhy06.fakeplayer.FakeServerPlayer;

import com.hanhy06.fakeplayer.mixin.PlayerManagerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FakePlayerManager{
    public static boolean instantRespawn = true;

    public static void onFakePlayerConnect(FakeServerPlayer player) {
        PlayerManager manager = player.getServer().getPlayerManager();

        List<ServerPlayerEntity> players = manager.getPlayerList();
        Map<UUID, ServerPlayerEntity> playerMap = ((PlayerManagerAccessor)manager).getPlayerMap();

        players.add(player);
        playerMap.put(player.getUuid(), player);

        manager.sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));

        player.getServerWorld().onPlayerConnected(player);

        player.onSpawn();
    }

    public static void removeFakePlayer(FakeServerPlayer player) {
        PlayerManager manager = player.getServer().getPlayerManager();

        List<ServerPlayerEntity> players = manager.getPlayerList();
        Map<UUID, ServerPlayerEntity> playerMap = ((PlayerManagerAccessor)manager).getPlayerMap();

        players.remove(player);
        playerMap.remove(player.getUuid());

        manager.getServer()
                .getWorld(player.getWorld().getRegistryKey())
                .removePlayer(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);

        manager.sendToAll(new PlayerRemoveS2CPacket(List.of(player.getUuid())));
    }

    public static FakeServerPlayer respawnFakePlayer(FakeServerPlayer player, boolean alive, Entity.RemovalReason removalReason) {
        PlayerManager manager = player.getServer().getPlayerManager();

        List<ServerPlayerEntity> players = manager.getPlayerList();
        Map<UUID, ServerPlayerEntity> playerMap = ((PlayerManagerAccessor) manager).getPlayerMap();

        players.remove(player);
        playerMap.remove(player.getUuid());

        player.getServerWorld().removePlayer(player, removalReason);

        FakeServerPlayer newFakePlayer = new FakeServerPlayer(
                player.getServer(),
                player.getServerWorld(),
                player.getGameProfile()
        );

        newFakePlayer.copyFrom(player, alive);
        newFakePlayer.setId(player.getId());
        newFakePlayer.setMainArm(player.getMainArm());

        newFakePlayer.setPosition(player.getPos());

        onFakePlayerConnect(newFakePlayer);

        return newFakePlayer;
    }
}
