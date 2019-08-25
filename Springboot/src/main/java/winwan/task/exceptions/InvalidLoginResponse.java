package winwan.task.exceptions;

public class InvalidLoginResponse {
	private String username;
	private String passowrd;
	
	public InvalidLoginResponse() {
		this.username = "Invalid Username";
		this.passowrd = "Invalid Password";
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassowrd() {
		return passowrd;
	}
	
	public void setPassowrd(String passowrd) {
		this.passowrd = passowrd;
	}
	
	
	
	
}
