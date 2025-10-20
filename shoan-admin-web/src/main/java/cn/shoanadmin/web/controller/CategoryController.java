package cn.shoanadmin.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.shoanadmin.common.annotation.RequireAuth;
import cn.shoanadmin.domain.api.ApiResult;
import cn.shoanadmin.domain.request.CategoryAddReq;
import cn.shoanadmin.domain.request.CategoryListReq;
import cn.shoanadmin.domain.request.CategoryUpdateReq;
import cn.shoanadmin.domain.response.CategoryRes;
import cn.shoanadmin.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏分类控制器
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "收藏分类管理", description = "收藏分类相关接口")
@RequireAuth
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/addCategory")
    @Operation(summary = "添加收藏分类", description = "创建新的收藏分类")
    public ApiResult<CategoryRes> addCategory(@Valid @RequestBody CategoryAddReq request) {
        CategoryRes result = categoryService.addCategory(request);
        return ApiResult.success(result);
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "更新收藏分类", description = "更新指定收藏分类的信息")
    public ApiResult<CategoryRes> updateCategory(
            @Parameter(description = "分类ID", example = "1") @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateReq updateReq) {
        updateReq.setId(categoryId);
        CategoryRes result = categoryService.updateCategory(updateReq);
        return ApiResult.success(result);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "删除收藏分类", description = "删除指定的收藏分类")
    public ApiResult<Boolean> deleteCategory(
            @Parameter(description = "分类ID", example = "1") @PathVariable Long categoryId) {
        Boolean result = categoryService.deleteCategory(categoryId);
        return ApiResult.success(result);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "获取收藏分类详情", description = "根据ID获取收藏分类的详细信息")
    public ApiResult<CategoryRes> getCategoryById(
            @Parameter(description = "分类ID", example = "1") @PathVariable Long categoryId) {
        CategoryRes result = categoryService.getCategoryById(categoryId);
        return ApiResult.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询收藏分类", description = "根据条件分页查询收藏分类列表")
    public ApiResult<Page<CategoryRes>> getCategoryList(@Valid @RequestBody CategoryListReq request) {
        Page<CategoryRes> result = categoryService.getCategoryList(request);
        return ApiResult.success(result);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有收藏分类", description = "获取所有收藏分类列表（不分页）")
    public ApiResult<List<CategoryRes>> getAllCategories() {
        List<CategoryRes> result = categoryService.getAllCategories();
        return ApiResult.success(result);
    }
}