package com.hanhy06.fakeplayer.FakeServerPlayer;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.UUID;

public class FakePlayerAPICommand {
    public static void registerFakePlayerAPICommand(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(
                    CommandManager.literal("FakePlayerAPI")
                            .then(CommandManager.literal("connect")
                                    .then(CommandManager
                                            .argument("name", StringArgumentType.string())
                                            .executes(FakePlayerAPICommand::onConnectFakePlayer)
                                    )
                            )
                            .then(CommandManager.literal("remove")
                                    .then(CommandManager
                                            .argument("player", EntityArgumentType.player())
                                            .executes(FakePlayerAPICommand::removeFakePlayer)
                                    )
                            )
            );
        });
    }

    private static int onConnectFakePlayer(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity user = context.getSource().getPlayer();

        GameProfile profile = new GameProfile(UUID.randomUUID(), StringArgumentType.getString(context,"name"));

        FakeServerPlayer player = new FakeServerPlayer(user.getServer(),user.getServerWorld(),profile);
        player.setPosition(user.getPos());

        FakePlayerManager.onFakePlayerConnect(player);
        
        return 1;
    }

    private static int removeFakePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerManager manager = context.getSource().getServer().getPlayerManager();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context,"player");

        if(player instanceof FakeServerPlayer){
            FakePlayerManager.removeFakePlayer((FakeServerPlayer) player);
        }

        return 1;
    }
}
