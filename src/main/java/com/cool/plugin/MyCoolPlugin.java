package com.cool.plugin;

import com.cool.core.annotation.CoolPlugin;
import com.cool.core.plugin.BaseCoolPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * - 类必须要有 @CoolPlugin 注解，一个插件jar有且只能有一个类被 @CoolPlugin 注解
 * - 类必须继承 BaseCoolPlugin, 并实现 invokePlugin
 * 插件方法
 */
@Slf4j
@CoolPlugin
public class MyCoolPlugin extends BaseCoolPlugin {

    @Override
    public Object invokePlugin(String... params) {
        System.out.println("Hello invokePlugin");
        useCache();
        return "Hello invokePlugin";
    }

    /**
     * 使用缓存，使用cool-admin的缓存，开发的时候只是模拟
     */
    private void useCache() {
        Object cache =
            invokeMain("coolCache", "get", "a");
        System.out.println("缓存结果：" + cache);
    }
}
