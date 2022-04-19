package io.renren;

import io.renren.common.utils.R;
import io.renren.modules.coupon.entity.ProductEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/15 21:33
 */
public class REnren {
    @Test
    public void test(){
        System.out.println(R.ok().put("1", 2));

        ArrayList<ProductEntity> productEntities = new ArrayList<>();
        for (ProductEntity productEntity : productEntities) {


            System.out.println(productEntity.getProductId());
        }


        Object[] skuKeys = new String[2];
        skuKeys[0]="ssss";
        skuKeys[1]="2222";
        System.out.println(Arrays.toString(skuKeys));
    }
}
