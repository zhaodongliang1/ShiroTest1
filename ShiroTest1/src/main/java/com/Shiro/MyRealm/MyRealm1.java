package com.Shiro.MyRealm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.Realm;

public class MyRealm1 implements Realm {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MyRealm1";
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		// 限制数据源只支持UsernamePasswordToken
		return token instanceof UsernamePasswordToken;
	}

	@Override
	public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		
		String username=(String)token.getPrincipal();
		String password=new String((char[])token.getCredentials());
		if(!"test".equals(username)) {
			throw new UnknownAccountException();
		}
		if(!"123456".equals(password)) {
			throw new IncorrectCredentialsException();
		}		
		return new SimpleAuthenticationInfo(username,password,getName());//应该返回用户名，密码，名字
	}

}
