package com.latte.menu.cache;

import com.latte.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheScheduler {

    private final MenuService menuService;

    // 매일 14시에 캐시( 인기 검색어 ) 초기화
    @Scheduled(cron = "0 0 14 * * *")
    public void updateCache() {
        menuService.clearCache();
    }
}
