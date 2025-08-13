package com.org.konnarpc.registry;

import com.org.konnarpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 注册中心服务本地缓存
 */
@Slf4j
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写入缓存
     *
     * @param newServiceCache 新的缓存
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache = newServiceCache;
    }

    /**
     * 获取缓存
     *
     * @return 缓存
     */
    List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache(){
        this.serviceCache = null;
        log.info("清空缓存");
    }

}
