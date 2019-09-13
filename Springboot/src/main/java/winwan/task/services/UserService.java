package winwan.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import winwan.task.domain.User;
import winwan.task.exceptions.UsernameAlreadyExistsException;
import winwan.task.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	public User saveuser(User newUser) {
		try {
			newUser.setPassword(bcryptPasswordEncoder.encode(newUser.getPassword()));

			//Username has to be unique
			newUser.setUsername(newUser.getUsername());
			//Make sure that password and confirmPassword match
			//we don't persist or show the confirmPassword
			newUser.setConfirmPassword("");
			return userRepository.save(newUser);

		} catch(Exception e) {
			throw new UsernameAlreadyExistsException("Username '"+newUser.getUsername()+"' already exist");
		}
		
	}
	
}
