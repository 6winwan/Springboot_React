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
		
		try{
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exist");
		}
	}
}
