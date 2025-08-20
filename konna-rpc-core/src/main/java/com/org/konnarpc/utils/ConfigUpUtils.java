package com.org.konnarpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件工具类
 */
public class ConfigUpUtils {

    /**
     * 加载配置文件
     *
     * @param tClass 类型
     * @param prefix 前置
     * @param <T> 泛型
     * @return 配置类
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix){
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置文件，properties优先级高于yml
     *
     * @param tClass 类型
     * @param prefix 前置
     * @param env 环境
     * @param <T> 泛型
     * @return 配置类
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String env){
        // 构建配置文件名
        StringBuilder configFileBase = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)){
            configFileBase.append("-").append(env);
        }

        // 先尝试加载properties文件
        String propertiesFile = configFileBase.toString() + ".properties";
        Props props = tryLoadProperties(propertiesFile);

        // 如果properties文件不存在或加载失败，尝试加载yml文件
        if (props == null) {
            String ymlFile = configFileBase.toString() + ".yml";
            props = tryLoadYml(ymlFile);
        }

        // 如果yml也不存在，尝试yaml后缀
        if (props == null) {
            String yamlFile = configFileBase.toString() + ".yaml";
            props = tryLoadYml(yamlFile);
        }

        // 如果都不存在，返回默认实例
        if (props == null) {
            try {
                return tClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("无法创建配置类实例: " + tClass.getName(), e);
            }
        }

        return props.toBean(tClass, prefix);
    }

    /**
     * 尝试加载properties文件
     *
     * @param fileName 文件名
     * @return Props对象，如果加载失败返回null
     */
    private static Props tryLoadProperties(String fileName) {
        try {
            // 检查资源是否存在
            InputStream inputStream = ConfigUpUtils.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                inputStream.close();
                return new Props(fileName);
            }
        } catch (Exception e) {
            // 忽略异常，返回null表示加载失败
        }
        return null;
    }

    /**
     * 尝试加载yml文件
     *
     * @param fileName 文件名
     * @return Props对象，如果加载失败返回null
     */
    private static Props tryLoadYml(String fileName) {
        try {
            InputStream inputStream = ConfigUpUtils.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                // 使用简单的Constructor而不是CustomClassLoaderConstructor
                LoaderOptions loaderOptions = new LoaderOptions();
                Constructor constructor = new Constructor(loaderOptions);
                Yaml yaml = new Yaml(constructor);
                Map<String, Object> yamlMap = yaml.load(inputStream);
                inputStream.close();

                // 将YAML映射转换为Properties
                Properties properties = new Properties();
                convertYamlMapToProperties(properties, yamlMap, "");

                // 创建Props对象
                Props props = new Props();
                props.putAll(properties);
                return props;
            }
        } catch (Exception e) {
            // 忽略异常，返回null表示加载失败
        }
        return null;
    }

    /**
     * 递归将YAML映射转换为Properties
     *
     * @param properties Properties对象
     * @param yamlMap YAML映射
     * @param prefix 前缀
     */
    @SuppressWarnings("unchecked")
    private static void convertYamlMapToProperties(Properties properties, Map<String, Object> yamlMap, String prefix) {
        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (value instanceof Map) {
                // 递归处理嵌套映射
                convertYamlMapToProperties(properties, (Map<String, Object>) value, fullKey);
            } else {
                // 添加属性
                properties.setProperty(fullKey, value != null ? value.toString() : "");
            }
        }
    }
}
