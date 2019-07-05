package winwan.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import winwan.task.domain.Project;
import winwan.task.repositories.ProjectRepository;

@Service
public class ProjectService {

	//Inject project repository
	@Autowired
	private ProjectRepository projectRepository;
	
	public Project saveOrUpdateProject(Project project) {
		
		return projectRepository.save(project);
	}
}
