package com.soen6441.battleship.utils;

import com.soen6441.battleship.data.model.Grid;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility functions to control timers in the game.
 */
public class TimerUtil {
    private boolean isRunning = false;
    private long startTime = 0;
    private long stopTime = 0;
    private BehaviorSubject<Long> timerListener = BehaviorSubject.create();

    public static String printableTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = (milliseconds / 1000) / 60;
        totalSeconds -= minutes * 60;
        return String.format("%02d:%02d", minutes, totalSeconds);
    }

    public TimerUtil() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRunning) {
                    timerListener.onNext(new Date().getTime() - startTime);
                } else {
                    timerListener.onNext(TimerUtil.this.stopTime - TimerUtil.this.startTime);
                }
            }
        }, 1000, 1000);
    }

    public void start() {
        if (isRunning) {
            throw new RuntimeException("Timer already started!");
        }

        isRunning = true;

        if (startTime == 0) {
            startTime = new Date().getTime();
        }
    }

    public long stop() {
        if (!isRunning) {
            throw new RuntimeException("Timer has not been started!");
        }

        isRunning = false;
        stopTime = new Date().getTime();
        return stopTime - startTime;
    }

    public void reset() {
        this.isRunning = false;
        this.startTime = 0;
        this.stopTime = 0;
    }

    public Observable<Long> asObservable() {
        return this.timerListener.subscribeOn(Schedulers.newThread());
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public long getTime() {
        return new Date().getTime() - startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setTimeElapsed(long gameTime) {
        this.startTime = new Date().getTime() - gameTime;
    }
}
