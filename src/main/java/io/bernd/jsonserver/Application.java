package io.bernd.jsonserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileParser;
import io.bernd.jsonserver.filters.GZIPFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static final String ATTR_BODY_STREAM = "bodyStream";

    private final String[] args;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper smileMapper;

    public Application(final String[] args) {
        this.args = args;
        this.jsonMapper = new ObjectMapper();
        this.smileMapper =  new ObjectMapper(new SmileFactory().disable(SmileParser.Feature.REQUIRE_HEADER));
    }

    public void run() {
        int port = 3000;

        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        Spark.port(port);
        Spark.before(new GZIPFilter());

        Spark.get("/*", (request, response) -> {
            log.info("Content-Type {}", request.contentType());

            return "HELLO\n";
        });

        Spark.post("/*", (request, response) -> {
            if ("application/x-jackson-smile".equals(request.contentType())) {
                parseSmile(request);
            } else {
                parseJson(request);
            }

            return "ok\n";
        });
    }

    private void parseJson(Request request) throws IOException {
        final JsonNode jsonNode = jsonMapper.readTree((InputStream) request.attribute(ATTR_BODY_STREAM));
        final String string = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

        System.out.println(string);
    }

    private void parseSmile(Request request) throws IOException {
        final JsonNode jsonNode = smileMapper.readTree((InputStream) request.attribute(ATTR_BODY_STREAM));
        final String string = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

        System.out.println(string);
    }
}
