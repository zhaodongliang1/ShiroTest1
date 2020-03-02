package com.Shiro;



import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;

import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.Shiro.MyRealm.MyRealm1;
import com.Shiro.MyRealm.MyRealm2;






public class Test {
	public static void main(String[] args) {		
		login("zhangsan", "123456");							
	}
	private static  void login(String userName, String pwd) {
		DefaultSecurityManager securityManager =new DefaultSecurityManager();
		int y=3;
		if(y==1) {
			//1.加载配置文件
			IniRealm iniRealm =new IniRealm("classpath:Shiro.ini");
			//2.使用配置文件的数据源
			securityManager.setRealm(iniRealm);
		}else if(y==2){
			//3.或者使用自定义的数据源
			//设置身份验证的策略最少有一个匹配
			ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();		
			authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
			//策略指定给DefaultSecurityManager
			securityManager.setAuthenticator(authenticator);
			//授权
			ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
			//解析对应的字符串到PermissionResolver权限的实例的
			authorizer.setPermissionResolver(new WildcardPermissionResolver());
			//授权指定给DefaultSecurityManager
			securityManager.setAuthorizer(authorizer);
			securityManager.setRealm(new MyRealm1());
		}else{
			//4.或者使用jdbcTemplate连接数据库
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			
			dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://localhost:3306/t14?characterEncoding=utf-8");		
			dataSource.setUsername("root");
			dataSource.setPassword("admin");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			MyRealm2 myRealm2=new MyRealm2();
			myRealm2.setJdbcTemplate(jdbcTemplate);
			securityManager.setRealm(new MyRealm2());
		}
				
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		System.out.println("是否记住我："+SecurityUtils.getSubject().isRemembered());
		UsernamePasswordToken token = new UsernamePasswordToken(userName,pwd);
		try {		
			subject.login(token);
			// 通过subject获取当前用户的登录状态（ops:从session中同步信息）
			System.out.println("是否登陆或验证:"+subject.isAuthenticated());
	        System.out.println("账户:"+subject.getPrincipal());	       
	        //角色校验
	        System.out.println("是否拥有admin角色:"+subject.hasRole("admin"));
	        //权限校验
	        System.out.println("验证权限："+subject.isPermitted("a:b"));
	        System.out.println("是否拥有user:insert该权限："+subject.isPermitted("user:insert"));
			System.out.println("验证成功");//注意判断是否已登陆
		} catch (ExcessiveAttemptsException eae) {
           System.out.println("用户名或密码错误次数过多");
        } catch (UnknownAccountException e) {
			System.out.println("账户不存在");
		}catch (IncorrectCredentialsException e) {
			System.out.println("密码错误");
		}catch (LockedAccountException e) {
			System.out.println("账户已冻结"+subject.getPrincipal());
		}
		catch(Exception e) {
			System.out.println("未知错误");
			e.printStackTrace();
		}
		 // 登出：身份信息，登录状态信息，权限信息，角色信息，会话信息 全部抹除
        subject.logout();
        System.out.println("登出成功");
	}
	
}
