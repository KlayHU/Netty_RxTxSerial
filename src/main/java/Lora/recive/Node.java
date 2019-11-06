package Lora.recive;

import io.netty.channel.ChannelHandlerContext;

public class Node {
    //节点id
    private String id;
    //节点对应串口
    private String PortId;

    /**
     * 写发送beacon消息的ChannelHandlerContext
     */
    private ChannelHandlerContext ctx;

    public String getId() {
        return id;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", PortId='" + PortId + '\'' +
                ", ctx=" + ctx +
                '}';
    }

    public String getPortId() {
        return PortId;
    }

    public void setPortId(String portId) {
        PortId = portId;
    }
}
