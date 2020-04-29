package com.zhy.service;

import com.zhy.constant.CodeType;
import com.zhy.constant.RoleConstant;
import com.zhy.mapper.UserMapper;
import com.zhy.model.User;
import com.zhy.utils.DataMap;
import com.zhy.utils.FileUtil;
import com.zhy.utils.MD5Util;
import com.zhy.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.security.krb5.internal.PAData;

import java.io.File;
import java.util.HashMap;

/**
 * @author: zhangocean
 * @Date: 2018/6/4 15:54
 * Describe: user业务操作
 */
@Service
public class UserService implements UserDetailsService {

    private final String HEAD_PORTRAIT = "https://zhy-myblog.oss-cn-shenzhen.aliyuncs.com/public/user/avatar/noLogin_male.jpg";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {


        User user = userMapper.findUserByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }

    public DataMap findUsernameByPhone(String phone){
        String username = userMapper.findUsernameByPhone(phone);
        return DataMap.success().setData(username);
    }

    public DataMap registerUser(HashMap hashMap) {
        String phone = (String) hashMap.get("phone");
        String authCode = (String) hashMap.get("authCode");
        String password = (String) hashMap.get("password");
        String username = "rj"+ new TimeUtil().getLongTime();
        String encryptionPsw = MD5Util.encode(password);

        String trueAuthCode = redisService.getMsgCode(phone);
        if(trueAuthCode == null || !trueAuthCode.equals(authCode)) {
            return DataMap.fail(CodeType.AUTH_CODE_ERROR);
        }

        User user = userMapper.findUserByPhone(phone);
        if(user != null) {
            return DataMap.fail(CodeType.PHONE_EXIST);
        }

        User userInfo = new User(phone, username, encryptionPsw, RoleConstant.ROLE_USER, HEAD_PORTRAIT);
        userMapper.save(userInfo);

        redisService.removeMsgCode(phone);

        return DataMap.success(CodeType.REGISTER_SUCCESS);
    }

    public DataMap getUserInfo(String phone) {
        User user = userMapper.findUserByPhone(phone);
        User userData = new User();
        userData.setPhone(user.getPhone());
        userData.setEmail(user.getEmail());
        userData.setHeadPortrait(user.getHeadPortrait());
        userData.setUsername(user.getUsername());
        userData.setRealName(user.getRealName());
        userData.setIdNumber(user.getIdNumber());
        return DataMap.success().setData(userData);
    }

    public DataMap registerCertInfo(HashMap hashMap, String phone){
        String realName = (String) hashMap.get("realName");
        String idNumber = (String) hashMap.get("idNumber");
        userMapper.updateCertInfo(realName, idNumber, phone);
        return DataMap.success(CodeType.UPDATE_CERT_SUCCESS);
    }

    public DataMap changePhone(HashMap hashMap, String oldPhone){
        String newPhone = (String) hashMap.get("newPhone");
        String authCode = (String) hashMap.get("authCode");

        String trueMsgCode = redisService.getMsgCode(newPhone);
        if(trueMsgCode == null || !trueMsgCode.equals(authCode)){
            return DataMap.fail(CodeType.AUTH_CODE_ERROR);
        }

        if(oldPhone.equals(newPhone)) {
            return DataMap.fail(CodeType.PHONE_EXIST);
        }

        User user = userMapper.findUserByPhone(newPhone);
        if(user != null) {
            return DataMap.fail(CodeType.PHONE_EXIST);
        }

        userMapper.updatePhoneByOldPhone(newPhone, oldPhone);
        SecurityContextHolder.getContext().setAuthentication(null);

        return DataMap.success();
    }

    public DataMap changePassword(HashMap hashMap, String phone) {
        String password = (String) hashMap.get("password");
        userMapper.updatePassword(MD5Util.encode(password), phone);

        return DataMap.success(CodeType.CHANGE_PASSWORD_SUCCESS);
    }

    public DataMap saveUserInfo(HashMap hashMap, String phone){
        String username = (String) hashMap.get("username");
        String email = (String) hashMap.get("email");
        userMapper.updateUserInfo(username, email, phone);

        return DataMap.success(CodeType.CHANGE_USER_INFO_SUCCESS);
    }

    public DataMap updateHeadPortrait(MultipartFile file, String phone) {
        String picUrl = updatePicToOss(file, "avatar/"+phone);

        userMapper.updateHeadPortrait(picUrl, phone);
        return DataMap.success().setData(picUrl);
    }

    public DataMap updateRoomPic(MultipartFile file, String phone){
        String picUrl = updatePicToOss(file, "rooms/"+phone);
        return DataMap.success().setData(picUrl);
    }

    private String updatePicToOss(MultipartFile file, String subCatalog) {
        String filePath = this.getClass().getResource("/").getPath().substring(1) + "";

        //获得文件扩展名
        String fileContentType = file.getContentType();
        String fileExtension = fileContentType.substring(fileContentType.indexOf("/") + 1);
        String fileName = new TimeUtil().getLongTime()+"."+fileExtension;

        FileUtil fileUtil = new FileUtil();
        File headPortrait = fileUtil.multipartFileToFile(file, filePath, fileName);
        String picUrl = fileUtil.uploadFile(headPortrait, subCatalog);
        return picUrl;
    }

}
