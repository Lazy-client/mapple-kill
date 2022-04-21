package io.renren.modules.app.utils;

import java.util.Objects;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheUtil {

    @Autowired
    private CacheManager cacheManager;

    private static CacheManager cm;

    @PostConstruct
    public void init() {
        cm = cacheManager;
    }

    /**
     * 添加缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param value     缓存值
     */
    public static void put(String cacheName, String key, Object value) {
        Cache cache = cm.getCache(cacheName);
        cache.put(key, value);
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @return
     */
    public static Object get(String cacheName, String key) {
        Cache cache = cm.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        return Objects.requireNonNull(cache.get(key)).get();
    }

    /**
     * 获取缓存（字符串）
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @return
     */
    public static String getString(String cacheName, String key) {
        Cache cache = cm.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper == null) {
            return null;
        }
        return Objects.requireNonNull(wrapper.get()).toString();
    }

    /**
     * 获取缓存（泛型）
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param clazz     缓存类
     * @param <T>       返回值泛型
     * @return
     */
    public static <T> T get(String cacheName, String key, Class<T> clazz) {
        Cache cache = cm.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper == null) {
            return null;
        }
        return (T) wrapper.get();
    }

    /**
     * 失效缓存
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     */
    public static void evict(String cacheName, String key) {
        Cache cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
