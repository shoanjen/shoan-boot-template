package cn.shoanadmin.service.impl;

import cn.shoanadmin.infrastructure.mapper.CategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.shoanadmin.common.enums.BusinessCodeEnum;
import cn.shoanadmin.common.exception.BusinessException;
import cn.shoanadmin.domain.request.CategoryAddReq;
import cn.shoanadmin.domain.request.CategoryListReq;
import cn.shoanadmin.domain.request.CategoryUpdateReq;
import cn.shoanadmin.domain.response.CategoryRes;
import cn.shoanadmin.domain.entity.FavoriteCategory;
import cn.shoanadmin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏分类服务实现类
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, FavoriteCategory> implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryRes addCategory(CategoryAddReq request) {
        log.info("添加收藏分类，分类名称：{}", request.getName());
        
        // 检查分类名称是否已存在
        LambdaQueryWrapper<FavoriteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FavoriteCategory::getName, request.getName());
        FavoriteCategory existCategory = categoryMapper.selectOne(queryWrapper);
        if (existCategory != null) {
            log.warn("分类名称已存在：{}", request.getName());
            throw new BusinessException(BusinessCodeEnum.CATEGORY_NAME_EXISTS);
        }

        // 创建分类对象
        FavoriteCategory category = FavoriteCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isSystem(0) // 用户创建的分类
                .build();

        // 保存分类
        int saved = categoryMapper.insert(category);
        if (saved == 0) {
            log.error("保存分类失败：{}", request.getName());
            throw new BusinessException(BusinessCodeEnum.SYSTEM_ERROR);
        }

        log.info("添加收藏分类成功，分类ID：{}", category.getId());
        return convertToResponse(category);
    }

    @Override
    public CategoryRes updateCategory(CategoryUpdateReq request) {
        log.info("更新收藏分类，分类ID：{}", request.getId());
        
        // 检查分类是否存在
        LambdaQueryWrapper<FavoriteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FavoriteCategory::getId, request.getId());
        FavoriteCategory category = categoryMapper.selectOne(queryWrapper);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", request.getId());
            throw new BusinessException(BusinessCodeEnum.CATEGORY_NOT_EXISTS);
        }

        // 检查是否为系统分类
        if (category.getIsSystem() == 1) {
            log.warn("系统分类不允许修改，分类ID：{}", request.getId());
            throw new BusinessException(BusinessCodeEnum.SYSTEM_CATEGORY_NOT_EDITABLE);
        }

        // 检查分类名称是否已存在（排除当前分类）
        LambdaQueryWrapper<FavoriteCategory> queryWrapperName = new LambdaQueryWrapper<>();
        queryWrapperName.eq(FavoriteCategory::getName, request.getName());
        FavoriteCategory existCategory = categoryMapper.selectOne(queryWrapperName);
        if (existCategory != null) {
            log.warn("分类名称已存在：{}", request.getName());
            throw new BusinessException(BusinessCodeEnum.CATEGORY_NAME_EXISTS);
        }

        // 更新分类信息
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        // 保存更新
        int updated = categoryMapper.updateById(category);
        if (updated <= 0) {
            log.error("更新分类失败，分类ID：{}", request.getId());
            throw new BusinessException(BusinessCodeEnum.SYSTEM_ERROR);
        }

        log.info("更新收藏分类成功，分类ID：{}", category.getId());
        return convertToResponse(category);
    }

    @Override
    public Boolean deleteCategory(Long id) {
        log.info("删除收藏分类，分类ID：{}", id);
        
        // 检查分类是否存在
        FavoriteCategory category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", id);
            throw new BusinessException(BusinessCodeEnum.CATEGORY_NOT_EXISTS);
        }

        // 检查是否为系统分类
        if (category.getIsSystem() == 1) {
            log.warn("系统分类不允许删除，分类ID：{}", id);
            throw new BusinessException(BusinessCodeEnum.SYSTEM_CATEGORY_NOT_DELETABLE);
        }

        // TODO: 检查分类下是否有收藏内容，如果有则不允许删除
        // 这里需要在收藏内容模块开发完成后添加相关检查逻辑

        // 执行逻辑删除
        int deleted = categoryMapper.deleteById(id);
        if (deleted <= 0) {
            log.error("删除分类失败，分类ID：{}", id);
            throw new BusinessException(BusinessCodeEnum.SYSTEM_ERROR);
        }

        log.info("删除收藏分类成功，分类ID：{}", id);
        return true;
    }

    @Override
    public CategoryRes getCategoryById(Long id) {
        log.info("获取分类详情，分类ID：{}", id);
        
        FavoriteCategory category = this.getById(id);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", id);
            throw new BusinessException(BusinessCodeEnum.CATEGORY_NOT_EXISTS);
        }

        return convertToResponse(category);
    }

    @Override
    public Page<CategoryRes> getCategoryList(CategoryListReq request) {
        log.info("分页查询收藏分类列表，页码：{}，每页大小：{}", request.getPageNum(), request.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<FavoriteCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        // 分类名称模糊查询
        if (StringUtils.hasText(request.getName())) {
            queryWrapper.like(FavoriteCategory::getName, request.getName());
        }
        
        // 是否系统分类
        if (request.getIsSystem() != null) {
            queryWrapper.eq(FavoriteCategory::getIsSystem, request.getIsSystem());
        }
        
        // 按排序权重降序，创建时间降序
        queryWrapper.orderByDesc(FavoriteCategory::getSortOrder, FavoriteCategory::getCreatedTime);
        
        // 分页查询
        Page<FavoriteCategory> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<FavoriteCategory> result = categoryMapper.selectPage(page, queryWrapper);
        
        // 转换为响应对象
        Page<CategoryRes> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<CategoryRes> categoryResList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(categoryResList);
        
        log.info("分页查询收藏分类列表完成，总数：{}", result.getTotal());
        return responsePage;
    }

    @Override
    public List<CategoryRes> getAllCategories() {
        log.info("获取所有收藏分类列表");
        
        // 构建查询条件
        LambdaQueryWrapper<FavoriteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(FavoriteCategory::getSortOrder, FavoriteCategory::getCreatedTime);
        
        // 查询所有分类
        List<FavoriteCategory> categories = categoryMapper.selectList(queryWrapper);
        
        // 转换为响应对象
        List<CategoryRes> categoryResList = categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("获取所有收藏分类列表完成，总数：{}", categoryResList.size());
        return categoryResList;
    }

    /**
     * 转换实体对象为响应对象
     */
    private CategoryRes convertToResponse(FavoriteCategory category) {
        CategoryRes response = new CategoryRes();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setIcon(category.getIcon());
        response.setSortOrder(category.getSortOrder());
        response.setIsSystem(category.getIsSystem());
        response.setCreatedTime(category.getCreatedTime());
        response.setUpdatedTime(category.getUpdatedTime());
        return response;
    }
}