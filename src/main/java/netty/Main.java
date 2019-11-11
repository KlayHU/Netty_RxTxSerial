package netty;

import Test.PortManager;
import org.apache.log4j.Logger;


public class Main {
    private static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("开始");
        nettyConfig nettyConfig = new nettyConfig();
        Thread thread = new Thread(nettyConfig);
        thread.start();
        new PortManager().run();
        }

}
