package com.quangnam.baseframework;

import rx.Subscription;

/**
 * Created by quangnam on 11/22/16.
 * Project FileManager-master
 */
public interface AutoUnsubscribe {

    void subscribe(Subscription subscription);
}
