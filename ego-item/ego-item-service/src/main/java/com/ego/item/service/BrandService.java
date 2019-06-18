package com.ego.item.service;

import com.ego.common.pojo.PageResult;
import com.ego.item.mapper.BrandMapper;
import com.ego.item.pojo.Brand;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/4
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;
    public PageResult<Brand> page(Boolean descending, Integer page, Integer rowsPerPage, String sortBy, String search) {

        PageResult<Brand> result = null;
        //通过分页助手帮我们分页
        PageHelper.startPage(page, rowsPerPage);

        //根据条件查询list数据
        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(search))
        {
            Example.Criteria criteria = example.createCriteria();
            //根据名字查询
            criteria.andLike("name","%"+search+"%");
            //根据letter查询
            criteria.orEqualTo("letter", search.toUpperCase());
        }


        //排序
        example.setOrderByClause(sortBy+(descending?" desc":" asc"));

        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);

        if(pageInfo.getTotal()>0)
        {
            result =  new PageResult(pageInfo.getTotal(),Long.valueOf(pageInfo.getPages()),pageInfo);
        }
        return result;

    }

    @Transactional
    public void save(Brand brand, String cids) {
        //保存品牌
        brandMapper.insertSelective(brand);
        //维护品牌和类别之间的关系
        if(cids!=null)
        {
            String[] cidArray = cids.split(",");
            for(int i = 0 ;i < cidArray.length;i++)
            {
                brandMapper.insertBrandCategory(brand.getId(),Long.valueOf(cidArray[i]));
            }
        }

    }

    public List<Brand> findByCid(Long cid) {
        return brandMapper.findByCid(cid);
    }

    public List<Brand> queryListByList(List<Long> idList) {
        return brandMapper.selectByIdList(idList);
    }
}
