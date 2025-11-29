package com.example.webhook.core.worker;

import com.example.webhook.core.dispatch.WebhookDispatcher;
import com.example.webhook.core.queue.QueuedWebhook;
import com.example.webhook.core.queue.WorkQueue;
import com.example.webhook.integrations.CallbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
public class BackgroundWorker {

    private static final Logger log = LoggerFactory.getLogger(BackgroundWorker.class);

    public BackgroundWorker(
            WorkQueue queue,
            WebhookDispatcher dispatcher,
            CallbackService callbackService
    ) {
        queue.stream()
                .flatMap(qw ->
                        dispatcher.dispatch(qw.request())
                                .flatMap(result -> callbackService.sendSuccess(
                                        qw.callbackUrl(),
                                        qw.traceId(),
                                        result
                                ))
                                .onErrorResume(ex -> callbackService.sendFailure(
                                        qw.callbackUrl(),
                                        qw.traceId(),
                                        ex
                                ))
                                .subscribeOn(Schedulers.boundedElastic()),
                        8
                )
                .subscribe(
                        v -> {},
                        ex -> log.error("[BackgroundWorker] Stream error: {}", ex.toString(), ex),
                        () -> log.info("[BackgroundWorker] Stream completed")
                );
    }
}
