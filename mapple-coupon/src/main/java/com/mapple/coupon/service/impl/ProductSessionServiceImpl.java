package com.mapple.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mapple.common.utils.RedisKeyUtils;
import com.mapple.coupon.dao.ProductDao;
import com.mapple.coupon.dao.SessionDao;
import com.mapple.coupon.entity.ProductEntity;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.entity.vo.productSessionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.ProductSessionDao;
import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.service.ProductSessionService;


@Service("productSessionService")
public class ProductSessionServiceImpl extends ServiceImpl<ProductSessionDao, ProductSessionEntity> implements ProductSessionService {

    @Autowired
    public SessionDao sessionDao;

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductSessionDao productSessionDao;

    @Autowired
    public RedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<>();
        //获取场地id
        String sessionId = (String) params.get("sessionId");
        //判空
        if (!StringUtils.isBlank(sessionId)) {
            productSessionEntityQueryWrapper.eq("session_id", sessionId);
        }
        IPage<ProductSessionEntity> page = this.page(
                new Query<ProductSessionEntity>().getPage(params),
                productSessionEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据场次id存对应的产品
     *
     * @param productSessionVo 要传入sessionId ,product对象，totalcount
     * @return
     */
    @Override
    public String saveProductSession(productSessionVo productSessionVo) {
        //首先获取场次的Id
        String sessionId = productSessionVo.getSessionId();
        SessionEntity sessionEntity = sessionDao.selectById(sessionId);
        //session表中有对应场次，则存对应产品，否则返回错误
        if (null != sessionEntity) {
            //将数据封装到product对象中
            ProductEntity product = new ProductEntity();
            //获取vo中的productentity对象
            ProductEntity productEntity = productSessionVo.getProductEntity();
            product.setProductName(productEntity.getProductName());
            product.setDescription(productEntity.getDescription());
            product.setDefaultImg(productEntity.getDefaultImg());
            product.setTitle(productEntity.getTitle());
            product.setIsDeleted(0);

            //插入数据库
            if (productDao.insert(product) == 1) {
                //存入成功，取出productId
                String productId = product.getId();
                //然后封装数据到productsession关联表
                ProductSessionEntity productSessionEntity = new ProductSessionEntity();
                //将数据封装到productsession对象中
                BeanUtils.copyProperties(productSessionVo, productSessionEntity);
                productSessionEntity.setProductId(productId);
                productSessionEntity.setIsDeleted(0);
                //插入数据库中
                if (productSessionDao.insert(productSessionEntity) == 1) {
                    //再放入redis中缓存一份
                    String redisKey = sessionId + "-" + productId;
                    productSessionVo.setProductId(productId);
                    //重新放入
                    productSessionVo.setProductEntity(product);
                    BeanUtils.copyProperties(sessionEntity, productSessionVo);
                    //调用存入redis的方法
                    new ProductSessionServiceImpl().saveProductSession_InRedis(redisKey, productSessionVo);
                    return "ok";
                }

            }
        }
        return null;
    }

    public void saveProductSession_InRedis(String redisKey, productSessionVo productSessionVo) {
        //redis hash操作绑定sku_prefix
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
        //生成随机码
        String token = UUID.randomUUID().toString().replace("-", "");
        if (!operations.hasKey(redisKey)) {
            //设置商品的随机码（防止恶意攻击）
            productSessionVo.setRandomCode(token);
            //用fastJson序列化json格式
            String seckillValue = JSON.toJSONString(productSessionVo);
            operations.put(redisKey, seckillValue);
        }
    }
}