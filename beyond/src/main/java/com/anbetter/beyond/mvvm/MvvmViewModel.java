package com.anbetter.beyond.mvvm;

/**
 *
 * Created by android_ls on 16/1/2.
 */
public interface MvvmViewModel<M, V extends MvvmView<M>> {

    void attachView(V view);

    void detachView();

}
