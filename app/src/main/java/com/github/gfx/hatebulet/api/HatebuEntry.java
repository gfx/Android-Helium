package com.github.gfx.hatebulet.api;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.annotation.ParametersAreNonnullByDefault;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

@ParametersAreNonnullByDefault
public class HatebuEntry {
    public static final String ENDPOINT = "http://b.hatena.ne.jp/";


    public Observable<String> hotentry(final OkHttpClient httpClient) {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Request request = new Request.Builder()
                        .get()
                        .url(ENDPOINT + "/hotentry?mode=rss")
                        .build();

                final Call call = httpClient.newCall(request);

                subscriber.add(new Subscription() {
                    @Override
                    public void unsubscribe() {
                        call.cancel();
                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return call.isCanceled();
                    }
                });

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        IOException exception = getExceptionOnBadStatus(response);
                        if (exception != null) {
                            subscriber.onError(exception);
                            return;
                        }

                        subscriber.onNext(response.body().string());
                        subscriber.onCompleted();
                    }
                });

            }
        });
    }

    private static IOException getExceptionOnBadStatus(Response response) {
        if (response.code() < 400) return null;
        return new UnexpectedResponseException(response);
    }

    public static class UnexpectedResponseException extends IOException {
        public final Response response;

        public UnexpectedResponseException(Response response) {
            this.response = response;
        }
    }
}
