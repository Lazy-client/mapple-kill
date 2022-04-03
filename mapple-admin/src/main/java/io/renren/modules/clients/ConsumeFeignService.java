package io.renren.modules.clients;

import io.renren.modules.clients.fallbact.FallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/3 17:50
 */
@FeignClient(value = "mapple-consume",fallback = FallbackService.class)
@Service
public interface ConsumeFeignService {
   @GetMapping("/consume/mk-order/getTimeOrders")
   List<String> getTimeOrders(long timeout,long currentTime);
}
