package cn.shoanadmin.domain.request;

import cn.shoanadmin.domain.request.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新收藏分类请求对象
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新收藏分类请求对象")
public class CategoryUpdateReq extends BaseReq {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "1", required = true)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Schema(description = "分类名称", example = "技术文章", required = true)
    private String name;

    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    @Schema(description = "分类描述", example = "技术相关的文章和资料")
    private String description;

    @Size(max = 50, message = "图标标识长度不能超过50个字符")
    @Schema(description = "分类图标标识", example = "tech")
    private String icon;

    @Schema(description = "排序权重，数值越大越靠前", example = "100")
    private Integer sortOrder;
}