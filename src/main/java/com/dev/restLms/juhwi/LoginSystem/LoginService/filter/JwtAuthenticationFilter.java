package com.dev.restLms.juhwi.LoginSystem.LoginService.filter;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.juhwi.LoginSystem.LoginService.repository.LoginUserOwnPermissionGroupRepository;
import com.dev.restLms.juhwi.LoginSystem.LoginService.service.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private LoginUserOwnPermissionGroupRepository loginUserOwnPermissionGroupRepository;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// 요청에서 JWT 토큰을 파싱합니다.
			String token = parseBearerToken(request);
			log.info(request.toString());
			log.info("Filter is running...");
			log.info("토큰 파싱: " + token);

			// 토큰이 유효한 경우
			if (token != null && !token.equalsIgnoreCase("null")) {
				// 토큰에서 사용자 ID를 추출합니다.
				String userId = tokenProvider.getUserIdFromJWT(token);
				log.info("Authenticated user ID: " + userId);

				// 사용자 ID로 권한 그룹을 조회합니다.
				Optional<List<UserOwnPermissionGroup>> permissionGroupsSelect = loginUserOwnPermissionGroupRepository
						.findBySessionId(userId);


				UsernamePasswordAuthenticationToken authentication;
				// 권한 그룹이 존재하는 경우
				if (permissionGroupsSelect.isPresent()) {
					log.info("권한이 존재합니다.");
					List<UserOwnPermissionGroup> permissionGroups = permissionGroupsSelect.get();
					List<SimpleGrantedAuthority> authorities = permissionGroups.stream()
							.map(pg -> new SimpleGrantedAuthority(pg.getPermissionGroupUuid2()))
							.collect(Collectors.toList());
					authentication = new UsernamePasswordAuthenticationToken(userId,
							"무의미한 정보", authorities);
				}
				// 권한 그룹이 존재하지 않는 경우
				else {
					log.info("권한이 존재하지 않습니다.");
					authentication = new UsernamePasswordAuthenticationToken(userId,
							"무의미한 정보",
							Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
				}

				// 사용자 ID와 권한을 포함하여 UsernamePasswordAuthenticationToken 객체를 생성합니다.
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 

				// SecurityContextHolder에 인증 정보를 설정합니다.
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (ExpiredJwtException e) {
			log.info("JWT 토큰이 만료되었습니다.");
		} catch (Exception e) {
			log.info("알 수 없는 오류 : " + e);
		}

		// 다음 필터를 호출합니다.
		filterChain.doFilter(request, response);
	}

	private String parseBearerToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}