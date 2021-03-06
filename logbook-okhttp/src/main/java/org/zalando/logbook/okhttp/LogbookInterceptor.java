package org.zalando.logbook.okhttp;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.zalando.logbook.Correlator;
import org.zalando.logbook.Logbook;

import java.io.IOException;
import java.util.Optional;

public final class LogbookInterceptor implements Interceptor {

    private final Logbook logbook;

    public LogbookInterceptor(final Logbook logbook) {
        this.logbook = logbook;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final LocalRequest request = new LocalRequest(chain.request());
        final Optional<Correlator> correlator = logbook.write(request);

        final RemoteResponse response = new RemoteResponse(chain.proceed(request.toRequest()));

        if (correlator.isPresent()) {
            correlator.get().write(response);
        }

        return response.toResponse();
    }

}
