package cn.shoanadmin.service;


import cn.shoanadmin.domain.api.ApiResult;
import cn.shoanadmin.domain.request.LoginReq;
import cn.shoanadmin.domain.response.LoginRes;
import jakarta.servlet.http.HttpServletRequest;

public interface WechatAuthService {
    /**
     * 微信小程序用户登录
     * @param request
     * @param httpRequest
     * @return
     */
    ApiResult<LoginRes> miniAppLogin(LoginReq request, HttpServletRequest httpRequest);

}
