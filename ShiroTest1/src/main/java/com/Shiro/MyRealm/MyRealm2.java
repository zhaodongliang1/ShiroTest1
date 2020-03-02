package com.Shiro.MyRealm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.jdbc.core.JdbcTemplate;


public class MyRealm2 extends AuthorizingRealm {
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	//权限验证调用
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//根据用户名查询出所有角色
		String sql="select role_name from user_role where user_name = ?";
		String username=(String)principals.getPrimaryPrincipal();
		List<String> roles=jdbcTemplate.queryForList(sql,String.class,username);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();	//null是加密的盐值	
		info.addRoles(roles);
		return info;
	}
    //登陆的时候调用
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 根据名字进行查询
		String sql="SELECT password FROM user WHERE user_name= ?";
		String username=(String)token.getPrincipal();
		String password = jdbcTemplate.queryForObject(sql,String.class,username);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username,password,null,getName());	//null是加密的盐值	
		return info;
	}

}
