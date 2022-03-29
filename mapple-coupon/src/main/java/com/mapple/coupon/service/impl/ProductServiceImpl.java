package com.mapple.coupon.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mapple.common.exception.RRException;
import com.mapple.common.utils.redis.cons.RedisKeyUtils;
import com.mapple.coupon.dao.ProductSessionDao;
import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.entity.vo.productSessionVo_Skus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.ProductDao;
import com.mapple.coupon.entity.ProductEntity;
import com.mapple.coupon.service.ProductService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductDao, ProductEntity> implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductSessionDao productSessionDao;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductEntity> page = this.page(
                new Query<ProductEntity>().getPage(params),
                new QueryWrapper<ProductEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void saveProduct(ProductEntity product) {
        checkRiskLevel(product);
        if (checkDepositTime(product)) {
            save(product);
        }
    }

    @Override
    @Transactional
    public void updateProductById(productSessionVo_Skus productSessionVo_Skus) {
        //如果数据库更新成功，则更新redis数据
        ProductEntity product = new ProductEntity();
        //转到product上面
        BeanUtils.copyProperties(productSessionVo_Skus, product);
        //校验product中修改字段的格式
        if (!StringUtils.isEmpty(product.getRiskLevel())) {
            checkRiskLevel(product);
        }
        if (!StringUtils.isEmpty(product.getDepositTime())) {
            checkDepositTime(product);
        }
        //更新操作
        productDao.updateById(product);

        //再判断秒杀价格或库存有没改变
        if ((!StringUtils.isEmpty(productSessionVo_Skus.getSeckillPrice()) || (!StringUtils.isEmpty(productSessionVo_Skus.getTotalCount())))) {
            ProductSessionEntity productSessionEntity = new ProductSessionEntity();
            BeanUtils.copyProperties(productSessionVo_Skus, productSessionEntity);
            //更新关联表信息
            productSessionDao.update(productSessionEntity
                    ,new QueryWrapper<ProductSessionEntity>()
                            .eq("session_id",productSessionEntity.getSessionId())
                            .eq("product_id",productSessionEntity.getProductId()));
        }

        //更新redis的数据
        //1. redis hash操作修改sku的数据
        BoundHashOperations<String, String, String> operationsForSku = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
        //获取rediskey
        String sessionId = productSessionVo_Skus.getSessionId();
        String redisKey =  sessionId + "-" + productSessionVo_Skus.getProductId();
        //从redis获取之前的vo数据
        if (operationsForSku.hasKey(redisKey)) {
            productSessionVo_Skus productSessionVo_skus_fromRedis = JSON.parseObject(operationsForSku.get(redisKey), productSessionVo_Skus.class);
            //将修改后的值注到之前的vo中 ,后面的指的是忽略空值
            assert productSessionVo_skus_fromRedis != null;
            BeanUtil.copyProperties(productSessionVo_Skus, productSessionVo_skus_fromRedis, new CopyOptions().setIgnoreNullValue(true));
            operationsForSku.put(redisKey, //用fastJson序列化json格式
                    JSON.toJSONString(productSessionVo_skus_fromRedis));
        }

        //2. skus修改
        String randomCode=null;
        BoundHashOperations<String, String, String> operationsForSkus = redisTemplate.boundHashOps(RedisKeyUtils.SKUS_PREFIX);
        //如果存在该场次，则继续操作
        if (operationsForSkus.hasKey(sessionId)) {
            String skus_fromRedis = operationsForSkus.get(sessionId);
            List<productSessionVo_Skus> skusList = JSONObject.parseArray(skus_fromRedis, productSessionVo_Skus.class);
            for (int i = 0; i < skusList.size(); i++) {
                if (skusList.get(i).getProductId().equals(productSessionVo_Skus.getProductId())){
                    productSessionVo_Skus vo_skus = skusList.get(i);
                    BeanUtil.copyProperties(productSessionVo_Skus,vo_skus, new CopyOptions().setIgnoreNullValue(true));
                    skusList.set(i,vo_skus);
                    //取出随机码randomcode
                    randomCode = vo_skus.getRandomCode();
                }
            }
            operationsForSkus.delete(sessionId);
            operationsForSkus.put(sessionId,JSON.toJSONString(skusList));
        }

        //3.stock修改库存
        //获取库存数量
        Integer stockCount = productSessionVo_Skus.getTotalCount();
        if (!StringUtils.isEmpty(stockCount)){
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            opsForValue.set(RedisKeyUtils.STOCK_PREFIX+randomCode,
                    stockCount.toString());
        }
    }


    /**
     * 校验存期格式
     *
     * @param product
     */
    private Boolean checkDepositTime(ProductEntity product) {
        String depositTime = product.getDepositTime();
        String[] strs = depositTime.split("-");
        int year = Integer.parseInt(strs[0]);
        int month = Integer.parseInt(strs[1]);
        if ((year == 0 && month > 0) || (year > 0 && year < 100 && month == 0)) {
            return true;
        } else {
            throw new RRException("存期格式错误");
        }
    }

    /**
     * 校验风险等级的格式
     *
     * @param product
     */
    private void checkRiskLevel(ProductEntity product) {
        Integer riskLevel = product.getRiskLevel();
        if (riskLevel != 1 && riskLevel != 2 && riskLevel != 3) {
            throw new RRException("风险等级格式错误");
        }
    }


    //2. 修改库存数量

}
