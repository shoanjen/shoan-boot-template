package cn.shoanadmin.service;

import cn.shoanadmin.domain.entity.FavoriteCategory;
import cn.shoanadmin.domain.request.CategoryAddReq;
import cn.shoanadmin.domain.request.CategoryListReq;
import cn.shoanadmin.domain.request.CategoryUpdateReq;
import cn.shoanadmin.domain.response.CategoryRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * 收藏分类服务接口
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
public interface CategoryService extends IService<FavoriteCategory> {

    /**
     * 添加收藏分类
     * 
     * @param request 添加分类请求
     * @return 分类响应对象
     */
    CategoryRes addCategory(CategoryAddReq request);

    /**
     * 更新收藏分类
     * 
     * @param request 更新分类请求
     * @return 分类响应对象
     */
    CategoryRes updateCategory(CategoryUpdateReq request);

    /**
     * 删除收藏分类
     * 
     * @param id 分类ID
     * @return 是否删除成功
     */
    Boolean deleteCategory(Long id);

    /**
     * 根据ID获取分类详情
     * 
     * @param id 分类ID
     * @return 分类响应对象
     */
    CategoryRes getCategoryById(Long id);

    /**
     * 分页查询分类列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    Page<CategoryRes> getCategoryList(CategoryListReq request);

    /**
     * 获取所有分类列表（不分页）
     * 
     * @return 分类列表
     */
    List<CategoryRes> getAllCategories();
}