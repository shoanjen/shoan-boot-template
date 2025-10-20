package cn.shoanadmin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.shoanadmin.domain.entity.FavoriteCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏分类Mapper接口
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Mapper
public interface CategoryMapper extends BaseMapper<FavoriteCategory> {

}