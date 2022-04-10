package com.mapple.consume.mapper;

import com.mapple.consume.entity.MkOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author Gelcon
 * @since 2022-03-19
 */
public interface MkOrderMapper extends BaseMapper<MkOrder> {

    List<MkOrder> getBySnBatch(List<String> orderSnList);

    int removeBatchBySnList(List<String> orderSnList);
}
