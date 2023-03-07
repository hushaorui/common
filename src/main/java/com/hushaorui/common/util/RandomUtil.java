package com.hushaorui.common.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomUtil {

	private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<>();
	private static Map<String, Map<String, AtomicInteger>> randomSeqMap = new ConcurrentHashMap<>();

	public static Random getRandom() {
		Random random = randomThreadLocal.get();
		if (random == null) {
			random = new Random();
			randomThreadLocal.set(random);
		}
		return random;
	}

	/**
	 * 根据一个种子获取一个伪随机数
	 * @param seed 种子，最好使用uuid，以免结果被很容易重现，使用的程序自己保存下来，以便每次随机时使用
	 * @param randomSeq 序列
	 * @param min 随机的最小值(包括最小值，可以被随机出来)
	 * @param max 随机的最大值(包括最大值，不可以被随机出来)
	 * @return 随机的整数，在 int范围内
	 */
	public static int getRandomCount(AtomicInteger randomSeq, String seed, int min, int max) {
		String md5 = MD5Utils.md5(seed + "," + randomSeq.getAndIncrement()).substring(0, 9);
		long num = Long.parseLong(md5, 16);
		return min + Math.abs((int) num) % (max + 1 - min);
	}

	/**
	 * 根据一个种子获取一个伪随机数
	 * @param seed 种子，最好使用uuid，以免结果被很容易重现，使用的程序自己保存下来，以便每次随机时使用
	 * @param keyword 关键字， 和 seed 联合唯一维护一个序列
	 * @param min 随机的最小值(包括最小值，可以被随机出来)
	 * @param max 随机的最大值(包括最大值，不可以被随机出来)
	 * @return 随机的整数，在 int范围内
	 */
	public static int getRandomCount(String seed, String keyword, int min, int max) {
		AtomicInteger atomicInteger = randomSeqMap.computeIfAbsent(seed, k -> new ConcurrentHashMap<>()).computeIfAbsent(keyword, k2 -> new AtomicInteger(1));
		return getRandomCount(atomicInteger, seed, min, max - 1);
	}

	/**
	 * 在一定范围内随机多个不重复的数
	 * @param start 最小数，包括
	 * @param end 最大数，不包括
	 * @param count 随机数的数量，不能大于 end - start
	 * @return 随机出的数的集合
	 */
	public static Collection<Integer> getRandomIntegers(AtomicInteger randomSeq, String seed, int start, int end, int count) {
		return getRandomIntegers(randomSeq, seed, start, end, count, Collections.emptySet());
	}

    /**
     * 在一定范围内随机多个不重复的数
     * @param start 最小数，包括
     * @param end 最大数，不包括
     * @param count 随机数的数量，不能大于 end - start
     * @return 随机出的数的集合
     */
    public static Collection<Integer> getRandomIntegers(String seed, String keyword, int start, int end, int count) {
        return getRandomIntegers(seed, keyword, start, end, count, Collections.emptySet());
    }

    /**
     * 在一定范围内随机多个不重复的数
     * @param seed 种子
     * @param keyword 关键字
     * @param start 最小数，包括
     * @param end 最大数，不包括
     * @param count 随机数的数量，不能大于 end - start
     * @param excludeSet 排除的数，不允许随机到
     * @return 随机出的数的集合
     */
    public static Collection<Integer> getRandomIntegers(String seed, String keyword, int start, int end, int count, Set<Integer> excludeSet) {
        int arrayLength = end - start - excludeSet.size();
        if (arrayLength <= 0) {
            return Collections.emptySet();
        }
        int[] array = new int[arrayLength];
        int index = 0;
        for (int i = start; i < end; i++) {
            if (excludeSet.contains(i)) {
                continue;
            }
            array[index ++] = i;
        }
        if (arrayLength == 1) {
            return Collections.singleton(array[0]);
        }

        TreeSet<Integer> list = new TreeSet<>();
        for (int i = 0; i < count; i++) {
            // 随机一个索引
            int randomIndex = getRandomCount(seed, keyword, 0, arrayLength - i);
            int temp = array[randomIndex];
            // 将该随机数放入列表
            list.add(temp);
            // 将该数和最后数交换位置
            array[randomIndex] = array[arrayLength - i - 1];
            array[arrayLength - i - 1] = temp;
        }
        return list;
    }

	/**
	 * 在一定范围内随机多个不重复的数
	 * @param randomSeq 序列
	 * @param seed 种子
	 * @param start 最小数，包括
	 * @param end 最大数，不包括
	 * @param count 随机数的数量，不能大于 end - start
	 * @param excludeSet 排除的数，不允许随机到
	 * @return 随机出的数的集合
	 */
	public static Collection<Integer> getRandomIntegers(AtomicInteger randomSeq, String seed, int start, int end, int count, Set<Integer> excludeSet) {
		int arrayLength = end - start - excludeSet.size();
		if (arrayLength <= 0) {
			return Collections.emptySet();
		}
		int[] array = new int[arrayLength];
		int index = 0;
		for (int i = start; i < end; i++) {
			if (excludeSet.contains(i)) {
				continue;
			}
			array[index ++] = i;
		}
		if (arrayLength == 1) {
			return Collections.singleton(array[0]);
		}

		TreeSet<Integer> list = new TreeSet<>();
		for (int i = 0; i < count; i++) {
			// 随机一个索引
			int randomIndex = getRandomCount(randomSeq, seed, 0, arrayLength - i);
			int temp = array[randomIndex];
			// 将该随机数放入列表
			list.add(temp);
			// 将该数和最后数交换位置
			array[randomIndex] = array[arrayLength - i - 1];
			array[arrayLength - i - 1] = temp;
		}
		return list;
	}

	/**
	 * 随机一个整数
	 * @param start 随机下限，包括
	 * @param end 随机上限，不包括
	 * @return 随机整数
	 */
	public static int getRandomNextInt(int start, int end) {
		return getRandom().nextInt(end - start) + start;
	}

	/**
	 * 随机一个整数
	 * @param start 随机下限，包括
	 * @param end 随机上限，不包括
	 * @return 随机整数
	 */
	public static int getRandomNextInt(Long start, Long end) {
		return getRandom().nextInt(end.intValue() - start.intValue()) + start.intValue();
	}

	/**
	 * 在一定范围内随机多个不重复的数
	 * @param start 最小数，包括
	 * @param end 最大数，不包括
	 * @param count 随机数的数量，不能大于 end - start
	 * @return 随机出的数的集合
	 */
	public static Collection<Integer> getRandomIntegers(int start, int end, int count) {
		return getRandomIntegers(start, end, count, Collections.emptySet());
	}

	/**
	 * 在一定范围内随机多个不重复的数
	 * @param start 最小数，包括
	 * @param end 最大数，不包括
	 * @param count 随机数的数量，不能大于 end - start
	 * @param excludeSet 排除的数，不允许随机到
	 * @return 随机出的数的集合
	 */
	public static Collection<Integer> getRandomIntegers(int start, int end, int count, Set<Integer> excludeSet) {
		Random random = getRandom();
		int arrayLength = end - start - excludeSet.size();
		if (arrayLength <= 0) {
			return Collections.emptySet();
		}
		int[] array = new int[arrayLength];
		int index = 0;
		for (int i = start; i < end; i++) {
			if (excludeSet.contains(i)) {
				continue;
			}
			array[index ++] = i;
		}
		if (arrayLength == 1) {
			return Collections.singleton(array[0]);
		}

		TreeSet<Integer> list = new TreeSet<>();
		for (int i = 0; i < count; i++) {
			// 随机一个索引
			int randomIndex = random.nextInt(arrayLength - i);
			int temp = array[randomIndex];
			// 将该随机数放入列表
			list.add(temp);
			// 将该数和最后数交换位置
			array[randomIndex] = array[arrayLength - i - 1];
			array[arrayLength - i - 1] = temp;
		}
		return list;
	}
	/**
	 * 在一定范围内随机多个不重复的数
	 * @param start 最小数，包括
	 * @param end 最大数，不包括
	 * @param count 随机数的数量，不能大于 end - start
	 * @param exclude 排除的数，不允许随机到
	 * @return 随机出的数的集合
	 */
	public static Collection<Integer> getRandomIntegers(int start, int end, int count, Integer... exclude) {
		Set<Integer> excludeSet;
		if (exclude != null && exclude.length > 0) {
			excludeSet = new HashSet<>(exclude.length,  1.5f);
			Collections.addAll(excludeSet, exclude);
		} else {
			excludeSet = Collections.emptySet();
		}
		return getRandomIntegers(start, end, count, excludeSet);
	}

	/**
	 * 随机一个满足条件的数组
	 * @param seed 随机种子
	 * @param key 随机种子
	 * @param minNum 数组每个元素的最小值
	 * @param maxNum 数组每个元素的最大值
	 * @param totalMinNum 数组所有元素之和的最小值
	 * @param totalMaxNum 数组所有元素之和的最大值
	 * @param count 数组的长度
	 * @return 随机数组
	 */
	public static int[] getRandomArray(String seed, String key, int minNum, int maxNum, int totalMinNum, int totalMaxNum, int count) {
		// 校验合法性
		if (totalMinNum < minNum * count) {
			//throw new RuntimeException("totalMinNum 过低");
			// 进行修正
			totalMinNum = minNum * count;
		}
		if (totalMaxNum > maxNum * count) {
			//throw new RuntimeException("totalMaxNum 过高");
			// 进行修正
			totalMaxNum = maxNum * count;
		}
		// 最终结果
		int[] resultArray = new int[count];
		// 已经随机完的人数
		int tempCount = 0;
		// 已经随机过的索引
		Set<Integer> tempIndexSet = new HashSet<>();
		for (int i = 0; i < count; i++) {
			// 随机索引
			Collection<Integer> randomIntegers = getRandomIntegers(seed, key, 0, count, 1, tempIndexSet);
			int index = randomIntegers.iterator().next();
			tempIndexSet.add(index);
			// 还剩多少人数可以随机
			int leftCount = totalMaxNum - tempCount;
			// 还剩有几个区域没有随机，包括当前区域
			int leftAreaCount = count - i;
			// 除去其他区域保底的人数，可以有多少人数可以随机
			int finalLeft = leftCount - minNum * leftAreaCount;
			// 以最大值分配需要多少人数
			int max = maxNum * leftAreaCount;
			int randomCount;
			if (max <= totalMinNum - tempCount) {
				randomCount = maxNum;
			} else {
				// 本次可随机的最大值与最小值的差
				int delta = Math.min(finalLeft, maxNum - minNum);
				// 随机
				randomCount = getRandomCount(seed, key, minNum, minNum + delta + 1);
			}
			tempCount += randomCount;
			// 赋值
			resultArray[index] = randomCount;
		}
		// 如果随机后的总人数比最小总人数还小，则随机给已经随机的结果加数值
		if (tempCount < totalMinNum) {
			int min = totalMinNum - tempCount;
			// 随机一个加分总数
			int addScore = getRandomCount(seed, key, min, totalMaxNum - totalMinNum + min + 1);
			if (addScore % count == 0) {
				int score = addScore / count;
				// 加的分数正好整除，也不用随机了，所有数值都加同样的分数
				for (int i = 0; i < resultArray.length; i++) {
					resultArray[i] = resultArray[i] + score;
				}
			} else {
                // 加分值
                int score = addScore / count + 1;
                // 清理set，再用一次
                tempIndexSet.clear();
                // 加的分数
                int tempScore = 0;
                for (int i = 0; i < resultArray.length; i++) {
                    Collection<Integer> randomIntegers = getRandomIntegers(seed, key, 0, count, 1, tempIndexSet);
                    int index = randomIntegers.iterator().next();
                    int temp = Math.min(addScore - tempScore, score);
                    resultArray[index] = resultArray[index] + temp;
                    tempScore += temp;
                    // 这个值就没必要累加了
                    //tempCount += temp;
                    if (tempScore >= addScore) {
                        break;
                    }
                }
            }
		}
		return resultArray;
	}
}
