package io.bernd.jsonserver.filters;

import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.zip.GZIPInputStream;

import static io.bernd.jsonserver.Application.ATTR_BODY_STREAM;

public class GZIPFilter implements Filter {
    @Override
    public void handle(Request request, Response response) throws Exception {
        final String contentEncoding = request.headers("Content-Encoding");

        if (contentEncoding != null && "gzip".startsWith(contentEncoding)) {
            request.attribute(ATTR_BODY_STREAM, new GZIPInputStream(request.raw().getInputStream()));
        } else {
            request.attribute(ATTR_BODY_STREAM, request.raw().getInputStream());
        }
    }
}
