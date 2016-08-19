package com.gmail.tracebachi.MangoShop.Utils;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/20/16.
 */
@FunctionalInterface
public interface Callback<TResult>
{
    void onResult(TResult result);
}
