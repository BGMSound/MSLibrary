package kr.msleague.proxy;

import com.google.common.base.Preconditions;
import kr.msleague.internal.logger.BCSPLogManager;
import kr.msleague.internal.netty.channel.ChannelWrapper;
import kr.msleague.internal.netty.pipeline.BossHandler;
import kr.msleague.proxy.event.connection.BCSPProxyClientConnectedEvent;
import kr.msleague.proxy.event.connection.BCSPProxyClientDisconnectedEvent;
import kr.msleague.proxy.event.packet.ProxyPacketReceivedEvent;
import kr.msleague.proxy.util.ServerInfoUtil;
import net.md_5.bungee.api.ProxyServer;

import java.util.HashMap;

public class BCSPProxyChannelContainer {
    protected HashMap<Integer, ChannelWrapper> portChannelContainer = new HashMap<>();
    protected HashMap<String, ChannelWrapper> serverNameChannelContainer = new HashMap<>();
    private int unknownClientId = 1;
    public void onChannelActive(Integer port, ChannelWrapper wrapper){
        Preconditions.checkNotNull(port, wrapper);
        String name = ServerInfoUtil.getServerNameByPort(port);
        if(name == null)
            name = "Unknown-"+unknownClientId++;
        ProxyServer.getInstance().getPluginManager().callEvent(new BCSPProxyClientConnectedEvent(wrapper, wrapper.getHandler().getConnectionState(), name));
        BCSPLogManager.getLogger().info("BCSP registered new channel (Port: {0}, Address: {1}, Name: {2})", port, wrapper.getChannel().remoteAddress(), name);
        portChannelContainer.put(port, wrapper);
        serverNameChannelContainer.put(name, wrapper);
        BossHandler handlerBoss = wrapper.getChannel().pipeline().get(BossHandler.class);
        handlerBoss.setDisconnectHandler(this::onChannelDisActive);
        handlerBoss.setPacketPreProcessHandler((pack,wrap)->ProxyServer.getInstance().getPluginManager().callEvent(new ProxyPacketReceivedEvent(pack, wrap)));
    }
    public void onChannelDisActive(ChannelWrapper wrapper){
        ProxyServer.getInstance().getPluginManager().callEvent(new BCSPProxyClientDisconnectedEvent(wrapper, wrapper.getHandler().getConnectionState()));
        portChannelContainer.entrySet().removeIf(i -> i.getValue() == wrapper);
        serverNameChannelContainer.entrySet().removeIf(i -> i.getValue() == wrapper);
    }
    public void onShutdown(){
        portChannelContainer.values().forEach(x->{
            if(x.getChannel().isActive() && x.getChannel().isOpen())
                x.getChannel().close();
        });
    }
    public ChannelWrapper getChannelByPort(int port){
        return portChannelContainer.get(port);
    }
    public ChannelWrapper getChannelByServerName(String serverName){
        return serverNameChannelContainer.get(serverName);
    }
}
