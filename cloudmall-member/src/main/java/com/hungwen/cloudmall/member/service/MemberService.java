package com.hungwen.cloudmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hungwen.cloudmall.member.exception.PhoneException;
import com.hungwen.cloudmall.member.exception.UsernameException;
import com.hungwen.cloudmall.member.vo.MemberUserLoginVo;
import com.hungwen.cloudmall.member.vo.MemberUserRegisterVo;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.cloudmall.member.entity.MemberEntity;
import com.hungwen.common.vo.SocialUser;

import java.util.Map;

/**
 * 會員
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 14:49:31
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 使用者註冊
     * @param vo
     */
    void register(MemberUserRegisterVo vo);
    /**
     * 判斷手機號碼是否重復
     * @param phone
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneException;
    /**
     * 判斷使用者名是否重復
     * @param userName
     * @return
     */
    void checkUserNameUnique(String userName) throws UsernameException;
    /**
     * 使用者登入
     * @param vo
     * @return
     */
    MemberEntity login(MemberUserLoginVo vo);
    /**
     * 社交使用者的登入
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser) throws Exception;

}

