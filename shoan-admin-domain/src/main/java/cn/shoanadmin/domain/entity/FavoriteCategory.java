package cn.shoanadmin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 收藏分类实体类
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("favorite_categories")
@Schema(description = "收藏分类实体")
public class FavoriteCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "分类ID", example = "1")
    private Long id;

    @TableField("name")
    @Schema(description = "分类名称", example = "技术文章")
    private String name;

    @TableField("description")
    @Schema(description = "分类描述", example = "技术相关的文章和资料")
    private String description;

    @TableField("icon")
    @Schema(description = "分类图标标识", example = "tech")
    private String icon;

    @TableField("sort_order")
    @Schema(description = "排序权重", example = "100")
    private Integer sortOrder;

    @TableField("is_system")
    @Schema(description = "是否系统分类：0-用户创建，1-系统预设", example = "0")
    private Integer isSystem;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "1640995200000")
    private Long createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "1640995200000")
    private Long updatedTime;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除标志：0-未删除，1-已删除", example = "0")
    private Integer deleted;
}