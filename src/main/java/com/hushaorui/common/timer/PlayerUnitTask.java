package com.hushaorui.common.timer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentSkipListMap;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerUnitTask extends TimerTask {
    private static final Log log = LogFactory.getLog(PlayerUnitTask.class);
    @Getter
    private final String key;
    @Getter
    private final long delay;
    @Getter
    private final long period;
    // 所有需要执行的任务(多任务)
    private ConcurrentSkipListMap<String, SimpleTask> taskMap;
    // 所有需要执行的任务(单任务)
    private SimpleTask simpleTask;

    public static PlayerUnitTask newSinglePlayerUnitTask(String key, long delay, Runnable runnable) {
        SimpleTask simpleTask = new SimpleTask(runnable) {
            @Override
            public void run() {
                runnable.run();
            }
        };
        return new PlayerUnitTask(key, delay, -1, null, simpleTask);
    }

    /** 创建一个周期性的任务，比如监听器 */
    public static PlayerUnitTask newMultiPlayerUnitTask(long key, long delay, long period) {
        return new PlayerUnitTask(String.valueOf(key), delay, period, new ConcurrentSkipListMap<>(), null);
    }

    @Override
    public void run() {
        for (Map.Entry<String, SimpleTask> entry : taskMap.entrySet()) {
            try {
                entry.getValue().run();
            } catch (Throwable throwable) {
                log.error(String.format("任务: %s, 执行失败", entry.getKey()), throwable);
            }
        }
    }

    /** 判断任务是否已经存在 */
    public boolean containsTask(String taskId) {
        if (taskMap == null) {
            return false;
        }
        return taskMap.containsKey(taskId);
    }

    /** 当任务不存在时添加 */
    public boolean putIfAbsent(String taskId, Runnable runnable) {
        if (taskMap == null) {
            if (simpleTask == null) {
                simpleTask = newSimpleTask(taskId, runnable);
                return true;
            } else {
                return false;
            }
        }
        return taskMap.putIfAbsent(taskId, newSimpleTask(taskId, runnable)) == null;
    }

    private SimpleTask newSimpleTask(String taskId, Runnable runnable) {
        return new SimpleTask(runnable) {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    log.error(String.format("任务执行出错，key:%s, taskId:%s, delay:%s, period:%s", key, taskId, delay, period), throwable);
                }
            }
        };
    }

    /** 强制覆盖原有任务 */
    public void forcePut(String taskId, Runnable runnable) {
        if (taskMap == null) {
            return;
        }
        taskMap.put(taskId, newSimpleTask(taskId, runnable));
    }

    /** 移除任务 */
    public void remove(String taskId) {
        if (taskMap == null) {
            return;
        }
        taskMap.remove(taskId);
    }

    public int taskSize() {
        return taskMap == null ? 0 : taskMap.size();
    }
}
