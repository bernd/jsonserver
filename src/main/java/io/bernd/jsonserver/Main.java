package io.bernd.jsonserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class Main {
    static {
        // Hijack java.util.logging, see https://logging.apache.org/log4j/log4j-2.2/log4j-jul/index.html
        System.setProperty("java.util.logging.manager", org.apache.logging.log4j.jul.LogManager.class.getCanonicalName());

    }

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws Exception {
        org.eclipse.jetty.util.log.Log.setLog(new org.eclipse.jetty.util.log.Slf4jLog());

        int port = 3000;

        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        Spark.port(port);

        Spark.get("/*", (request, response) -> {
            log.info("Content-Type {}", request.contentType());

            return "HELLO\n";
        });

        Spark.post("/*", (request, response) -> {
            if ("application/x-jackson-smile".equals(request.contentType())) {
                parseSmile(request, response);
            } else if (request.contentType().startsWith("application/json")) {
                parseJson(request, response);
            } else {
                log.warn("Unhandled content-type: {}", request.contentType());
            }

            return "ok\n";
        });
    }

    private static void parseJson(Request request, Response response) {

    }

    private static void parseSmile(Request request, Response response) throws IOException {
        final SmileFactory smileFactory = new SmileFactory().disable(SmileParser.Feature.REQUIRE_HEADER);
        final ObjectMapper smileMapper = new ObjectMapper(smileFactory);
        final ObjectMapper jsonMapper = new ObjectMapper();


        final InputStream inputStream;
        if ("gzip".startsWith(request.headers("Content-Encoding"))) {
            inputStream = new GZIPInputStream(request.raw().getInputStream());
        } else {
            inputStream = request.raw().getInputStream();
        }

        final JsonNode jsonNode = smileMapper.readTree(inputStream);
        final String string = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

        System.out.println(string);
    }
}
