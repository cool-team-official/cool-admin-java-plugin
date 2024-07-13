package com.cool.plugin;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.cool.core.annotation.CoolPlugin;
import com.cool.core.plugin.BaseCoolPlugin;
import java.io.InputStream;
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
        usePlugin();
        getParentResource();
        return "Hello invokePlugin";
    }

    /**
     * 使用缓存，使用cool-admin的缓存，开发的时候只是模拟
     */
    private void useCache() {
        // 调用主应用设置缓存
        invokeMain("coolCache", "set", "a", "一个项目用COOL就够了");
        // 调用主应用获取缓存
        Object cache =
            invokeMain("coolCache", "get", "a");
        System.out.println("缓存结果：" + cache);
    }

    /**
     * 调用其他插件
     */
    private void usePlugin() {
        // 获得其他插件，开发的时候无法调试，只有安装到cool-admin中才能调试  xxx 为插件key
        Object result = invokeOtherPlugin("aliyun-oss", "invokePlugin");
        System.out.println(result);
    }

    /**
     * 获取主应用resources目录下的资源文件
     */
    private void getParentResource() {
        ClassLoader classLoader = getClass().getClassLoader();
        ClassLoader parentClassLoader = classLoader.getParent();
        // 获取主应用resources下的 banner.txt 文件内容
        InputStream inputStream = parentClassLoader.getResourceAsStream("banner.txt");
        System.out.println(StrUtil.str(IoUtil.readBytes(inputStream), "UTF-8"));
    }
}
