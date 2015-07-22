package io.bernd.jsonserver;

public class Main {
    static {
        // Hijack java.util.logging, see https://logging.apache.org/log4j/log4j-2.2/log4j-jul/index.html
        System.setProperty("java.util.logging.manager", org.apache.logging.log4j.jul.LogManager.class.getCanonicalName());

    }

    public static void main(final String[] args) throws Exception {
        org.eclipse.jetty.util.log.Log.setLog(new org.eclipse.jetty.util.log.Slf4jLog());

        new Application(args).run();
    }

}
