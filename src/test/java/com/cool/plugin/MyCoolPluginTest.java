package com.cool.plugin;

import cn.hutool.extra.spring.SpringUtil;
import com.cool.CoolPluginApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CoolPluginApplication.class)
class MyCoolPluginTest {

    @Test
    void runTestPlugin() {
        // 可以在这边调试运行插件，
        // 注意在这运行无法直接调用 主应用方法：invokeMain  和 其他插件方法：invokeOtherPlugin
        // 这个两个需要在主应用加载后调试
        MyCoolPlugin myCoolPlugin = new MyCoolPlugin();
        myCoolPlugin.setApplicationContext(SpringUtil.getApplicationContext());
//        myCoolPlugin.invokePlugin();
    }

}
