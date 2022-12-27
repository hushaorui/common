package com.hushaorui.common.timer;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class SimpleTask implements Runnable {
    private Runnable runnable;
}
