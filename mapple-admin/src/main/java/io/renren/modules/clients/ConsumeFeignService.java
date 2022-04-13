package io.renren.modules.clients;

import io.renren.common.utils.PageUtils;
import io.renren.modules.clients.fallbact.FallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/3 17:50
 */
@FeignClient(value = "mapple-consumer",fallback = FallbackService.class)
@Service
public interface ConsumeFeignService {
   @GetMapping("/consume/mk-order/getTimeOrders")
   List<String> getTimeOrders(@RequestParam Long timeout,@RequestParam Long currentTime);

   /*
    * 订单接口，供管理员使用
    */
   @GetMapping("/consume/mk-order/listForAdmin")
   PageUtils listForAdmin(@RequestParam Map<String, Object> params);
}
