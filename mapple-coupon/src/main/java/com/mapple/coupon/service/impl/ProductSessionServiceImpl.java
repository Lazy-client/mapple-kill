package com.mapple.coupon.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mapple.common.exception.RRException;
import com.mapple.common.utils.RedisKeyUtils;
import com.mapple.common.utils.RedisUtils;
import com.mapple.coupon.dao.ProductDao;
import com.mapple.coupon.dao.SessionDao;
import com.mapple.coupon.entity.ProductEntity;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.entity.vo.productSessionVo;
import com.mapple.coupon.entity.vo.productSessionVo_Skus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapple.common.utils.PageUtils;
import com.mapple.common.utils.Query;

import com.mapple.coupon.dao.ProductSessionDao;
import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.service.ProductSessionService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("productSessionService")
public class ProductSessionServiceImpl extends ServiceImpl<ProductSessionDao, ProductSessionEntity> implements ProductSessionService {

    @Autowired
    public SessionDao sessionDao;

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductSessionDao productSessionDao;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    /**
     * redis操作hash
     */
    @Resource
    private HashOperations<String, String, Object> hashOperations;

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
    @Transactional
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
            //判断有无重复商品
            String productId=null;
            ProductEntity productEntity_fromdb = productDao.selectOne(new QueryWrapper<ProductEntity>().eq("product_name", product.getProductName()));

            if (null==productEntity_fromdb) {
                int insert_product = productDao.insert(product);
                if (insert_product != 1) {
                    throw new RRException("插入数据库出错");
                }
                //存入成功，取出productId
                productId = product.getId();
            }else {
                productId=productEntity_fromdb.getId();
            }
            QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<ProductSessionEntity>().eq("session_id", sessionId).eq("product_id", productId);
            if (productSessionDao.selectCount(productSessionEntityQueryWrapper)==0){
                //然后封装数据到productsession关联表
                ProductSessionEntity productSessionEntity = new ProductSessionEntity();
                //将数据封装到productsession对象中
                BeanUtils.copyProperties(productSessionVo, productSessionEntity);
                productSessionEntity.setProductId(productId);
                productSessionEntity.setIsDeleted(0);
                //插入数据库中
                if (productSessionDao.insert(productSessionEntity) == 1) {
                    // 1.放入redis中缓存一份sku
                    String redisKey = sessionId + "-" + productId;
                    productSessionVo.setProductId(productId);
                    //重新放入
                    productSessionVo.setProductEntity(product);
                    productSessionVo.setSessionName(sessionEntity.getName());
                    BeanUtils.copyProperties(sessionEntity, productSessionVo);
                    //调用存入redis的方法
                    //redis hash操作绑定sku_prefix
                    BoundHashOperations<String, Object, Object> operationsForSku = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
                    //生成随机码
                    String token = UUID.randomUUID().toString().replace("-", "");
                    if (!operationsForSku.hasKey(redisKey)) {
                        //设置商品的随机码（防止恶意攻击）
                        productSessionVo.setRandomCode(token);
                        //用fastJson序列化json格式
                        String seckillValue = JSON.toJSONString(productSessionVo);
                        operationsForSku.put(redisKey, seckillValue);
                    }

                    // 2.再放入redis中缓存skus信息
                    BoundHashOperations<String, Object, Object> operationsForSkus = redisTemplate.boundHashOps(RedisKeyUtils.SKUS_PREFIX);
                    //如果K中不存在这个sessionId
                    if (!operationsForSkus.hasKey(sessionId)) {
                        //存放多个sku信息的list
                        ArrayList<productSessionVo_Skus> productSessionVo_skus_list = new ArrayList<>();
                        productSessionVo_Skus productSessionVo_skus = new productSessionVo_Skus();
                        BeanUtils.copyProperties(productSessionVo, productSessionVo_skus);
                        BeanUtils.copyProperties(productEntity, productSessionVo_skus);
                        productSessionVo_skus_list.add(productSessionVo_skus);
                        operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skus_list));
                    }else {
                        //如果存在这个sessionId
                        //取出list并添加商品信息
                        JSONArray objects = JSONArray.parseArray(operationsForSkus.get(sessionId).toString());
                        List<productSessionVo_Skus> productSessionVo_skuses = objects.toJavaList(productSessionVo_Skus.class);
                        int flag=0;
                        for (productSessionVo_Skus productSessionVo_skus : productSessionVo_skuses) {
                            if (productSessionVo_skus.getProductName()==product.getProductName()){
                                flag=1;
                            }
                        }
                        if (productSessionVo_skuses!=null&&productSessionVo_skuses.size()>=1&&flag==0){
                            productSessionVo_Skus productSessionVo_skus = new productSessionVo_Skus();
                            BeanUtils.copyProperties(productSessionVo, productSessionVo_skus);
                            BeanUtils.copyProperties(productEntity, productSessionVo_skus);
                            productSessionVo_skuses.add(productSessionVo_skus);
                            operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skuses));
                        }
                    }
                    return "ok";
                }
            }else {
                throw new RRException("数据插入重复");
            }
        }
        return null;
    }
}