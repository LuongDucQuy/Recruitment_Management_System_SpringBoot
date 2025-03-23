package com.ace.job.recruitment.config;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ace.job.recruitment.entity.User;
import com.ace.job.recruitment.repository.UserRepository;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException {
		String username = authentication.getName(); // Lấy email từ Authentication
		User user = userRepository.findByEmail(username);

		// Kiểm tra nếu người dùng bị cấm (status = false)
		if (user == null || !user.isStatus()) {
			response.sendRedirect("/signin?error");
			return;
		}

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		for (GrantedAuthority authority : authorities) {
			String role = authority.getAuthority();
			if (role.equals("Admin") || role.equals("Default-Admin") || role.equals("Senior-HR") || role.equals("Junior-HR")) {
				response.sendRedirect("/hr/dashboard");
				return;
			} else if (role.equals("Interviewer") || role.equals("Department-head")) {
				response.sendRedirect("/department/dashboard");
				return;
			}
		}

		// Nếu không có vai trò hợp lệ, chuyển hướng đến lỗi
		response.sendRedirect("/signin?error=invalid_role");
	}
}