package com.example.chetna_priya.myapplication;

/**
 * Created by chetna_priya on 10/1/2015.
 */
public interface ResultSubject {

    public void registerObserver(ResultObserver resultObserver);
    public void unregisterObserver(ResultObserver resultObserver);

    public void notifyResultObservers();

    public Object getUpdate(ResultObserver resultObserver);


}
