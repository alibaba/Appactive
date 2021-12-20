package io.appactive.java.api.base.enums;

import java.util.HashMap;
import java.util.Map;

public enum PriorityLevel implements IEnum<Integer> {
    /**
     * level 1
     */
    LevelOne(1),
    /**
     * level 2
     */
    LevelTwo(2),
    /**
     * level 3
     */
    LevelThree(3),
    /**
     * level 4
     */
    LevelFour(4),
    /**
     * level 5
     */
    LevelFive(5);

    private int level;

    /**
     * 状态上下文
     */
    private static final Map<Integer, PriorityLevel> LEVEL_MAP = new HashMap<Integer, PriorityLevel>();

    static {
        for (PriorityLevel level : PriorityLevel.values()) {
            LEVEL_MAP.put(level.getLevel(), level);
        }
    }

    PriorityLevel(int level) {
        this.level = level;
    }

    public static PriorityLevel get(int level) {
        return LEVEL_MAP.get(level);
    }

    @Override
    public Integer getValue() {
        return this.level;
    }

    /**
     * level是否完全与当前Level对象相同
     *
     * @param level
     * @return
     */
    public boolean identical(int level) {
        PriorityLevel status = LEVEL_MAP.get(level);
        if (this == status) {
            return true;
        }
        return false;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

}
