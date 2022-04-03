package io.renren.modules.clients.fallbact;

import io.renren.modules.clients.ConsumeFeignService;
import io.renren.modules.job.task.vo.OrderVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hxx
 * @date 2022/3/30 16:08
 */
@Component
public class FallbackService implements ConsumeFeignService {

    @Override
    public List<OrderVo> getTimeOrders(long timeout, long currentTime) {
        return null;
    }
}
