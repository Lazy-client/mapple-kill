package io.renren.modules.clients.fallbact;

import io.renren.common.utils.PageUtils;
import io.renren.modules.clients.ConsumeFeignService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    @Override
    public PageUtils listForAdmin(Map<String, Object> params) {
        return null;
    }
}
