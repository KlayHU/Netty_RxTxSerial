package netty;

import Test.PortManager;

public class Main {
    public static void main(String[] args) {
        nettyConfig nettyConfig = new nettyConfig();
        Thread thread = new Thread(nettyConfig);
        thread.start();
        new PortManager().run();

        }

}
