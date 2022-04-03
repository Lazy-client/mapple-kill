package io.renren.modules.clients.fallbact;

import io.renren.modules.clients.ConsumeFeignService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zsc
 * @date 2022/3/30 16:08
 */
@Component
public class FallbackService implements ConsumeFeignService {

    @Override
    public List<String> getTimeOrders(Long timeout, Long currentTime) {
        return null;
    }
}
