package com.github.gfx.helium.util;

import com.cookpad.android.rxt4a.subscriptions.AndroidCompositeSubscription;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;

public class OperatorAddToCompositeSubscriptionForSingle<T> implements Observable.Operator<T, T> {

    final AndroidCompositeSubscription compositeSubscription;

    public OperatorAddToCompositeSubscriptionForSingle(@NonNull AndroidCompositeSubscription subscription) {
        this.compositeSubscription = subscription;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        compositeSubscription.add(subscriber);
        return subscriber;
    }
}
