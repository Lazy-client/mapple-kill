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
     * redis操作hash
     */
    @Resource
    private HashOperations<String, String, String> hashOperations;

    //redis hash操作绑定sku_prefix
    BoundHashOperations<String, Object, Object> operationsForSku;

    //redis kv操作库存stock操作
    ValueOperations<String, String> opsForValue;

    /**
     * 构造函数
     * @param redisTemplate
     * @param stringRedisTemplate
     */
    public ProductSessionServiceImpl(RedisTemplate<String, Object> redisTemplate,RedisTemplate<String, String> stringRedisTemplate) {
        this.operationsForSku = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
        //redis 操作字符 放入库存stock
        this.opsForValue = stringRedisTemplate.opsForValue();
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<>();
        //获取场地id
        String sessionId = (String) params.get("sessionId");
        //判空
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
     * 保存stock库存到redis的方法
     * @param token
     * @param totalCount
     */
    private void saveStockInRedis(String token, String totalCount) {
        String key = RedisKeyUtils.STOCK_PREFIX + token;
        //如果不存在该商品的库存信息stock，则放入redis
        if (StringUtils.isEmpty(opsForValue.get(key))) {
            opsForValue.set(key, totalCount);
        }
    }

    /**
     * 保存sku到redis的方法
     * @param redisKey
     * @param productSessionVo_skus1
     */
    public void saveSkuInRedis(String redisKey, productSessionVo_Skus productSessionVo_skus1) {
        //存入redis的方法
        if (!operationsForSku.hasKey(redisKey)) {
            //用fastJson序列化json格式
            String seckillValue = JSON.toJSONString(productSessionVo_skus1);
            operationsForSku.put(redisKey, seckillValue);
        }
    }

    /**
     * 修改后的业务类
     * 直接用下拉框选择产品加入该场次
     *
     * @param productSessionVo_new
     * @return
     */
    @Override
    @Transactional
    public List<String> saveProductSession_new(productSessionVo_new productSessionVo_new) {
        //首先获取场次的Id
        String sessionId = productSessionVo_new.getSessionId();
        SessionEntity sessionEntity = sessionDao.selectById(sessionId);
        //session表中有对应场次，则存对应产品，否则返回错误
        if (null != sessionEntity) {
            //获取vo中的ArrayList<productVo_new>
            ArrayList<productVo_new> productList = productSessionVo_new.getProductList();
            //创建产品id列表
            ArrayList<String> productIdList = new ArrayList<>();
            //将id封装到productIdlist中进行校验
            for (int i = 0; i < productList.size(); i++) {
                productVo_new productVo_new = productList.get(i);
                productIdList.add(productVo_new.getProductId());
                //加个判空的代码
                if (productVo_new.getTotalCount() == null || productVo_new.getSeckillPrice() == null) {
                    throw new RRException("库存或秒杀价格为空");
                }
                //顺便校验关联表中是否存在重复信息
                Integer count = productSessionDao.selectCount(new QueryWrapper<ProductSessionEntity>().eq("session_id", sessionId).eq("product_id", productVo_new.getProductId()));
                if (count == 1) {
                    throw new RRException("该场次产品插入重复");
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
                throw new RRException("数据插入错误");
            } else {

                //redis hash操作绑定SKUS_PREFIX  ，缓存skus
                BoundHashOperations<String, Object, Object> operationsForSkus = redisTemplate.boundHashOps(RedisKeyUtils.SKUS_PREFIX);
                //声明存放skus的list
                ArrayList<productSessionVo_Skus> productSessionVo_skus_list = new ArrayList<>();
                for (int i = 0; i < productIdList.size(); i++) {
                    //循环封装进productSessionVo_Skus
                    productSessionVo_Skus productSessionVo_skus1 = new productSessionVo_Skus();
                    String redisKey = sessionId + "-" + productIdList.get(i);
                    productSessionVo_skus1.setProductId(productIdList.get(i));
                    productSessionVo_skus1.setSessionId(sessionId);
                    BeanUtils.copyProperties(sessionEntity, productSessionVo_skus1);
                    BeanUtils.copyProperties(productEntities.get(i), productSessionVo_skus1);
                    BeanUtils.copyProperties(productList.get(i), productSessionVo_skus1);
                    //生成随机码
                    String token = UUID.randomUUID().toString().replace("-", "");
                    productSessionVo_skus1.setRandomCode(token);

                    // 1.放入redis中缓存一份sku (抽取）
                    saveSkuInRedis(redisKey, productSessionVo_skus1);

                    //2. 把秒杀商品的库存量stock缓存一份到redis中 (抽取）
                    String totalCount=productList.get(i).getTotalCount().toString();
                    saveStockInRedis(token,totalCount);

                    // 3.再放入redis中缓存skus信息
                    //如果K中不存在这个sessionId
                    if (!operationsForSkus.hasKey(sessionId)) {
                        //存放多个sku信息的list
                        productSessionVo_skus_list.add(productSessionVo_skus1);
                    } else {
                        //如果存在这个sessionId
                        //取出list并添加商品信息
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
                //redis放入skus的操作
                operationsForSkus.put(sessionId, JSON.toJSONString(productSessionVo_skus_list));
            }
            ArrayList<String> ids = new ArrayList<>();
            for (ProductSessionEntity productSessionEntity : productSessionEntities) {
                ids.add(productSessionEntity.getId());
            }
//            返回productsession表的主键id的列表
            return ids;
        } else {
            throw new RRException("场次不存在");
        }

    }


    @Override
    @Transactional
    public int deductStock(String productId, String sessionId) {
        log.info("调用扣减库存, productId: {}, sessionId: {}", productId, sessionId);
        return baseMapper.deductStock(productId, sessionId);
    }

    @Override
    public int refundStock(String productId, String sessionId) {
        return baseMapper.refundStock(productId, sessionId);
    }


    /**
     * 原来的save操作
     *
     * @param productSessionVo
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
//        ArrayList<String> productIdList=productSessionVo.getProductList();
//        判空，在字段里面设置注解不生效不知道啥情况
            if (StringUtils.isEmpty(productEntity.getProductName())) {
                throw new RRException("产品名称为空");
            }
            if (StringUtils.isEmpty(productEntity.getDescription())) {
                throw new RRException("产品描述为空");
            }
            if (StringUtils.isEmpty(productEntity.getTitle())) {
                throw new RRException("产品标题为空");
            }
            BeanUtils.copyProperties(productEntity, product);
            String productId;
            ProductEntity productEntity_fromdb = productDao.selectOne(new QueryWrapper<ProductEntity>().eq("product_name", product.getProductName()));

            if (null == productEntity_fromdb) {
                int insert_product = productDao.insert(product);
                if (insert_product != 1) {
                    throw new RRException("插入数据库出错");
                }
                //存入成功，取出productId
                productId = product.getProductId();
            } else {
                productId = productEntity_fromdb.getProductId();
            }
            QueryWrapper<ProductSessionEntity> productSessionEntityQueryWrapper = new QueryWrapper<ProductSessionEntity>().eq("session_id", sessionId).eq("product_id", productId);
            if (productSessionDao.selectCount(productSessionEntityQueryWrapper) == 0) {
                //然后封装数据到productsession关联表
                ProductSessionEntity productSessionEntity = new ProductSessionEntity();
                //将数据封装到productsession对象中
                BeanUtils.copyProperties(productSessionVo, productSessionEntity);
                productSessionEntity.setProductId(productId);
//                productSessionEntity.setIsDeleted(0);
                //插入数据库中
                if (productSessionDao.insert(productSessionEntity) == 1) {
                    // 1.放入redis中缓存一份sku
                    productSessionVo_Skus productSessionVo_skus1 = new productSessionVo_Skus();
                    String redisKey = sessionId + "-" + productId;
                    productSessionVo_skus1.setProductId(productId);
                    //重新放入
                    BeanUtils.copyProperties(sessionEntity, productSessionVo_skus1);
                    BeanUtils.copyProperties(productSessionEntity, productSessionVo_skus1);
                    BeanUtils.copyProperties(product, productSessionVo_skus1);
                    //存入redis的方法
                    //redis hash操作绑定sku_prefix
                    BoundHashOperations<String, Object, Object> operationsForSku = redisTemplate.boundHashOps(RedisKeyUtils.SKU_PREFIX);
                    //生成随机码
                    String token = UUID.randomUUID().toString().replace("-", "");
                    if (!operationsForSku.hasKey(redisKey)) {
                        //设置商品的随机码（防止恶意攻击）
                        productSessionVo_skus1.setRandomCode(token);
                        //用fastJson序列化json格式
                        String seckillValue = JSON.toJSONString(productSessionVo_skus1);
                        operationsForSku.put(redisKey, seckillValue);
                    }

                    //2. 把秒杀商品的库存量缓存一份到redis中
                    ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
                    String key = RedisKeyUtils.STOCK_PREFIX + token;
                    //如果不存在该商品的库存信息stock，则放入redis
                    if (StringUtils.isEmpty(opsForValue.get(key))) {
                        opsForValue.set(key, productSessionVo.getTotalCount().toString());
                    }

                    // 3.再放入redis中缓存skus信息
                    BoundHashOperations<String, Object, Object> operationsForSkus = redisTemplate.boundHashOps(RedisKeyUtils.SKUS_PREFIX);
                    //如果K中不存在这个sessionId
                    if (!operationsForSkus.hasKey(sessionId)) {
                        //存放多个sku信息的list
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
                        //如果存在这个sessionId
                        //取出list并添加商品信息
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
                    //返回productsession表的主键id
                    return productSessionEntity.getId();
                }
            }
        } else {
            throw new RRException("数据插入重复");
        }
        return null;
    }
}
