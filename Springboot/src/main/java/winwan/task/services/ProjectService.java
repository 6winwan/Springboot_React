package winwan.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import winwan.task.domain.Backlog;
import winwan.task.domain.Project;
import winwan.task.domain.User;
import winwan.task.exceptions.ProjectIdException;
import winwan.task.exceptions.ProjectNotFoundException;
import winwan.task.repositories.BacklogRepository;
import winwan.task.repositories.ProjectRepository;
import winwan.task.repositories.UserRepository;

@Service
public class ProjectService {

	//Inject project repository
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Project saveOrUpdateProject(Project project, String username) {
		
		if(project.getId()!=null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
			
			if(existingProject !=null && (!existingProject.getProjectLeader().equals(username))) {
				throw new ProjectNotFoundException("Project not found in your account");
			} else if(existingProject ==null) {
				throw new ProjectNotFoundException("Project with ID '"+project.getProjectIdentifier()+"' cannot be upadted because project does not exist");
			}
		}
		
		
		// if projectId does not exist save project, not return exception
		try{
			User user = userRepository.findByUsername(username);
			
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if(project.getId()==null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());	
			}
			if(project.getId()!=null) {
				project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
			}
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exist");
		}
	}
	
	
	public Project findProjectByIdentifier(String projectId, String username) {
		
		// get project by projecId
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		// if project is null get exception message
		if(project == null) {
			throw new ProjectIdException("Project does not exist");
		}
		if(!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project not found in your account");
		}
		return project;
	}
	
	
	public Iterable<Project> findAllProjects(String username){
		return projectRepository.findAllByProjectLeader(username);
	}
	
	public void deleteProjectByIdentifier(String projectId, String username) {
		projectRepository.delete(findProjectByIdentifier(projectId, username));
	}
		
}
