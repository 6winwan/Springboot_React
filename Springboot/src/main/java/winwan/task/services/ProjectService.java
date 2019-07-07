package winwan.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import winwan.task.domain.Project;
import winwan.task.exceptions.ProjectIdException;
import winwan.task.repositories.ProjectRepository;

@Service
public class ProjectService {

	//Inject project repository
	@Autowired
	private ProjectRepository projectRepository;
	
	public Project saveOrUpdateProject(Project project) {
		
		// if projectId does not exist save project, not return exception
		try{
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exist");
		}
	}
	
	
	public Project findProjectByIdentifier(String projectId) {
		
		// get project by projecId
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		// if project is null get exception message
		if(project == null) {
			throw new ProjectIdException("Project does not exist");
		}
		return project;
	}
	
	
	public Iterable<Project> findAllProjects(){
		return projectRepository.findAll();
	}
	
	
}
