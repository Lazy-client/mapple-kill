package io.renren.modules.clients;

import io.renren.modules.clients.fallbact.FallbackService;
import io.renren.modules.job.task.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/3 17:50
 */
@FeignClient(value = "mapple-coupon",fallback = FallbackService.class)
public interface ConsumeFeignService {
   // todo 加上路径
   List<OrderVo> getTimeOrders(long timeout,long currentTime);
}
