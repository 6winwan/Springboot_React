package winwan.task.domain.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import winwan.task.domain.User;
import winwan.task.payload.JWTLoginSucessResponse;
import winwan.task.payload.LoginRequest;
import winwan.task.security.JwtTokenProvider;
import winwan.task.services.MapValidationErrorService;
import winwan.task.services.UserService;
import winwan.task.validator.UserValidator;

import static winwan.task.security.SecurityContents.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private MapValidationErrorService mapValidationErrorService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserValidator userValidator;
	
	@Autowired
	private JwtTokenProvider  tokenProvider;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticatUser(@Valid @RequestBody LoginRequest loginRequest,
			BindingResult result) {
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if(errorMap !=null){
			return errorMap;
		}
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(),
				loginRequest.getPassword()
				)
			);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = TOKEN_PREFIX + tokenProvider.GenerateToken(authentication);
		
		return ResponseEntity.ok(new JWTLoginSucessResponse(true, jwt));
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result){
		
		// Validate passwords match
		userValidator.validate(user, result);
		
		ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(result);
		if(errorMap !=null) {
			return errorMap;
		}
		
		User newUser = userService.saveuser(user);
		
		return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
	}
}
