### 介绍

替换系统的文件上传服务，使用阿里云 oss 作为文件上传服务。

### 标识

调用插件的时候需要用到标识，标识是唯一的，不能重复，建议使用英文，不要使用中文，对应插件 `plugin.json` 中的 `key` 字段

- 标识：upload-oss（注：本插件时 hook 插件，调用时使用标识：upload）
- Hook：upload （替换系统的 upload）

### 配置

```json
{
  "accessKeyId": "必须，阿里云accessKeyId",
  "accessKeySecret": "必须，阿里云accessKeySecret",
  "bucket": "必须，阿里云bucket",
  "endpoint": "必须，阿里云endpoint",
  "expAfter": "可选，签名失效时间，毫秒，默认30000（30秒）",
  "maxSize": "可选， 文件最大的 size 字节，默认50M"
}
```

### 注意

前端签名直传用于浏览器安全限制，在传输的时候会有跨域访问的情况，因此需要到阿里云 oss 管理，添加跨域规则。

`authorization` 这一项不可省略，严格按照截图所示配置，替换成自己的域名即可

![跨域配置](https://cool-service.oss-cn-shanghai.aliyuncs.com/app/base/1ed8aef9ca354f2b8f9aef031bbf7c1a_image.png)

### 方法

下面是插件提供的一些方法

```java
 public Object upload();
```

### 调用示例

```java
@Resource
private CoolPluginService coolPluginService;

coolPluginService.invoke("ossUploadPlugin", "upload");
```

### 更新日志

- v1.0.0 (2024-6-28)
    - 初始版本
