package cn.shoanadmin.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏分类响应对象
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏分类响应对象")
public class CategoryRes {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "技术文章")
    private String name;

    @Schema(description = "分类描述", example = "技术相关的文章和资料")
    private String description;

    @Schema(description = "分类图标标识", example = "tech")
    private String icon;

    @Schema(description = "排序权重", example = "100")
    private Integer sortOrder;

    @Schema(description = "是否系统分类：0-用户创建，1-系统预设", example = "0")
    private Integer isSystem;

    @Schema(description = "创建时间", example = "1640995200000")
    private Long createdTime;

    @Schema(description = "更新时间", example = "1640995200000")
    private Long updatedTime;
}