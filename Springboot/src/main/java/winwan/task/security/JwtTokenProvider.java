package winwan.task.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import winwan.task.domain.User;

import static winwan.task.security.SecurityContents.EXPIRATION_TIME;
import static winwan.task.security.SecurityContents.SECRET;

@Component
public class JwtTokenProvider {

	//generate the token
	public String GenerateToken(Authentication authentication) {
		User user = (User)authentication.getPrincipal();
		Date now = new Date(System.currentTimeMillis());
		
		Date expiryDate = new Date (now.getTime()+EXPIRATION_TIME);
		
		String userId = Long.toString(user.getId());
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", (Long.toString(user.getId())));
		claims.put("username", user.getUsername());
		claims.put("fullname", user.getfullName());
		
		return Jwts.builder()
				.setSubject(userId)
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
	}
	
	//validate token
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
			return true;
		} catch(SignatureException ex){
			System.out.println("Invalid JWT Signature");
		} catch(MalformedJwtException ex) {
			System.out.println("Invalid JWT Token");
		} catch(ExpiredJwtException ex) {
			System.out.println("Expired JWT Token");
		} catch(UnsupportedJwtException ex) {
			System.out.println("Unsupported JWT Token");
		} catch(IllegalArgumentException ex) {
			System.out.println("JWT claims string empty");
		}
		return false;
	}
	//get user id from token 
	public Long getUserIdfromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		String id = (String)claims.get("id");
		
		return Long.parseLong(id);
	}
}
