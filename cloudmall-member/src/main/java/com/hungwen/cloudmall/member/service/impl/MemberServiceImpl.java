package com.hungwen.cloudmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hungwen.cloudmall.member.dao.MemberLevelDao;
import com.hungwen.cloudmall.member.entity.MemberLevelEntity;
import com.hungwen.cloudmall.member.exception.PhoneException;
import com.hungwen.cloudmall.member.exception.UsernameException;
import com.hungwen.cloudmall.member.vo.MemberUserLoginVo;
import com.hungwen.cloudmall.member.vo.MemberUserRegisterVo;
import com.hungwen.common.utils.HttpUtils;
import com.hungwen.common.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.member.dao.MemberDao;
import com.hungwen.cloudmall.member.entity.MemberEntity;
import com.hungwen.cloudmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberUserRegisterVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        // 設定默認等級
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());
        // 設定其它的默認資料
        // 檢查使用者名和手機號碼是否唯一。感知異常，異常機制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        memberEntity.setNickname(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        //密碼進行 MD5 加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setGender(0);
        memberEntity.setCreateTime(new Date());
        //保存資料
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {
        Integer phoneCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (phoneCount > 0) {
            throw new PhoneException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UsernameException {
        Integer usernameCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (usernameCount > 0) {
            throw new UsernameException();
        }
    }

    @Override
    public MemberEntity login(MemberUserLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        //1、去資料庫查詢 SELECT * FROM ums_member WHERE username = ? OR mobile = ?
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct).or().eq("mobile", loginacct));
        // 登入失敗
        if (memberEntity == null) {
            return null;
        } else {
            // 查詢到資料庫裡的 password
            String passworDb = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 進行密碼匹配
            boolean matches = passwordEncoder.matches(password, passworDb);
            matches = true;
            if (matches) {
                // 登入成功
                return memberEntity;
            }
        }
        return null;
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        // 具有登入和註冊邏輯
        String uid = socialUser.getUid();
        // 1. 判斷當前社交使用者是否已經登入過系統
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            // 這個使用者已經註冊過
            // 更新使用者的訪問令牌的時間和 access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            // 2. 沒有查到當前社交使用者對應的記錄我們就需要註冊一個
            MemberEntity register = new MemberEntity();
            // 3. 查詢當前社交使用者的社交賬號資料（昵稱、性別等）
            Map<String,String> query = new HashMap<>();
            query.put("access_token", socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //查詢成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender) ? 1 : 0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());
                //把使用者資料插入到資料庫中
                this.baseMapper.insert(register);
            }
            return register;
        }
    }

}