package io.renren.modules.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.RedisKeyUtils;
import io.renren.modules.coupon.dao.ProductDao;
import io.renren.modules.coupon.dao.ProductSessionDao;
import io.renren.modules.coupon.dao.SessionDao;
import io.renren.modules.coupon.entity.ProductEntity;
import io.renren.modules.coupon.entity.ProductSessionEntity;
import io.renren.modules.coupon.entity.SessionEntity;
import io.renren.modules.coupon.entity.vo.productSessionVo;
import io.renren.modules.coupon.entity.vo.productSessionVo_Skus;
import io.renren.modules.coupon.entity.vo.productSessionVo_new;
import io.renren.modules.coupon.entity.vo.productVo_new;
import io.renren.modules.coupon.service.ProductSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service("productSessionService")
@Slf4j
public class ProductSessionServiceImpl extends ServiceImpl<ProductSessionDao, ProductSessionEntity> implements ProductSessionService {

    @Autowired
    public SessionDao sessionDao;

    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductSessionDao productSessionDao;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisTemplate<String, String> stringRedisTemplate;

    /**
     * redis??????hash
     */
    @Resource
    private HashOperations<String, String, String> hashOperations;

    //redis hash????????????sku_prefix
    BoundHashOperations<String, Object, Object> operationsForSku;

    //redis kv????????????stock??????
    ValueOperations<String, String> opsForValue;

    /**
     * ????????????
     * @param redisTemplate
     * @param stringRedisTemplate
     */
    public ProductSessionServiceImpl(RedisTemplate<String, Object> redisTemplate,RedisTemplate<String, String> stringRedisTemplate) {
        this.operationsForSku = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
        //redis ???????????? ????????????stock
        this.opsForValue = stringRedisTemplate.opsForValue();
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<>();
        //????????????id
        String sessionId = (String) params.get("sessionId");
        //??????
        if (!StringUtils.isEmpty(sessionId)) {
            productSessionEntityQueryWrapper.eq("session_id", sessionId);
        }
        IPage<ProductSessionEntity> page = this.page(
                new Query<ProductSessionEntity>().getPage(params),
                productSessionEntityQueryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * ??????stock?????????redis?????????
     * @param token
     * @param totalCount
     */
    private void saveStockInRedis(String token, String totalCount) {
        String key = RedisKeyUtils.STOCK_PREFIX + token;
        //???????????????????????????????????????stock????????????redis
        if (StringUtils.isEmpty(opsForValue.get(key))) {
            opsForValue.set(key, totalCount);
        }
    }

    /**
     * ??????sku???redis?????????
     * @param redisKey
     * @param productSessionVo_skus1
     */
    public void saveSkuInRedis(String redisKey, productSessionVo_Skus productSessionVo_skus1) {
        //??????redis?????????
        if (!operationsForSku.hasKey(redisKey)) {
            //???fastJson?????????json??????
            String seckillValue = JSON.toJSONString(productSessionVo_skus1);
            operationsForSku.put(redisKey, seckillValue);
        }
    }

    /**
     * ?????????????????????
     * ?????????????????????????????????????????????
     *
     * @param productSessionVo_new
     * @return
     */
    @Override
    @Transactional
    public List<String> saveProductSession_new(productSessionVo_new productSessionVo_new) {
        //?????????????????????Id
        String sessionId = productSessionVo_new.getSessionId();
        SessionEntity sessionEntity = sessionDao.selectById(sessionId);
        //session???????????????????????????????????????????????????????????????
        if (null != sessionEntity) {
            //??????vo??????ArrayList<productVo_new>
            ArrayList<productVo_new> productList = productSessionVo_new.getProductList();
            //????????????id??????
            ArrayList<String> productIdList = new ArrayList<>();
            //???id?????????productIdlist???????????????
            for (int i = 0; i < productList.size(); i++) {
                productVo_new productVo_new = productList.get(i);
                productIdList.add(productVo_new.getProductId());
                //?????????????????????
                if (productVo_new.getTotalCount() == null || productVo_new.getSeckillPrice() == null) {
                    throw new RRException("???????????????????????????");
                }
                //????????????????????????????????????????????????
                Integer count = productSessionDao.selectCount(new QueryWrapper<ProductSessionEntity>().eq("session_id", sessionId).eq("product_id", productVo_new.getProductId()));
                if (count == 1) {
                    throw new RRException("???????????????????????????");
                }
            }
            List<ProductEntity> productEntities = productDao.selectBatchIds(productIdList);

            ArrayList<ProductSessionEntity> productSessionEntities = null;

            if (productEntities.size() == productIdList.size()) {
                productSessionEntities = new ArrayList<>();
                for (int i = 0; i < productEntities.size(); i++) {
                    ProductSessionEntity productSessionEntity = new ProductSessionEntity();
                    productSessionEntity.setSessionId(sessionId);
                    productSessionEntity.setProductId(productIdList.get(i));
                    productSessionEntity.setSeckillPrice(productList.get(i).getSeckillPrice());
                    productSessionEntity.setTotalCount(productList.get(i).getTotalCount());
                    productSessionEntities.add(productSessionEntity);
                }
            }
            if (!saveBatch(productSessionEntities)) {
                throw new RRException("??????????????????");
            } else {

                //redis hash????????????SKUS_PREFIX  ?????????skus
                BoundHashOperations<String, Object, Object> operationsForSkus = redisTemplate.boundHashOps(RedisKeyUtils.SKUS_PREFIX);
                //????????????skus???list
                ArrayList<productSessionVo_Skus> productSessionVo_skus_list = new ArrayList<>();
                for (int i = 0; i < productIdList.size(); i++) {
                    //???????????????productSessionVo_Skus
                    productSessionVo_Skus productSessionVo_skus1 = new productSessionVo_Skus();
                    String redisKey = sessionId + "-" + productIdList.get(i);
                    productSessionVo_skus1.setProductId(productIdList.get(i));
                    productSessionVo_skus1.setSessionId(sessionId);
                    BeanUtils.copyProperties(sessionEntity, productSessionVo_skus1);
                    BeanUtils.copyProperties(productEntities.get(i), productSessionVo_skus1);
                    BeanUtils.copyProperties(productList.get(i), productSessionVo_skus1);
                    //???????????????
                    String token = UUID.randomUUID().toString().replace("-", "");
                    productSessionVo_skus1.setRandomCode(token);

                    // 1.??????redis???????????????sku (?????????
                    saveSkuInRedis(redisKey, productSessionVo_skus1);

                    //2. ???????????????????????????stock???????????????redis??? (?????????
                    String totalCount=productList.get(i).getTotalCount().toString();
                    saveStockInRedis(token,totalCount);

                    // 3.?????????redis?????????skus??????
                    //??????K??????????????????sessionId
                    if (!operationsForSkus.hasKey(sessionId)) {
                        //????????????sku?????????list
                        productSessionVo_skus_list.add(productSessionVo_skus1);
                    } else {
                        //??????????????????sessionId
                        //??????list?????????????????????
                        JSONArray objects = JSONArray.parseArray(operationsForSkus.get(sessionId).toString());
                        productSessionVo_skus_list = (ArrayList<productSessionVo_Skus>) objects.toJavaList(productSessionVo_Skus.class);
                        int flag = 0;
                        for (productSessionVo_Skus productSessionVo_skus : productSessionVo_skus_list) {
                            if (productSessionVo_skus.getProductName().equals(productEntities.get(i).getProductName())) {
                                flag = 1;
                            }
                        }
                        if (productSessionVo_skus_list != null && productSessionVo_skus_list.size() >= 1 && flag == 0) {
                            productSessionVo_skus_list.add(productSessionVo_skus1);
                            operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skus_list));
                        }
                    }
                }
                //redis??????skus?????????
                operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skus_list));
            }
            ArrayList<String> ids = new ArrayList<>();
            for (ProductSessionEntity productSessionEntity : productSessionEntities) {
                ids.add(productSessionEntity.getId());
            }
//            ??????productsession????????????id?????????
            return ids;
        } else {
            throw new RRException("???????????????");
        }

    }


    @Override
    @Transactional
    public int deductStock(String productId, String sessionId) {
        log.info("??????????????????, productId: {}, sessionId: {}", productId, sessionId);
        return baseMapper.deductStock(productId, sessionId);
    }

    @Override
    public int refundStock(String productId, String sessionId) {
        return baseMapper.refundStock(productId, sessionId);
    }


    /**
     * ?????????save??????
     *
     * @param productSessionVo
     * @return
     */
    @Override
    @Transactional
    public String saveProductSession(productSessionVo productSessionVo) {
        //?????????????????????Id
        String sessionId = productSessionVo.getSessionId();
        SessionEntity sessionEntity = sessionDao.selectById(sessionId);
        //session???????????????????????????????????????????????????????????????
        if (null != sessionEntity) {
            //??????????????????product?????????
            ProductEntity product = new ProductEntity();
            //??????vo??????productentity??????
            ProductEntity productEntity = productSessionVo.getProductEntity();
//        ArrayList<String> productIdList=productSessionVo.getProductList();
//        ???????????????????????????????????????????????????????????????
            if (StringUtils.isEmpty(productEntity.getProductName())) {
                throw new RRException("??????????????????");
            }
            if (StringUtils.isEmpty(productEntity.getDescription())) {
                throw new RRException("??????????????????");
            }
            if (StringUtils.isEmpty(productEntity.getTitle())) {
                throw new RRException("??????????????????");
            }
            BeanUtils.copyProperties(productEntity, product);
            String productId;
            ProductEntity productEntity_fromdb = productDao.selectOne(new QueryWrapper<ProductEntity>().eq("product_name", product.getProductName()));

            if (null == productEntity_fromdb) {
                int insert_product = productDao.insert(product);
                if (insert_product != 1) {
                    throw new RRException("?????????????????????");
                }
                //?????????????????????productId
                productId = product.getProductId();
            } else {
                productId = productEntity_fromdb.getProductId();
            }
            QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<ProductSessionEntity>().eq("session_id", sessionId).eq("product_id", productId);
            if (productSessionDao.selectCount(productSessionEntityQueryWrapper) == 0) {
                //?????????????????????productsession?????????
                ProductSessionEntity productSessionEntity = new ProductSessionEntity();
                //??????????????????productsession?????????
                BeanUtils.copyProperties(productSessionVo, productSessionEntity);
                productSessionEntity.setProductId(productId);
//                productSessionEntity.setIsDeleted(0);
                //??????????????????
                if (productSessionDao.insert(productSessionEntity) == 1) {
                    // 1.??????redis???????????????sku
                    productSessionVo_Skus productSessionVo_skus1 = new productSessionVo_Skus();
                    String redisKey = sessionId + "-" + productId;
                    productSessionVo_skus1.setProductId(productId);
                    //????????????
                    BeanUtils.copyProperties(sessionEntity, productSessionVo_skus1);
                    BeanUtils.copyProperties(productSessionEntity, productSessionVo_skus1);
                    BeanUtils.copyProperties(product, productSessionVo_skus1);
                    //??????redis?????????
                    //redis hash????????????sku_prefix
                    BoundHashOperations<String, Object, Object> operationsForSku = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
                    //???????????????
                    String token = UUID.randomUUID().toString().replace("-", "");
                    if (!operationsForSku.hasKey(redisKey)) {
                        //????????????????????????????????????????????????
                        productSessionVo_skus1.setRandomCode(token);
                        //???fastJson?????????json??????
                        String seckillValue = JSON.toJSONString(productSessionVo_skus1);
                        operationsForSku.put(redisKey, seckillValue);
                    }

                    //2. ??????????????????????????????????????????redis???
                    ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
                    String key = RedisKeyUtils.STOCK_PREFIX + token;
                    //???????????????????????????????????????stock????????????redis
                    if (StringUtils.isEmpty(opsForValue.get(key))) {
                        opsForValue.set(key, productSessionVo.getTotalCount().toString());
                    }

                    // 3.?????????redis?????????skus??????
                    BoundHashOperations<String, Object, Object> operationsForSkus = redisTemplate.boundHashOps(RedisKeyUtils.SKUS_PREFIX);
                    //??????K??????????????????sessionId
                    if (!operationsForSkus.hasKey(sessionId)) {
                        //????????????sku?????????list
                        ArrayList<productSessionVo_Skus> productSessionVo_skus_list = new ArrayList<>();
                        productSessionVo_Skus productSessionVo_skus = new productSessionVo_Skus();
                        BeanUtils.copyProperties(productSessionVo, productSessionVo_skus);
                        BeanUtils.copyProperties(productEntity, productSessionVo_skus);
                        BeanUtils.copyProperties(sessionEntity, productSessionVo_skus);
                        productSessionVo_skus.setId(productSessionEntity.getId());
                        productSessionVo_skus.setRandomCode(token);
                        productSessionVo_skus_list.add(productSessionVo_skus);
                        operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skus_list));
                    } else {
                        //??????????????????sessionId
                        //??????list?????????????????????
                        JSONArray objects = JSONArray.parseArray(operationsForSkus.get(sessionId).toString());
                        List<productSessionVo_Skus> productSessionVo_skuses = objects.toJavaList(productSessionVo_Skus.class);
                        int flag = 0;
                        for (productSessionVo_Skus productSessionVo_skus : productSessionVo_skuses) {
                            if (productSessionVo_skus.getProductName() == product.getProductName()) {
                                flag = 1;
                            }
                        }
                        if (productSessionVo_skuses != null && productSessionVo_skuses.size() >= 1 && flag == 0) {
                            productSessionVo_Skus productSessionVo_skus = new productSessionVo_Skus();
                            BeanUtils.copyProperties(productSessionVo, productSessionVo_skus);
                            BeanUtils.copyProperties(productEntity, productSessionVo_skus);
                            BeanUtils.copyProperties(sessionEntity, productSessionVo_skus);
                            productSessionVo_skus.setId(productSessionEntity.getId());
                            productSessionVo_skus.setRandomCode(token);
                            productSessionVo_skuses.add(productSessionVo_skus);
                            operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skuses));
                        }
                    }
                    //??????productsession????????????id
                    return productSessionEntity.getId();
                }
            }
        } else {
            throw new RRException("??????????????????");
        }
        return null;
    }
}
