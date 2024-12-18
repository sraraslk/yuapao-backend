package com.yupi.yupao.cotroller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.Result;
import com.yupi.yupao.exception.WxException;
import com.yupi.yupao.model.UserLoginRequest;
import com.yupi.yupao.model.UserRegisterRequest;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.yupao.contant.UserConstant.ADMIN_ROLE;
import static com.yupi.yupao.contant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"},allowCredentials = "true")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
           //return null;
            throw new WxException(ErrorCode.PARAMS_ERROR);  
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)) {
            return null;
        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return Result.ok(id);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new WxException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new WxException(ErrorCode.NULL_ERROR);
        }
        User user = userService.doLogin(userAccount, userPassword, request);
      return Result.ok(user);
    }

    @PostMapping("/logOut")
    public BaseResponse<Boolean> userLogout( HttpServletRequest request) {
        if (request == null) {
           throw new WxException(ErrorCode.NO_AUTH);
        }
        boolean b = userService.userLogOut(request);
        return Result.ok(b);

    }

    /**
     *
     * 查看当前用户
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrent( HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObject;
        if (currentUser == null) {
          throw new WxException(ErrorCode.NOT_LOGIN);
        }
        Long id = currentUser.getId();
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return Result.ok(safetyUser);

    }

    /**
     * 根据用户名查询
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            //return new ArrayList<>();
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            wrapper.eq(User::getUsername, username);
        }
        List<User> userList = userService.list(wrapper);
        List<User> list = userList.stream().map(user -> {
            user.setUserPassword(null);
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        return Result.ok(list);

    }

    /**
     * 根据id删除用户
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new WxException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new WxException(ErrorCode.NULL_ERROR);
        }

        boolean b = userService.removeById(id);
        return Result.ok(b);

    }

    /**
     * 判断是否为管理员
     *
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }
}
