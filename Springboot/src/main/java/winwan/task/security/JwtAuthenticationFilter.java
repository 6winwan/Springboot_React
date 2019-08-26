package winwan.task.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import winwan.task.domain.User;
import winwan.task.services.CustomUserDetailsService;

import static winwan.task.security.SecurityContents.HEADER_STRING;
import static winwan.task.security.SecurityContents.TOKEN_PREFIX;



public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest httpServletRequest, 
			HttpServletResponse httpServletResponse, 
			FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String jwt = getJWTfromRequest(httpServletRequest);
			
			if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				Long userId = tokenProvider.getUserIdfromJWT(jwt);
				User userDetails = customUserDetailsService.loadUserById(userId);
				
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(
								userDetails, null, Collections.emptyList());
				
				authentication.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication is security is unclear", ex);
		}
		
		filterChain.doFilter(httpServletRequest, httpServletResponse);
		
	}
	
	private String getJWTfromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(HEADER_STRING);
		
		if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(7,bearerToken.length());
		}
		
		return null;
	}

}
