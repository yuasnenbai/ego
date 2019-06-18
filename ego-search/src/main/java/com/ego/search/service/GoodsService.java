package com.ego.search.service;

import com.ego.common.pojo.PageResult;
import com.ego.common.utils.NumberUtils;
import com.ego.item.pojo.*;
import com.ego.search.client.BrandClient;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.client.SpecificationClient;
import com.ego.search.pojo.Goods;
import com.ego.search.pojo.SearchRequest;
import com.ego.search.pojo.SearchResult;
import com.ego.search.repository.GoodsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.stats.InternalStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/11
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    private ObjectMapper mapper = new ObjectMapper();
    /**
     * 根据spu 构建 goods
     * @param spu
     * @return
     */
    public Goods buildGoods(SpuBO spu) throws Exception {

        Goods  result = new Goods();
        try {
            //1.skus
            List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId()).getBody();
            //1.1 skuList --> skus(String)
            String skus = mapper.writeValueAsString(skuList);
            result.setSkus(skus);

            //2.spuDetail
            SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId()).getBody();

            //3.specs(动态属性)
            Map<String, Object> map = new HashMap<>();
            String specifications = spuDetail.getSpecifications();
            //3.1 specifications -->list<Map<String,Object>>  ->  map
            List<Map<String,Object>> allSpecs = mapper.readValue(specifications,new TypeReference<List<Map<String,Object>>>(){});
            allSpecs.forEach(m->{
                List<Map<String,Object>> params = (List<Map<String,Object>>)m.get("params");
                params.forEach(specMap->{
                    //判断每个spec参数是不是可搜索
                    if((Boolean) specMap.get("searchable"))
                    {
                        String k = specMap.get("k").toString();
                        //判断是否有v属性
                        if(specMap.get("v")==null)
                        {
                            //那么一定会有option
                            map.put(k, specMap.get("options"));
                        }
                        else
                        {
                            //那么一定会有v
                            map.put(k, specMap.get("v"));
                        }
                    }
                });

            });
            result.setSpecs(map);


            //4.其他数据
            result.setId(spu.getId());
            result.setBrandId(spu.getBrandId());
            result.setCid1(spu.getCid1());
            result.setCid2(spu.getCid2());
            result.setCid3(spu.getCid3());
            result.setCreateTime(spu.getCreateTime());
            //priceList
            List<Long> price = skuList.stream().map(sku -> sku.getPrice()).collect(Collectors.toList());
            result.setPrice(price);
            result.setSubTitle(spu.getSubTitle());
            result.setAll(spu.getTitle() + "" +spu.getBrandName() + "" +spu.getCategoryName());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            throw new Exception();
        }
        return result;
    }

    /**
     * 分页
     * @param request 前端传的数据
     * @return
     */
    public PageResult<Goods> page(SearchRequest request) {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //基本条件查询
//        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all", searchRequest.getKey()));
//        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米");
        QueryBuilder basicQuery =this.buildBasicQueryWithFilter(request);

        nativeSearchQueryBuilder.withQuery(basicQuery);
        //分页条件
        nativeSearchQueryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //指定字段过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //指定聚合条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brandId"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("categories").field("cid3"));
        //查询
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>)goodsRepository.search(nativeSearchQueryBuilder.build());

        //查询商品分类列表
        List<Category> categoryList = getCategoryList(pageInfo.getAggregation("categories"));
        //查询品牌列表
        List<Brand> brandList = getBrandList(pageInfo.getAggregation("brands"));
        //查询其他规格参数
        List<Map<String,Object>> specs = null;
        //获取
        if(categoryList!=null&&categoryList.size()>0)
        {
            specs = getSpecs(categoryList.get(0).getId(),basicQuery);
        }


        //Page --> PageResult
        return new SearchResult(pageInfo.getTotalElements(),Long.valueOf(pageInfo.getTotalPages()),pageInfo.getContent(),categoryList,brandList,specs);
    }

    /**
     * 构建带过滤条件的基本查询
     * @param searchRequest
     * @return
     */
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        //过滤条件构造器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        //整理过滤条件
        Map<String,String> filter = searchRequest.getFilter();

        for (Map.Entry<String,String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String regex = "^(\\d+\\.?\\d*)-(\\d+\\.?\\d*)$";
            if (!"key".equals(key)) {
                if ("price".equals(key)){
                    if (!value.contains("元以上")) {
                        String[] nums = StringUtils.substringBefore(value, "元").split("-");
                        filterQueryBuilder.must(QueryBuilders.rangeQuery(key).gte(Double.valueOf(nums[0]) * 100).lt(Double.valueOf(nums[1]) * 100));
                    }else {
                        String num = StringUtils.substringBefore(value,"元以上");
                        filterQueryBuilder.must(QueryBuilders.rangeQuery(key).gte(Double.valueOf(num)*100));
                    }
                }else {
                    if (value.matches(regex)) {
                        Double[] nums = NumberUtils.searchNumber(value, regex);
                        //数值类型进行范围查询   lt:小于  gte:大于等于
                        filterQueryBuilder.must(QueryBuilders.rangeQuery("specs." + key).gte(nums[0]).lt(nums[1]));
                    } else {
                        //商品分类和品牌要特殊处理
                        if (key.equals("分类"))
                        {
                            key = "cid3";
                        }
                        else if(key.equals("品牌"))
                        {
                            key = "brandId";
                        }
                        else{
                            key = "specs." + key + ".keyword";
                        }
                        //字符串类型，进行term查询
                        filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
                    }
                }
            } else {
                break;
            }
        }
        //添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }

    /**
     * 查询其他规格参数（尺寸，大小.....）
     * @param cid   品牌id
     * @param basicQuery  基础查询
     * @return
     */
    private List<Map<String, Object>> getSpecs(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> result = null;
        try
        {
            //1.根据类别id查询出对应的规格参数json
            String specifications = specificationClient.queryByCid(cid).getBody();
            //1.1.将json-->List<Map<String,Object>
            List<Map<String,Object>> mapList = mapper.readValue(specifications,new TypeReference<List<Map<String,Object>>>(){});

            //2.区分规格参数类型(字符型,数字型)
            //2.1.将不同的规格参数存放到不同的容器中set<k>  map<k,interval>
            Set<String> strSpecs = new HashSet<>();
            Map<String, Double> numberInterval = new HashMap<>();
            Map<String, String> numberSpecs = new HashMap<>();
            //查询出聚合条件
            mapList.forEach(m->{
                                ((List<Map<String,Object>>)m.get("params")).forEach(param->{
                                    //只要用于搜索的规格参数
                                    if((Boolean) param.get("searchable"))
                                    {
                                        //获取的是value
                                        String k = (String)param.get("k");
                                        //判断是否是数字型
                                        if(param.get("numerical")!=null && (Boolean)param.get("numerical"))
                                        {
                                            //k + unit
                                            numberSpecs.put(k,(String)param.get("unit"));
                        }
                        else
                        {
                            //k 只有名字
                            strSpecs.add(k);
                        }
                    }
                });
            });
            //查询出间隔值
            numberInterval = getInterval(numberSpecs);
            //3.开始聚合查询
            result = getSpecAgg(strSpecs,numberSpecs,numberInterval,basicQuery);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 聚合查询
     * @param strSpecs      字符型
     * @param numberSpecs    数字型
     * @param numberInterval   数字型间隔
     * @param basicQuery        封装的基本查询
     * @return
     */
    private List<Map<String, Object>> getSpecAgg(Set<String> strSpecs, Map<String, String> numberSpecs, Map<String, Double> numberInterval, QueryBuilder basicQuery) {
        List<Map<String, Object>> result = new ArrayList<>();
        //查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //指定基础查询
        nativeSearchQueryBuilder.withQuery(basicQuery);
        //不查询数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null)).withPageable(PageRequest.of(0,1));
        //指定聚合条件
        //字符型
        strSpecs.forEach(k->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(k).field("specs."+k+".keyword"));
        });
        //数字型    设置interval  设置最小数
        numberSpecs.forEach((k,v)->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.histogram(k).field("specs."+k).interval(numberInterval.get(k)).minDocCount(1));
        });

        //开始聚合查询
        Map<String, Aggregation> aggResult = this.elasticsearchTemplate.query(nativeSearchQueryBuilder.build(), searchResponse -> searchResponse.getAggregations().asMap());

        //解析聚合结果
        //解析字符型规格结果
        strSpecs.forEach(k->{
            //k:参数名  value options
            Map<String, Object> map = new HashMap<>();
            //根据k去获取对应聚合结果
            StringTerms agg = (StringTerms)aggResult.get(k);
            List<String> options = agg.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            map.put("k", k);
            map.put("options", options);
            result.add(map);
        });
        //解析数字型规格结果
        numberSpecs.forEach((k,v)->{
            //k:参数名  value options
            Map<String, Object> map = new HashMap<>();
            InternalHistogram agg = (InternalHistogram) aggResult.get(k);
            List<String> options = agg.getBuckets().stream().map(bucket -> {
                Double begin = (Double) bucket.getKey();
                Double end = begin + numberInterval.get(k);
                if (NumberUtils.isInt(begin) && NumberUtils.isInt(end)) {
                    return begin + "-" + end;
                } else {
                    //保留一位小数点
                    begin = NumberUtils.scale(begin, 1);
                    end = NumberUtils.scale(end, 1);
                    return begin + "-" + end;
                }

            }).collect(Collectors.toList());
            map.put("k", k);
            map.put("options", options);
            result.add(map);
        });
        return result;
    }

    /**
     * 计算出每个数字型规格参数的间隔
     * @param numberSpecs
     * @return
     */
    private Map<String, Double> getInterval(Map<String, String> numberSpecs) {
        Map<String, Double> result = new HashMap<>();
        //从es中查询都每个数字型规格参数的min max sum
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //不查询数据  为了提升开发效率
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null)).withPageable(PageRequest.of(0,1));
        //指定聚合条件
        numberSpecs.forEach((k,v)->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.stats(k).field("specs."+k));
        });
        //查询
        Map<String, Aggregation> aggResult = elasticsearchTemplate.query(nativeSearchQueryBuilder.build(),
                searchResponse -> searchResponse.getAggregations().asMap());
        //解析聚合结果  InternalStats 封装stats结果集  agg,聚合的结果  min，max，sum.......
        aggResult.forEach((k,agg)->{
            InternalStats internalStats = (InternalStats)agg;
            Double interval = NumberUtils.getInterval(internalStats.getMin(),internalStats.getMax(),internalStats.getSum());
            result.put(k,interval);
        });
        return result;
    }

    /**
     * 获取brand的数据
     * @param agg
     * @return
     */
    private List<Brand> getBrandList(Aggregation agg) {
        List<Brand> result = null;
        LongTerms longTerms = (LongTerms)agg;
        //获取到每个桶中的key（cid3）-->List<Long>
        List<Long> idList = longTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据List<cid> -> List<Category>
        result = brandClient.queryListByIds(idList);
        return result;
    }

    /**
     * 获取category数据
     * @param agg
     * @return
     */
    private List<Category> getCategoryList(Aggregation agg) {
        List<Category> result = null;
        LongTerms longTerms = (LongTerms)agg;
        //获取到每个桶中的key（cid3）-->List<Long>
        List<Long> idList = longTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据List<cid> -> List<Category>
        result = categoryClient.queryListByIds(idList);
        return result;
    }


    public void createIndex(Long id) throws Exception {
        SpuBO spu = this.goodsClient.queryGoodsById(id);
    // 构建商品
        Goods goods = this.buildGoods(spu);
    // 保存数据到索引库
        this.goodsRepository.save(goods);
    }
}
