package com.hushaorui.common.timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommonTimerMgr {
    private static final Log log = LogFactory.getLog(CommonTimerMgr.class);
    private static CommonTimerMgr instance;
    public static CommonTimerMgr getInstance() {
        if (instance == null) {
            synchronized (CommonTimerMgr.class) {
                if (instance == null) {
                    instance = new CommonTimerMgr();
                }
            }
        }
        return instance;
    }
    public CommonTimerMgr() {
        this(CommonTimerMgr.class.getSimpleName());
    }
    public CommonTimerMgr(String timerName) {
        this.timer = new Timer(timerName);
        timerTaskMap = new ConcurrentHashMap<>();
    }
    private final Timer timer;
    private Map<String, PlayerUnitTask> timerTaskMap;

    /**
     * 添加一个一次性的延时任务
     * @param key 任务存储的键1，一般是任务所属的主体
     * @param taskId 任务存储的键2，一般是特指某个功能的字符串
     * @param timerTask 任务
     * @param delay 延迟时间
     * @return 添加成功返回true，如果key，taskId已经可以找到对应的任务，则添加失败，返回false
     */
    public boolean addOnceDelayTask(Long key, String taskId, Runnable timerTask, long delay) {
        return addIntervalTask(key, taskId, timerTask, delay, -1);
    }

    private String getSingleKey(Long key, String taskId) {
        return String.format("%s-%s", key, taskId);
    }

    /**
     * 添加一个周期性的延时任务
     * @param key 任务存储的键1，一般是任务所属的主体
     * @param taskId 任务存储的键2，一般是特指某个功能的字符串
     * @param runnable 任务
     * @param delay 延迟时间
     * @param period 周期
     * @return 添加成功返回true，如果key，taskId已经可以找到对应的任务，则添加失败，返回false
     */
    public boolean addIntervalTask(Long key, String taskId, Runnable runnable, long delay, long period) {
        try {
            PlayerUnitTask playerUnitTask;
            if (period <= 0) {
                String singleKey = getSingleKey(key, taskId);
                // 一次性的任务
                playerUnitTask = PlayerUnitTask.newSinglePlayerUnitTask(singleKey, delay, () -> {
                    try {
                        runnable.run();
                    } catch (Throwable throwable) {
                        log.error(String.format("任务执行出错，key:%s, taskId:%s, delay:%s, period:%s", key, taskId, delay, period), throwable);
                    } finally {
                        timerTaskMap.remove(singleKey);
                    }
                });
                timer.schedule(playerUnitTask, delay);
                log.debug(String.format("成功添加一次性定时任务, key: %s, taskId: %s, delay: %s, period: %s", key, taskId, delay, period));
                return true;
            } else {
                AtomicBoolean result = new AtomicBoolean(false);
                timerTaskMap.computeIfAbsent(String.valueOf(key), k -> {
                    PlayerUnitTask unitTask = PlayerUnitTask.newMultiPlayerUnitTask(key, delay, period);
                    result.set(unitTask.putIfAbsent(taskId, runnable));
                    timer.schedule(unitTask, delay, period);
                    return unitTask;
                });
                boolean success = result.get();
                if (success) {
                    log.debug(String.format("成功添加周期性定时任务, key: %s, taskId: %s, delay: %s", key, taskId, delay));
                }
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 取消任务
     * @param key 任务存储的键1，一般是任务所属的主体
     * @param taskId 任务存储的键2，一般是特指某个功能的字符串
     * @param needRun 是否需要执行一遍任务后取消
     * @return 如果key，taskId已经可以找到对应的任务，正常取消，返回true，若没有找到，则返回false
     */
    public boolean cancelTask(Long key, String taskId, boolean needRun) {
        if (key == null || taskId == null) {
            return false;
        }
        String keyString = String.valueOf(key);
        if (timerTaskMap.containsKey(keyString)) {
            // 存在周期性任务
            PlayerUnitTask timerTask = timerTaskMap.get(keyString);
            if (timerTask.containsTask(taskId)) {
                if (timerTask.taskSize() == 1) {
                    // 只有这一个任务，将整个 PlayerUnitTask 删除
                    timerTaskMap.remove(keyString);
                    if (needRun) {
                        timerTask.run();
                    }
                    // 取消整个任务
                    timerTask.cancel();
                } else {
                    // 只删除这一个任务
                    timerTask.remove(taskId);
                }
                log.debug(String.format("成功取消周期性定时任务, key: %s, taskId: %s needRun: %s", key, taskId, needRun));
            }
            return true;
        } else {
            String singleKey = getSingleKey(key, taskId);
            if (timerTaskMap.containsKey(singleKey)) {
                // 存在一次性任务
                PlayerUnitTask timerTask = timerTaskMap.get(keyString);
                timerTaskMap.remove(keyString);
                if (needRun) {
                    timerTask.run();
                }
                // 取消整个任务
                timerTask.cancel();
                log.debug(String.format("成功取消一次性定时任务, key: %s, taskId: %s needRun: %s", key, taskId, needRun));
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 取消任务
     * @param key 任务存储的键1，一般是任务所属的主体
     * @param taskId 任务存储的键2，一般是特指某个功能的字符串
     * @return 如果key，taskId已经可以找到对应的任务，正常取消，返回true，若没有找到，则返回false
     */
    public boolean cancelTask(Long key, String taskId) {
        return cancelTask(key, taskId, false);
    }

    /**
     * 取消所有任务
     * @param needRun 是否需要执行一遍
     */
    public void cancelAllTask(boolean needRun) {
        for (PlayerUnitTask timerTask : timerTaskMap.values()) {
            if (needRun) {
                timerTask.run();
            }
            timerTask.cancel();
            log.debug(String.format("成功取消所有定时任务, needRun: %s", needRun));
        }
    }
}
