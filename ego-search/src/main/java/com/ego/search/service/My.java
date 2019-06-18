package com.ego.search.service;


import com.ego.search.client.BrandClient;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.client.SpecificationClient;
import com.ego.search.repository.GoodsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

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
public class My {
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
     * 根据spu 构建 goods   数据库数据转换成es数据
     * @param spu
     * @return
     */
    /*public Goods buildGoods(SpuBO spu) throws Exception {

        Goods  result = new Goods();
        try {
            //1.skus
            List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId()).getBody();
            //1.1 skuList --> skus(json)
            String skus = mapper.writeValueAsString(skuList);
            result.setSkus(skus);

            //2.spuDetail
            SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId()).getBody();

            //3.specs(动态属性)
            Map<String, Object> map = new HashMap<>();
            String specifications = spuDetail.getSpecifications();


            //3.1 specifications（json） -->list<Map<String,Object>>  ->  map
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
            //priceList   遍历出price转换为集合
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
    }*/
    /**
     * 分页
     * @param searchRequest 前端传的数据
     * @return
     */
    /*public PageResult page(SearchRequest searchRequest) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //基础查询 查询all里边的searchRequest.getKey()（你要搜索的数据）
//      nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all", searchRequest.getKey()));
        QueryBuilder basicQuery =buildBasicQueryWithFilter(searchRequest);
        nativeSearchQueryBuilder.withQuery(basicQuery);
        //分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(searchRequest.getPage() - 1, searchRequest.getSize()));
        //过滤   过滤类   第一个参数显示什么数据，第二个参数不显示什么数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        //聚合，这个东西适合分页隔开的，不是分页,虽然写在分页里，但是分页的时候不用的，是为了展示上边的搜索列表使用
        //取一个名字    根据什么属性聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brandId"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("categories").field("cid3"));
        //查询  page的子类 AggregatedPage   可以获取到聚合信息
        AggregatedPage<Goods> search = (AggregatedPage<Goods>)goodsRepository.search(nativeSearchQueryBuilder.build());
        //获取品牌列表
        List<Category> categoryList=getCategoryLits(search.getAggregation("categories"));
        //获取商品列表
        List<Brand> brandList = getBrandLists(search.getAggregation("brands"));
        //查询其他参数的规格
        List<Map<String,Object>> specs = null;
        if(categoryList.get(0).getId()!=null) {
            specs = getSpecs(categoryList.get(0).getId(), basicQuery);
        }
        //Page --> PageResult
        return new SearchResult(search.getTotalElements(),Long.valueOf(search.getTotalPages()),search.getContent(),categoryList,brandList,specs);

    }*/
    /**
     * 查询其他规格参数（尺寸，大小.....）
     * @param cid   品牌id
     * @param basicQuery  基础查询
     * @return
     */
    /*private List<Map<String, Object>> getSpecs(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> result = null;
        //1.据类别id查询出对应的规格参数jsonspecifications字符串，根据id去查就行
        // getbody是因为返回的是ResponseEntity<String>所以要获取里边的String
        String specifications=specificationClient.queryByCid(cid).getBody();
        //1.1.将json转换为->List<Map<String,Object>>
        try {
            List<Map<String,Object>> mapList = mapper.readValue(specifications, new TypeReference<List<Map<String,Object>>>(){});
            //2.区分规格参数类型(字符型，数字型)
            //2.1.把不同的类型放到不同的容器中  字符型set   数字型map(k,interval) interval取值范围
            HashSet<String> strSpecs = new HashSet<>();
            Map<String, Double> numberInterval = new HashMap<>();
            Map<String, String> numberSpecs = new HashMap<>();
            mapList.forEach(m->{
                ((List<Map<String,Object>>)m.get("params")).forEach(mp->{
                    //判断是不是搜索条件
                    if((Boolean) mp.get("searchable")){
                        //获取的是value
                        String k = (String) mp.get("k");
                        //是数字
                        if(mp.get("numerical")!=null && (Boolean) mp.get("numerical")){
                            //k + unit
                            numberSpecs.put(k,(String) mp.get("unit"));
                        }else {
                            //k 只有名字
                            strSpecs.add(k);
                        }
                    }
                });
            });
            //查询出间隔值
            numberInterval = getInterval(numberSpecs);
            //3.聚合查询
            result = getSpecAgg(strSpecs,numberSpecs,numberInterval,basicQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }*/
    /**
     * 聚合查询
     * @param strSpecs      字符型
     * @param numberSpecs    数字型
     * @param numberInterval   数字型间隔
     * @param basicQuery        封装的基本查询
     * @return
     */
    /*private List<Map<String, Object>> getSpecAgg(HashSet<String> strSpecs, Map<String, String> numberSpecs, Map<String, Double> numberInterval, QueryBuilder basicQuery) {
        List<Map<String, Object>> result = new ArrayList<>();
        //查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //指定基础查询
        nativeSearchQueryBuilder.withQuery(basicQuery);
        //不查询数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null)).withPageable(PageRequest.of(0, 1));
        //指定聚合条件
        //字符型
        strSpecs.forEach(k->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(k).field("specs."+k+".keyword"));
        });
        //数字型
        numberSpecs.forEach((k,v)->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.histogram(k).field("specs."+k).interval(numberInterval.get(k)).minDocCount(1));
        });
        //开始聚合查询
        Map<String, Aggregation> query = elasticsearchTemplate.query(nativeSearchQueryBuilder.build(), searchResponse -> searchResponse.getAggregations().asMap());
        //解析聚合结果
        //解析字符型规格结果
        strSpecs.forEach(k->{
            Map<String, Object> map = new HashMap<>();
            StringTerms aggregation = (StringTerms)query.get(k);
            List<String> options = aggregation.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            map.put("key", k);
            map.put("options", options);
            result.add(map);
        });
        //解析数字型规格结果
        numberSpecs.forEach((k,v)->{
            Map<String, Object> map = new HashMap<>();
            InternalHistogram aggregation = (InternalHistogram)query.get(k);
            List<String> options = aggregation.getBuckets().stream().map(bucket -> {
                Double begin=(Double) bucket.getKey();
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
            //put一个没有value的map，里边放一个对象options
            map.put("k", k);
            map.put("options", options);
            result.add(map);

        });
        return  result;
    }*/
    /**
     * 计算出每个数字型规格参数的间隔
     * @param numberSpecs
     * @return
     */
    /*private Map<String, Double> getInterval(Map<String, String> numberSpecs) {
        //从es中查询都每个数字型规格参数的min max sum
        Map<String, Double> result = new HashMap<>();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //不查询分页数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""},null
        )).withPageable(PageRequest.of(0, 1));
        //指定聚合条件
        numberSpecs.forEach((k,v)->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.stats(k).field("specs."+ k));
        });
        //查询 获取所有聚合转换为map
        Map<String, Aggregation> aggResult = elasticsearchTemplate.query(nativeSearchQueryBuilder.build(), searchResponse -> searchResponse.getAggregations().asMap());
        //解析聚合结果  InternalStats 封装stats结果集  agg,聚合的结果  min，max，sum.......
        aggResult.forEach((k,agg)->{
            InternalStats internalStats = (InternalStats)agg;
            Double interval = NumberUtils.getInterval(((InternalStats) agg).getMin(), ((InternalStats) agg).getMax(), ((InternalStats) agg).getSum());
            result.put(k, interval);
        });
        return result;
    }*/
    /**
     * 构建带过滤条件的基本查询
     * @param searchRequest
     * @return
     */
    /*private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
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
    }*/
    /**
     * 根据category的ids获取到所对应的对象
     * @param agg
     * @return
     */
    /*private List<Category> getCategoryLits(Aggregation agg) {
        List<Category> result = null;
        //Aggregation不能获取里边的桶  所以转化为LongTerms
        LongTerms longTerms=(LongTerms)agg;
        //将桶中的key（cid3）转化为List<Long>
        List<Long> idList = longTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据list<cid>转换为list<Category>
        result = categoryClient.queryListByIds(idList);
        return result;
    }*/
    /**
     * Brand的ids获取到所对应的对象
     * @param agg
     * @return
     */
    /*private List<Brand> getBrandLists(Aggregation agg) {
        List<Brand> result = null;
        //Aggregation不能获取里边的桶  所以转化为LongTerms
        LongTerms longTerms=(LongTerms)agg;
        //将桶中的key（cid3）转化为List<Long>
        List<Long> idList = longTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        //根据list<cid>转换为list<Category>
        result=brandClient.queryListByIds(idList);
        return result;
    }*/
}
