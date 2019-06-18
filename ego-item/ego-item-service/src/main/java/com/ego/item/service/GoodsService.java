package com.ego.item.service;


import com.ego.common.enums.ExceptionEnum;
import com.ego.common.exception.PayException;
import com.ego.common.pojo.CartDto;
import com.ego.common.pojo.PageResult;
import com.ego.item.mapper.*;
import com.ego.item.pojo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    SpuMapper spuMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandMapper brandMapper;

    @Autowired
    SpuDetailMapper spuDetailMapper;

    @Autowired
    SkuMapper skuMapper;

    @Autowired
    StockMapper stockMapper;

    @Autowired
    AmqpTemplate amqpTemplate;
    /**
     * 分页
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult page(String key, Integer saleable, Integer page, Integer rows) {
        //分页  ,rows 和 100 取最小的数
        PageHelper.startPage(page,Math.min(rows,100));
        //创建一个例子  类
        Example example = new Example(Spu.class);
        //例子 规则
        Example.Criteria criteria = example.createCriteria();
        //判断是否由关键字
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title", key).orLike("sub_title", key);
        }
        //判断查看的是什么类型的   0  下架   1  上架
        if(saleable!=null){
            criteria.andEqualTo(saleable);
        }

        //根据例子查询
        Page<Spu> pageInfo = (Page<Spu>)spuMapper.selectByExample(example);
        System.out.println(pageInfo);

        //将SpuList  -->  SpuBOList    新方法
        List<SpuBO> spuBOList = pageInfo.stream().map(spu -> {
            SpuBO spuBO = new SpuBO();
            //copy  复制到的地点，bei复制的类
            try {
                BeanUtils.copyProperties(spuBO, spu);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return spuBO;
            //将所有转换好的spuBo存入一个list中
        }).collect(Collectors.toList());

        spuBOList.forEach(spuBo ->{
            List<Long> longs = Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3());
            //通过类别id查询类别名字
            List<String> strings = categoryService.queryListByCids(longs);
            String categoryName = "";
            //将List转换为String拼接
            for(int i=0;i<strings.size();i++){
                categoryName+=strings.get(i);
                if(i<strings.size()){
                    categoryName+="/";
                }
            }
            spuBo.setCategoryName(categoryName);
            System.out.println(spuBo.getBrandId());
            //通过品牌id查询品牌名字
            /*Brand brand = brandMapper.selectByPrimaryKey(spuBo.getBrandId());
            spuBo.setCategoryName(brand.getName());*/
            if (spuBo.getBrandId() !=null) {
                Brand brand = brandMapper.selectByPrimaryKey(spuBo.getBrandId());
                spuBo.setBrandName(brand.getName());
            }
        });

        //分页并返回     总数据 总页数  List数据
        return new PageResult(pageInfo.getTotal(),Long.valueOf(pageInfo.getPages()),spuBOList);

    }

    /**
     * 新增
     * @param spuBO
     */
    @Transactional
    public void save(SpuBO spuBO) {
        //新增spu
        spuBO.setCreateTime(new Date());
        spuBO.setLastUpdateTime(new Date());
        spuMapper.insertSelective(spuBO);
        //新增spudetail
        SpuDetail spuDetail = spuBO.getSpuDetail();
        spuDetail.setSpuId(spuBO.getId());
        spuDetailMapper.insertSelective(spuDetail);
        //新增skus
        saveSkuAndStock(spuBO);
        //发送信息到mq
        this.amqpTemplate.convertAndSend("item.insert" , spuBO.getId());
//        amqpTemplate.convertAndSend("exchange.ego.item","item.update",spuBO.getId());

    }


    public SpuDetail findSpuDetailBySpuId(Long supId) {
       return spuDetailMapper.selectByPrimaryKey(supId);
    }

    /**
     * 查找sku和库存
     * @param spuid
     * @return
     */
    public List<Sku> findSkuById(Long spuid) {

        Sku sku = new Sku();
        sku.setSpuId(spuid);
        List<Sku> skus = skuMapper.select(sku);
        if(skus!=null){
            skus.forEach(sku1 -> {
                Stock stock1 = stockMapper.selectByPrimaryKey(sku1.getId());
                sku1.setStock(stock1);
            });
        }

        return skus;
    }

    /**
     * 新增
     * @param spuBO
     */
    @Transactional
    public void update(SpuBO spuBO) {
        //updateByPrimaryKeySelective  判断以后再更新
        //updateByPrimaryKey   直接更新
        //更新sup
        spuBO.setLastUpdateTime(new Date());
        spuMapper.updateByPrimaryKey(spuBO);
        //更新spudetail  根据类更新
        SpuDetail spuDetail = spuBO.getSpuDetail();
        spuDetailMapper.updateByPrimaryKey(spuDetail);
        //根据spuBo找到stock的id并删除
        Sku sku = new Sku();
        sku.setSpuId(spuBO.getId());
        List<Sku> skus = skuMapper.select(sku);
        List<Long> skuIds = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
        //删除stock
        Example example = new Example(Stock.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuId", skuIds);
//        example.createCriteria().andIn("skuId", skuIds);
        stockMapper.deleteByExample(example);
        //删除sku
        skuMapper.delete(sku);
        //添加sku和stock
        saveSkuAndStock(spuBO);
        /*List<Sku> skus = spuBO.getSkus();
        if(skus!=null)
        {
            skus.forEach(sku->{

                skuMapper.updateByPrimaryKey(sku);

                //新增stock
                Stock stock = sku.getStock();
                stock.setSkuId(sku.getId());

                stockMapper.updateByPrimaryKey(sku.getStock());
            });
        }*/
    }


    /**
     * 添加sku和stock
     * @param spuBO
     */
    private void saveSkuAndStock(SpuBO spuBO) {
        List<Sku> skus = spuBO.getSkus();
        if(skus!=null)
        {
            skus.forEach(sku->{
                sku.setSpuId(spuBO.getId());

                sku.setCreateTime(spuBO.getCreateTime());
                sku.setLastUpdateTime(sku.getCreateTime());
                skuMapper.insertSelective(sku);

                //新增stock
                Stock stock = sku.getStock();
                stock.setSkuId(sku.getId());

                stockMapper.insert(stock);
            });
        }
    }

    /**
     * 根据id获取
     * @param spuId
     * @return
     */
    public List<Sku> findSkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);

        //查询库存数据
        if(skus!=null)
        {
            skus.forEach(s->{
                Stock stock = stockMapper.selectByPrimaryKey(s.getId());
                s.setStock(stock);
            });
        }

        return skus;
    }

    public SpuBO queryGoodsById(Long spuId) {
        SpuBO spuBO = new SpuBO();
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //spuDetail
        spuBO.setSpuDetail(spuDetailMapper.selectByPrimaryKey(spuId));
        //拷贝其他相同属性值
        try {
            BeanUtils.copyProperties(spuBO, spu);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //查询skus
        spuBO.setSkus(this.findSkuBySpuId(spuId));
        return  spuBO;
    }

    public Sku querySkuById(String skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }

    @Transactional
    public void decreaseStock(List<CartDto> cartDtos) {
        for (CartDto cartDto : cartDtos) {
            int count = stockMapper.decreaseStock(cartDto.getSkuId(), cartDto.getNum());
            if (count != 1) {
                throw new PayException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }
        }
    }
}

