package winwan.task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameAlreadyExistsException extends RuntimeException{

	public UsernameAlreadyExistsException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

		
}
