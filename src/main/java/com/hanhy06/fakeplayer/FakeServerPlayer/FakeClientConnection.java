package com.hanhy06.fakeplayer.FakeServerPlayer;

import com.hanhy06.fakeplayer.mixin.ClientConnectionAccessor;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

public class FakeClientConnection extends ClientConnection {

    public static final FakeClientConnection SERVER_FAKE_CONNECTION = new FakeClientConnection(NetworkSide.SERVERBOUND);

    public FakeClientConnection(NetworkSide side) {
        super(side);

        ((ClientConnectionAccessor) this).setChannel(new EmbeddedChannel());
    }
}
