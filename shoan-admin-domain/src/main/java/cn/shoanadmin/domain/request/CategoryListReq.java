package cn.shoanadmin.domain.request;

import cn.shoanadmin.domain.request.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏分类列表查询请求对象
 * 
 * @author FruitPieces
 * @since 2024-01-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "收藏分类列表查询请求对象")
public class CategoryListReq extends BaseReq {

    @Schema(description = "分类名称（模糊查询）", example = "技术")
    private String name;

    @Schema(description = "是否系统分类：0-用户创建，1-系统预设", example = "0")
    private Integer isSystem;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize = 20;
}